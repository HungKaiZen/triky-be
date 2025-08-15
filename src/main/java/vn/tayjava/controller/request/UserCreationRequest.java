package vn.tayjava.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import vn.tayjava.common.Gender;
import vn.tayjava.validate.EnumValue;
import vn.tayjava.validate.PhoneNumber;
import vn.tayjava.validate.ValidateDate;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
public class UserCreationRequest implements Serializable {
    @NotBlank(message = "firstName must be not blank")
    private String firstName;

    @NotNull(message = "firstName must be not null")
    private String lastName;

    @NotNull(message = "Gender must be not null")
    @EnumValue(name = "gender", enumClass = Gender.class)
    private String gender;

    @ValidateDate
    private String birthday;

    @NotBlank(message = "username must be not blank")
    private String username;

    @Email(message = "email invalid")
    private String email;

    @PhoneNumber(message = "phone invalid format")
    private String phone;

    private List<AddressRequest> addresses; // home, office
}
