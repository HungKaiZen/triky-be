package vn.tayjava.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.tayjava.controller.request.*;
import vn.tayjava.controller.response.DataResponse;
import vn.tayjava.controller.response.TokenResponse;
import vn.tayjava.controller.response.UserResponse;
import vn.tayjava.service.AuthenticationService;
import vn.tayjava.service.UserAvatarService;
import vn.tayjava.service.UserService;

import java.io.IOException;
import java.util.Map;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Authentication Controller")
@Slf4j(topic = "AUTHENTICATION-CONTROLLER")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final UserAvatarService userAvatarService;

    @Operation(summary = "Access token", description = "Get access token and refresh token by username and password")
    @PostMapping("/access")
    public DataResponse<?> getAccessToken(@RequestBody SignInRequest request) {
        log.info("Access token request");
        return new DataResponse<>(HttpStatus.CREATED.value(), "Login successfully", authenticationService.getAccessToken(request));
    }

    @Operation(summary = "Refresh token", description = "Get new access token by refresh")
    @PostMapping("/refresh")
    public TokenResponse getRefreshToken(HttpServletRequest request) {
        log.info("Refresh token request");
        return authenticationService.getRefreshToken(request);
    }

    @Operation(summary = "Create user", description = "API add new user to database")
    @PostMapping("/register")
    public DataResponse<?> addUsers(@RequestBody @Valid UserCreationRequest request) {
        log.info("Create user: {}", request);
        Long userId = userService.save(request);
        return new DataResponse<>(HttpStatus.CREATED.value(), "User created", userId);
    }

    @Operation(summary = "Update user", description = "API update user to database")
    @PutMapping("/upd")
    public DataResponse<?> updateUsers(@RequestBody @Valid UserUpdateRequest request) {
        log.info("Update user: {}", request);

        userService.update(request);
        return new DataResponse<>(HttpStatus.OK.value(), "User updated", null);
    }

    @Operation(summary = "Change password", description = "API change password for user to database")
    @PatchMapping ("/change-pwd")
    public DataResponse<?> changePassword(@RequestBody @Valid UserPasswordRequest userPasswordRequest) {
        log.info("Change password: {}", userPasswordRequest);

        userService.changePassword(userPasswordRequest);
        return new DataResponse<>(HttpStatus.OK.value(), "User password changed", null);
    }

    @Operation(summary = "Update user status", description = "API update user status for user to database")
    @PatchMapping("/{id}/upd-status")
    public DataResponse<?> updateUserStatus(@PathVariable("id") long id, @RequestParam("status") String status) {
        log.info("Update user status for id: {}, {}", status, id);
        userService.updateUserStatus(id, status);
        return new DataResponse<>(HttpStatus.OK.value(), "User status updated", null);
    }

    @Operation(summary = "Delete user", description = "API inactivate user from database")
    @DeleteMapping ("/del/{userId}")
    public DataResponse<?> deleteUser(@PathVariable @Min(value = 1, message = "userId must be greater than 0") Long userId) {
        log.info("Deleting user: {}", userId);
        userService.deleteUser(userId);
        return new DataResponse<>(HttpStatus.OK.value(), "User deleted", null);
    }

    @Operation(summary = "Get user detail", description = "API retrieve user detail by id from database")
    @GetMapping("/{userId}")
    public DataResponse<?> getUsers(@PathVariable @Min(value = 1, message = "userId must be greater than 0") Long userId) {
        log.info("Get user detail by id: {}", userId);

        UserResponse userResponse = userService.getUserById(userId);

        return new DataResponse<>(HttpStatus.OK.value(), "User detail", userResponse);
    }

    @Operation(summary = "Verify email and set password", description = "API verify email and set password to db")
    @PostMapping("/verify-and-set-password")
    public DataResponse<?> verifyEmailAndSetPassword(@RequestBody RegisterStep2Request request) {
       log.info("Verify email and password request");
       return new DataResponse<>(HttpStatus.ACCEPTED.value(), "Verify email and set password successfully", authenticationService.verifyEmailAndSetPassword(request));
    }


//    @GetMapping("/{userId}/avatar/all")
//    public DataResponse<?> getUserAvatars(@PathVariable @Min(value = 1, message = "userId must be greater than 0") Long userId) {
//        log.info("Get user avatars: {}", userId);
//        return new DataResponse<>(HttpStatus.OK.value(), "User avatars", userAvatarService.getUserAvatars(userId));
//    }
//
//    @PostMapping("/{userId}/avatar/upload")
//    public DataResponse<?> uploadUserAvatar(@PathVariable Long userId, @RequestParam MultipartFile file) {
//        log.info("Upload user avatar: {}", userId);
//        try {
//            return new DataResponse<>(HttpStatus.OK.value(), "Upload success", userAvatarService.uploadAvatar(userId, file).getAvatarUrl());
//        }catch (Exception e) {
//            return new DataResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Upload failed");
//        }
//    }
//
//    @PutMapping("/{userId}/avatar/select")
//    public DataResponse<?> selectUserAvatar(@PathVariable Long userId, @RequestBody Map<String, String> body) {
//        log.info("Select user avatar: {}", userId);
//        String avatarUrl = body.get("avatarUrl");
//        userAvatarService.selectAvatar(userId, avatarUrl);
//        return new DataResponse<>(HttpStatus.OK.value(), "Avatar selected successfully", avatarUrl);
//    }


}
