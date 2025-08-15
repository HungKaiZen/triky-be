package vn.tayjava.controller.request;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class SignInRequest implements Serializable {
    private String username;
    private String password;
    private String platform;  // web or mobile
    private String deviceToken;
    private String versionUp;
}
