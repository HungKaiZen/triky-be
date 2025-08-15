package vn.tayjava.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.tayjava.model.UserEntity;
import vn.tayjava.model.VerificationToken;
import vn.tayjava.repository.VerificationTokenRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j(topic = "VERIFICATION TOKEN SERVICE")
@RequiredArgsConstructor
public class VerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;

    public VerificationToken createToken(UserEntity user) {
        String token = UUID.randomUUID().toString();
        VerificationToken vt = new VerificationToken();
        vt.setToken(token);
        vt.setUser(user);
        vt.setExpiryTime(LocalDate.now().atStartOfDay().plusHours(24));
        return verificationTokenRepository.save(vt);
    }

    public Optional<VerificationToken> findByToken(String token) {
        return verificationTokenRepository.findByToken(token);
    }

   public Optional<UserEntity> findUserByToken(String token) {
        return verificationTokenRepository.findUserByToken(token);
   }

   @Transactional
   public void deleteByToken(String token) {
        verificationTokenRepository.deleteByToken(token);
   }

}
