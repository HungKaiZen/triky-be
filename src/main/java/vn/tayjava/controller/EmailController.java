package vn.tayjava.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vn.tayjava.controller.response.DataResponse;
import vn.tayjava.service.EmailService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@RestController
@RequiredArgsConstructor
@Slf4j(topic = "EMAIL CONTROLLER")
public class EmailController {
    private final EmailService emailService;

    @PostMapping("/send-email")
    public DataResponse<?> sendEmail(@RequestParam String to, @RequestParam String subject, @RequestParam String body, @RequestParam(required = false) MultipartFile[] files) {
        log.info("Sending email to " + to);
        try {
            return new DataResponse<>(HttpStatus.ACCEPTED.value(), "Email sent successfully", emailService.sendEmail(to, subject, body, files));
        } catch (Exception e) {
            return new DataResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }
}
