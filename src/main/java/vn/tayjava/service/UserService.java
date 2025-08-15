package vn.tayjava.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.tayjava.controller.request.UserCreationRequest;
import vn.tayjava.controller.request.UserPasswordRequest;
import vn.tayjava.controller.request.UserUpdateRequest;
import vn.tayjava.controller.response.UserPageResponse;
import vn.tayjava.controller.response.UserResponse;
import vn.tayjava.model.UserEntity;

import java.util.List;

public interface UserService {

    UserPageResponse getAllUsers(String keyword, String sort, int page, int pageSize);

    UserResponse getUserByUsername(String userName);

    UserResponse getUserById(Long id);

    UserResponse getUserByEmail(String email);

    long save(UserCreationRequest req);

    void update(UserUpdateRequest req);

    void updateUserStatus(Long id, String status);

    void updateUserType(Long id, String type);

    void changePassword(UserPasswordRequest req);

    void deleteUser(Long id);



}
