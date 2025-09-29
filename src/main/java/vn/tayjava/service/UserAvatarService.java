package vn.tayjava.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.tayjava.model.UserAvatar;
import vn.tayjava.model.UserEntity;
import vn.tayjava.repository.UserAvatarRepository;
import vn.tayjava.repository.UserRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@Slf4j(topic = "USER AVATAR SERVICE")
@RequiredArgsConstructor
public class UserAvatarService {
    private final UserAvatarRepository userAvatarRepository;
    private final UserRepository userRepository;

    // upload new image
    public UserAvatar uploadAvatar(Long userId, MultipartFile file) throws IOException {
        String uploadDir = "E:/Workspace/Backend/hungjava/fullstack/triky/triky_be/src/main/resources/static/uploads/avatar/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        file.transferTo(filePath.toFile());

        // Lưu vào bảng history
        UserAvatar userAvatar = new UserAvatar();
        userAvatar.setUserId(userId);
        userAvatar.setAvatarUrl("/uploads/avatar/" + fileName);

        userAvatarRepository.save(userAvatar);

        // Cập nhật avatar hiện tại của user
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setAvatarUrl(userAvatar.getAvatarUrl());
        userRepository.save(user);
        return userAvatar;
    }

    // chon avatar cu
    public UserAvatar selectAvatar(Long userId, String avatarUrl) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);

        // Kiểm tra xem avatar đã có trong history chưa
        UserAvatar userAvatar = userAvatarRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .filter(a -> a.getAvatarUrl().equals(avatarUrl))
                .findFirst()
                .orElseGet(() -> {
                    // Nếu chưa có, tạo mới và lưu
                    UserAvatar newUserAvatar = new UserAvatar();
                    newUserAvatar.setUserId(userId);
                    newUserAvatar.setAvatarUrl(avatarUrl);
                    return userAvatarRepository.save(newUserAvatar);

                });
        return userAvatar;
    }

    public List<String> getUserAvatars(Long userId) {
        return userAvatarRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(UserAvatar::getAvatarUrl)
                .toList();
    }


}
