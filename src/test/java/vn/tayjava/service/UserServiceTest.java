package vn.tayjava.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import vn.tayjava.common.Gender;
import vn.tayjava.common.UserStatus;
import vn.tayjava.common.UserType;
import vn.tayjava.controller.response.UserPageResponse;
import vn.tayjava.controller.response.UserResponse;
import vn.tayjava.exception.ResourceNotFoundException;
import vn.tayjava.model.UserEntity;
import vn.tayjava.repository.AddressRepository;
import vn.tayjava.repository.UserRepository;
import vn.tayjava.service.impl.UserServiceImpl;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserService userService;

    private @Mock UserRepository userRepository;
    private @Mock AddressRepository addressRepository;
    private @Mock PasswordEncoder passwordEncoder;
    private @Mock EmailService emailService;
    private @Mock VerificationTokenService verificationTokenService;

    private static UserEntity tayJava;
    private static UserEntity johnDoe;
    @BeforeAll
    static void  beforeAll() {
        tayJava = new UserEntity();
        tayJava.setId(1L);
        tayJava.setFirstName("Tay");
        tayJava.setLastName("Java");
        tayJava.setGender(String.valueOf(Gender.MALE));
        tayJava.setBirthday(new Date());
        tayJava.setEmail("tayjava@gmail.com");
        tayJava.setPhone("123456789");
        tayJava.setUsername("tayjava");
        tayJava.setPassword("password");
        tayJava.setType(String.valueOf(UserType.USER));
        tayJava.setStatus(String.valueOf(UserStatus.ACTIVE));

        johnDoe = new UserEntity();
        johnDoe.setId(1L);
        johnDoe.setFirstName("John");
        johnDoe.setLastName("Doe");
        johnDoe.setGender(String.valueOf(Gender.MALE));
        johnDoe.setBirthday(new Date());
        johnDoe.setEmail("johndoe@gmail.com");
        johnDoe.setPhone("123456789");
        johnDoe.setUsername("johndoe");
        johnDoe.setPassword("password");
        johnDoe.setType(String.valueOf(UserType.USER));
        johnDoe.setStatus(String.valueOf(UserStatus.ACTIVE));

    }

    @BeforeEach
    void setUp() {
        // khoi tao buoc trien khai la UserService
        userService = new UserServiceImpl(userRepository, addressRepository, passwordEncoder, emailService, verificationTokenService);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAllUsers() {
        // gia lap phuong thuc
        Page<UserEntity> usePage = new PageImpl<>(Arrays.asList(tayJava, johnDoe));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(usePage);


        // goi phuong thuc can test
        UserPageResponse result = userService.getAllUsers(null, null, 0, 20);
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
    }


    @Test
    void getAllUsersTest() {
        // gia lap phuong thuc
        Page<UserEntity> usePage = new PageImpl<>(Arrays.asList(tayJava, johnDoe));
        when(userRepository.searchByKeyword(any(), any(Pageable.class))).thenReturn(usePage);
        UserPageResponse result = userService.getAllUsers("tay", null, 0, 20);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
    }


    @Test
    void getAllUsersTest_Empty() {
        // gia lap phuong thuc
        Page<UserEntity> usePage = new PageImpl<>(List.of());
        when(userRepository.findAll(any(Pageable.class))).thenReturn(usePage);

        UserPageResponse result = userService.getAllUsers(null, null, 0, 20);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void getUserByUsername() {
    }

    @Test
    void testGetUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(tayJava));

        UserResponse userResponse = userService.getUserById(1L);

        assertNotNull(userResponse);
        assertEquals(tayJava.getId(), userResponse.getId());
    }

    @Test
    void testGetUserById_Failure() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getUserByEmail() {
    }

    @Test
    void save() {

    }

    @Test
    void update() {
    }

    @Test
    void changePassword() {
    }

    @Test
    void deleteUser() {
    }
}