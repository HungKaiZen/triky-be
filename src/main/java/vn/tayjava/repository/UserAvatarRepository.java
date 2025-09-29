package vn.tayjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.tayjava.model.UserAvatar;

import java.util.List;

public interface UserAvatarRepository extends JpaRepository<UserAvatar, Long> {

    List<UserAvatar> findByUserIdOrderByCreatedAtDesc(Long userId);
}
