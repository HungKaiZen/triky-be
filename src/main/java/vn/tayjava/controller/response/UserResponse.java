package vn.tayjava.controller.response;

import lombok.*;
import vn.tayjava.common.Gender;
import vn.tayjava.model.AddressEntity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String gender;
    private String birthday;
    private String username;
    private String email;
    private String phone;
    private String avatarUrl;
    private Date created;
    private List<AddressEntity> addresses;
    // more
}
