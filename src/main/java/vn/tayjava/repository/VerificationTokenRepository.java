package vn.tayjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.tayjava.model.UserEntity;
import vn.tayjava.model.VerificationToken;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);

    @Query("SELECT v.user FROM VerificationToken v WHERE v.token = :token")
    Optional<UserEntity> findUserByToken(String token);

    void deleteByToken(String token);
}
