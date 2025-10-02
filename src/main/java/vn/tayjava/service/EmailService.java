package vn.tayjava.service;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.tayjava.model.UserEntity;

import java.io.UnsupportedEncodingException;
import java.util.Objects;


@Service
@Slf4j(topic = "EMAIL SERVICE")
@RequiredArgsConstructor
public class EmailService {

    @Autowired
    private final JavaMailSender mailSender;





    public String sendEmail(String recipients, String subject, String content, MultipartFile[] files) throws UnsupportedEncodingException, MessagingException {
        log.info("Email is sending ...");

        String verifyUrl = "";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom("HungJava", "Hung Java");

        if (recipients.contains(",")) { // send to multiple users
            helper.setTo(InternetAddress.parse(recipients));
        } else { // send to single user
            helper.setTo(recipients);
        }

        // Send attach files
        if (files != null) {
            for (MultipartFile file : files) {
                helper.addAttachment(Objects.requireNonNull(file.getOriginalFilename()), file);
            }
        }

        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);

        log.info("Email has sent to successfully, recipients: {}", recipients);

        return "Sent";
    }

//    public void sendVerificationEmail(UserEntity user, String token) {
//       String verifyUrl = "http://localhost:8080/auth/verify?token=" + token;
//       SimpleMailMessage message = new SimpleMailMessage();
//       message.setTo(user.getEmail());
//       message.setSubject("Verification Email");
//       message.setText("Nhấn vào link để kích hoạt: " + verifyUrl);
//        mailSender.send(message);
//    }

    public void sendVerificationEmail(UserEntity user, String token) {
        log.info("Email is sending ...");
//        String verifyUrl = "http://localhost:8080/auth/verify?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Verification Email");
        message.setText("Verify code: " + token);
        mailSender.send(message);
    }











}
