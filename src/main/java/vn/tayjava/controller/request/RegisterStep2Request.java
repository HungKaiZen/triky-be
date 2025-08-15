package vn.tayjava.controller.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterStep2Request {
    private String token;
    private String password;
    private String confirmPassword;
}
