package vn.tayjava.controller.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyEmailRequest {
    private String token;
}
