package vn.tayjava.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.tayjava.common.TokenType;
import vn.tayjava.exception.ResponseError;
import vn.tayjava.service.JwtService;
import vn.tayjava.service.UserServiceDetail;

import java.io.IOException;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "CUSTOMIZE-REQUEST-FILTER")
//@EnableGlobalAuthentication
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class CustomizeRequestFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserServiceDetail userServiceDetail;

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        log.info("{} {}", request.getMethod(), request.getRequestURI());

        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        resp.setHeader("Pragma", "no-cache"); // HTTP 1.0
        resp.setDateHeader("Expires", 0); // Proxies


        String pathIgnoring = request.getServletPath();
        if (pathIgnoring.startsWith("/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String pathRequest = request.getRequestURI();

        // Bỏ qua request tới thư mục avatar / uploads
        if (pathRequest.startsWith("/avatars/") || pathRequest.startsWith("/uploads/")) {
            filterChain.doFilter(request, response);
            return;
        }


        // get token
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            String username = "";
            try {
                username = jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);
                log.info("username {}", username);
            } catch (Exception e) {
                log.info(e.getMessage());
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                String path = request.getRequestURI();
                response.getWriter().write(errorResponse(e.getMessage(), path));
                return;
            }

            UserDetails userDetails = userServiceDetail.userDetailsService().loadUserByUsername(username);
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            UsernamePasswordAuthenticationToken  authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetails(request));
            securityContext.setAuthentication(authenticationToken);
            SecurityContextHolder.setContext(securityContext);

            filterChain.doFilter(request, response);


        }else {
            filterChain.doFilter(request, response);
        }
    }

    /**
     * Create error response with pretty template
     * @param message
     * @param path
     * @return
     */
    private String errorResponse(String message, String path) {
        ResponseError response = new ResponseError();
        try {
            response.setTimestamp(new Date());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setPath(path);
            response.setMessage(message);
            response.setError("FORBIDDEN");
        } catch (Exception e) {
            return ""; // Return an empty string if serialization fails
        }
        return gson.toJson(response);
    }



}
