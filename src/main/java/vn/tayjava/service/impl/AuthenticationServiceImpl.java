package vn.tayjava.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.tayjava.common.TokenType;
import vn.tayjava.common.UserStatus;
import vn.tayjava.controller.request.RegisterStep2Request;
import vn.tayjava.controller.request.SignInRequest;
import vn.tayjava.controller.response.TokenResponse;
import vn.tayjava.model.UserEntity;
import vn.tayjava.model.VerificationToken;
import vn.tayjava.repository.UserRepository;
import vn.tayjava.repository.VerificationTokenRepository;
import vn.tayjava.service.AuthenticationService;
import vn.tayjava.service.JwtService;
import vn.tayjava.service.VerificationTokenService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AUTHENTICATION-SERVICE")
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final VerificationTokenService verificationTokenService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public TokenResponse getAccessToken(SignInRequest request) {
        log.info("Get access token");

        List<String> authorities = new ArrayList<>();
        try {
            Authentication authentication =  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            authorities.add(authentication.getAuthorities().toString());
            log.info("isAuthenticated = {}", authentication.isAuthenticated());
            log.info("Authorities: {}", authentication.getAuthorities().toString());
            // Nếu xác thực thành công, lưu thông tin vào SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (BadCredentialsException | DisabledException e) {
            log.info("login fail, message={}", e.getMessage());
            throw new BadCredentialsException(e.getMessage());
        }

        String accessToken = jwtService.generateAccessToken(request.getUsername(), authorities);
        String refreshToken = jwtService.generateRefreshToken(request.getUsername(), authorities);
        UserEntity user = userRepository.findByUsername(request.getUsername());
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .avatarUrl(user.getAvatarUrl())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .username(user.getUsername())
                .build();
    }

    @Override
    public TokenResponse getRefreshToken(HttpServletRequest request) {
        log.info("Get Refresh Token");

        final String refreshToken = request.getHeader("REFERER");
        if(!StringUtils.hasLength(refreshToken)) {
            throw new IllegalArgumentException("Token must be not blank");
        }

        try {
            // verify  token
            String username = jwtService.extractUsername(refreshToken, TokenType.REFRESH_TOKEN);

            // check user is active or inactive
            UserEntity userEntity = userRepository.findByUsername(username);

            List<String> authorities = new ArrayList<>();
            userEntity.getAuthorities().forEach(authority -> authorities.add(authority.getAuthority()));

            // generate new access token
            String accessToken = jwtService.generateAccessToken(username, authorities);

            return TokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (Exception e) {
            log.error("Access denied! errorMessage: {}", e.getMessage());
            throw new AccessDeniedException(e.getMessage());
        }
    }


    @Override
    public String verifyEmailAndSetPassword(RegisterStep2Request request) {
        log.info("Verify email and password");

        Optional<VerificationToken> token  = verificationTokenService.findByToken(request.getToken());

        if (token.isEmpty()) {
            return "invalid token";
        }

        if (token.get().getExpiryTime().isBefore(LocalDateTime.now())) {
            return "token expired";
        }

        UserEntity user = verificationTokenService.findUserByToken(request.getToken()).get();
        log.info(user.getStatus());

        if(user.getStatus().equalsIgnoreCase(String.valueOf(UserStatus.ACTIVE))) {
            return "user already enabled";
        }

        if(!request.getPassword().equals(request.getConfirmPassword())) {
            return "password mismatch";
        }

        user.setStatus(String.valueOf(UserStatus.ACTIVE).toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        verificationTokenService.deleteByToken(request.getToken());
        return "password saved";
    }


}
