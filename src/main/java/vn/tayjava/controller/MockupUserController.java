package vn.tayjava.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vn.tayjava.common.Gender;
import vn.tayjava.controller.request.UserPasswordRequest;
import vn.tayjava.controller.request.UserCreationRequest;
import vn.tayjava.controller.request.UserUpdateRequest;
import vn.tayjava.controller.response.UserResponse;

import java.util.*;

@RestController
@RequestMapping("/mockup/user")
@Tag(name = "Mockup User Controller")
public class MockupUserController {

    @Operation(summary = "Get user list", description = "API retrieve user from database")
    @GetMapping("/list")
    public Map<String, Object> getUsers(@RequestParam(required = false) String keyword,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "20") int size) {

        UserResponse response = new UserResponse();
        response.setId(1L);
        response.setUsername("admin");
        response.setFirstName("Leo");
        response.setLastName("Messi");
        response.setEmail("admin@gmail.com");
        response.setBirthday(String.valueOf(new Date()));
        response.setPhone("0989123456");
        response.setGender(String.valueOf(Gender.MALE));


        UserResponse response2 = new UserResponse();
        response2.setId(2L);
        response2.setUsername("admin");
        response2.setFirstName("Cristiano");
        response2.setLastName("Ronaldo");
        response2.setEmail("admin@gmail.com");
        response2.setBirthday(String.valueOf(new Date()));
        response2.setPhone("0989123456");
        response.setGender(String.valueOf(Gender.MALE));

        List<UserResponse> users = Arrays.asList(response,response2);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "user list");
        result.put("data", users);
        return result;
    }


    @Operation(summary = "Get user detail", description = "API retrieve user detail by id from database")
    @GetMapping("/{userId}")
    public Map<String, Object> getUsers(@PathVariable Long userId) {

        UserResponse response = new UserResponse();
        response.setId(userId);
        response.setUsername("admin");
        response.setFirstName("Leo");
        response.setLastName("Messi");
        response.setEmail("admin@gmail.com");
        response.setBirthday(String.valueOf(new Date()));
        response.setPhone("0989123456");
        response.setGender(String.valueOf(Gender.MALE));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "user detail");
        result.put("data", response);
        return result;

    }


    @Operation(summary = "Create user", description = "API add new user to database")
    @PostMapping("/add")
    public Map<String, Object> addUsers( UserCreationRequest userCreationRequest) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "add user successfully");
        result.put("data", 3);
        return result;

    }

    @Operation(summary = "Update user", description = "API update user to database")
    @PutMapping("/upt")
    public Map<String, Object> updateUsers( UserUpdateRequest userUpdateRequest) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.ACCEPTED.value());
        result.put("message", "update user successfully");
        result.put("data", 3);
        return result;

    }

    @Operation(summary = "Change password", description = "API change password for user to database")
    @PatchMapping ("/change-pwd")
    public Map<String, Object> changePassword( UserPasswordRequest userPasswordRequest) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.NO_CONTENT.value());
        result.put("message", "password changed successfully");
        result.put("data", "");
        return result;

    }


    @Operation(summary = "Delete user", description = "API inactivate user from database")
    @PatchMapping ("/del/{userId}")
    public Map<String, Object> deleteUser(@PathVariable Long userId) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.RESET_CONTENT.value());
        result.put("message", "User deleted successfully");
        result.put("data", "");
        return result;

    }



}
