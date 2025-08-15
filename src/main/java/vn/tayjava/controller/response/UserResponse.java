package vn.tayjava.controller.response;

import lombok.*;
import vn.tayjava.common.Gender;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private String birthday;
    private String username;
    private String email;
    private String phone;
    // more
}
