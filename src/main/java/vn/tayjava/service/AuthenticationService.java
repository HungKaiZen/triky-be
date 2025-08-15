package vn.tayjava.service;

import jakarta.servlet.http.HttpServletRequest;
import vn.tayjava.controller.request.RegisterStep2Request;
import vn.tayjava.controller.request.SignInRequest;
import vn.tayjava.controller.response.TokenResponse;


public interface AuthenticationService {


    TokenResponse getAccessToken(SignInRequest request);

    TokenResponse getRefreshToken(HttpServletRequest request);

    boolean verifyAccount(String token);

    String verifyEmailAndSetPassword(RegisterStep2Request request);

    public Long getUserIdFromToken(String token);
}
