package vn.tayjava.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import vn.tayjava.repository.UserRepository;

@Service
@Slf4j(topic = "USER SERVICE DETAIL")
@RequiredArgsConstructor
public class UserServiceDetail {
    private final UserRepository userRepository;

    public UserDetailsService userDetailsService() {
        return userRepository::findByUsername;
    }
}
