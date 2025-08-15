package vn.tayjava.controller.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import vn.tayjava.common.Gender;

import java.util.Date;
import java.util.List;

@Getter
public class UserUpdateRequest {
    @NotNull(message = "firstName must be not null")
    @Min(value = 1, message = "id must be greater than 0")
    private long id;
    @NotBlank(message = "firstName must be not blank")
    private String firstName;

    @NotNull(message = "lastName must be not null")
    private String lastName;
    private String gender;
    private String birthday;
    private String username;
    private String email;
    private String phone;

    private List<AddressRequest> addresses;
}
