package vn.tayjava.controller.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UserPasswordRequest {
    @NotNull(message = "id must be not null")
    @Min(value = 1, message = "id must be greater than 0")
    private Long id;
    @NotBlank(message = "password must be not blank")
    private String password;
    @NotBlank(message = "confirmPassword must be not blank")
    private String confirmPassword;
}
