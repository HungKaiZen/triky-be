package vn.tayjava.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.tayjava.controller.request.UserPasswordRequest;
import vn.tayjava.controller.request.UserCreationRequest;
import vn.tayjava.controller.request.UserUpdateRequest;
import vn.tayjava.controller.response.DataResponse;
import vn.tayjava.controller.response.UserPageResponse;
import vn.tayjava.controller.response.UserResponse;
import vn.tayjava.service.UserService;

import java.io.IOException;
import java.util.*;


@RestController
@RequestMapping("/user")
@Tag(name = "User Controller")
@Slf4j(topic = "USER-CONTROLLER")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get user list", description = "API retrieve user from database")
    @GetMapping("/list")
    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    public DataResponse<?> getUsers(@RequestParam(required = false) String keyword,
                                 @RequestParam(required = false) String sort,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "20") int size) {

        UserPageResponse userPageResponse = userService.getAllUsers(keyword, sort, page, size);
        return new DataResponse<>(HttpStatus.OK.value(), "User list", userPageResponse);
    }


    @Operation(summary = "Get user detail", description = "API retrieve user detail by id from database")
    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('user')")
    public DataResponse<?> getUsers(@PathVariable @Min(value = 1, message = "userId must be greater than 0") Long userId) {
        log.info("Get user detail by id: {}", userId);

        UserResponse userResponse = userService.getUserById(userId);

        return new DataResponse<>(HttpStatus.OK.value(), "User detail", userResponse);
    }


    @Operation(summary = "Create user", description = "API add new user to database")
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('admin')")
    public DataResponse<?> addUsers(@RequestBody @Valid UserCreationRequest request) {
        log.info("Create user: {}", request);
        Long userId = userService.save(request);
        return new DataResponse<>(HttpStatus.CREATED.value(), "User created", userId);
    }

    @Operation(summary = "Update user", description = "API update user to database")
    @PutMapping("/upd")
    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    public DataResponse<?> updateUsers(@RequestBody @Valid UserUpdateRequest request) {
        log.info("Update user: {}", request);

        userService.update(request);
        return new DataResponse<>(HttpStatus.OK.value(), "User updated", null);
    }

    @Operation(summary = "Change password", description = "API change password for user to database")
    @PatchMapping ("/change-pwd")
    @PreAuthorize("hasAnyAuthority('user', 'manager', 'admin')")
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

    @Operation(summary = "Update user type", description = "API update user type for user to database")
    @PatchMapping("/{id}/upd-type")
    public DataResponse<?> updateUserType(@PathVariable("id") long id, @RequestParam("type") String type) {
        log.info("Update user type for id: {}, {}", id, type);
        userService.updateUserType(id, type);
        return new DataResponse<>(HttpStatus.OK.value(), "User type updated", null);
    }


    @Operation(summary = "Delete user", description = "API inactivate user from database")
    @DeleteMapping ("/del/{userId}")
    @PreAuthorize("hasAuthority('admin')")
    public DataResponse<?> deleteUser(@PathVariable @Min(value = 1, message = "userId must be greater than 0") Long userId) {
        log.info("Deleting user: {}", userId);
        userService.deleteUser(userId);
        return new DataResponse<>(HttpStatus.OK.value(), "User deleted", null);
    }

    @Operation(summary = "Confirm email", description = "API confirm email when register")
    @GetMapping("/confirm-email")
    public void confirmEmail(@RequestParam String secretCode, HttpServletResponse response) {
        log.info("Confirm email: {}", secretCode);
        try {
            // TODO checked or compare secretCode from database
        }catch (Exception e) {
            log.error("Confirm email was failure, errorMessage={}", e.getMessage());
        }finally {
            try {
                response.sendRedirect("https://google.com");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
