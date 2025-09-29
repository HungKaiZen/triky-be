package vn.tayjava.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.tayjava.common.Gender;
import vn.tayjava.common.UserStatus;
import vn.tayjava.common.UserType;
import vn.tayjava.controller.request.UserCreationRequest;
import vn.tayjava.controller.request.UserPasswordRequest;
import vn.tayjava.controller.request.UserUpdateRequest;
import vn.tayjava.controller.response.UserPageResponse;
import vn.tayjava.controller.response.UserResponse;
import vn.tayjava.exception.ResourceNotFoundException;
import vn.tayjava.model.AddressEntity;
import vn.tayjava.model.UserEntity;
import vn.tayjava.model.VerificationToken;
import vn.tayjava.repository.AddressRepository;
import vn.tayjava.repository.UserRepository;
import vn.tayjava.service.EmailService;
import vn.tayjava.service.UserService;
import vn.tayjava.service.VerificationTokenService;
import vn.tayjava.util.DateUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j(topic = "USER-SERVICE")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/uuuu");
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final VerificationTokenService verificationTokenService;

    @Value("${app.default-avatar}")
    private String defaultAvatar;


    @Override
    public UserPageResponse getAllUsers(String search, String sort, int page, int size) {

        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "id");
        if(!StringUtils.isBlank(sort)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sort);
            if(matcher.find()) {
                String column = matcher.group(1);
                if(matcher.group(3).equalsIgnoreCase("asc")) {
                    order = new Sort.Order(Sort.Direction.ASC, column);
                }else if(matcher.group(3).equalsIgnoreCase("desc")) {
                    order = new Sort.Order(Sort.Direction.DESC, column);
                }
            }
        }

        // handle when FE start page = 1 because sql start page = 0
        int pageNo = 0;
        if(page > 0) {
            pageNo = page - 1;
        }
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by(order));
        
        Page<UserEntity>  userEntityPage;

        if(!StringUtils.isBlank(search)) {
            search = "%" + search.toLowerCase() + "%";
            userEntityPage = userRepository.searchByKeyword(search, pageable);
        }else {
            userEntityPage = userRepository.findAll(pageable);
        }

        return getUserPageResponse(page, size, userEntityPage);
    }

    @Override
    public UserResponse getUserByUsername(String userName) {
        return null;
    }

    /*
    * Get user by id
    * */
    @Override
    public UserResponse getUserById(Long id) {
        log.info("Get user detail by id: {}", id);

        UserEntity userEntity = getUserEntityById(id);
        List<AddressEntity> addressEntities = addressRepository.findByUserId(userEntity.getId());

        return UserResponse.builder()
                .id(id)
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .gender(userEntity.getGender())
                .birthday(String.valueOf(userEntity.getBirthday()))
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .phone(userEntity.getPhone())
                .avatarUrl(userEntity.getAvatarUrl())
                .created(userEntity.getCreatedAt())
                .addresses(addressEntities)
                .build();
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        return null;
    }

    /*
    * Create new user, save address to db
    * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long save(UserCreationRequest req) {
        log.info("Saving user: {}", req);
        UserEntity user = new UserEntity();
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setGender(req.getGender());

        LocalDate localDate = LocalDate.parse(req.getBirthday(), FORMATTER);
        java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
        user.setBirthday(sqlDate);

        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setUsername(req.getUsername());
        user.setAvatarUrl(defaultAvatar);
        user.setType(String.valueOf(UserType.USER));
        user.setStatus("NONE");
        userRepository.save(user);
        log.info("Saved user: {}", user);

        if(user.getId() != null) {
            List<AddressEntity> addresses = new ArrayList<>();
            req.getAddresses().forEach(address -> {
                AddressEntity addressEntity = new AddressEntity();
                addressEntity.setApartmentNumber(address.getApartmentNumber());
                addressEntity.setFloor(address.getFloor());
                addressEntity.setBuilding(address.getBuilding());
                addressEntity.setStreetNumber(address.getStreetNumber());
                addressEntity.setStreet(address.getStreet());
                addressEntity.setCity(address.getCity());
                addressEntity.setCountry(address.getCountry());
                addressEntity.setAddressType(address.getAddressType());
                addressEntity.setUserId(user.getId());
                addresses.add(addressEntity);
            });
            addressRepository.saveAll(addresses);
            log.info("Saved addresses: {}", addresses);
        }

        // send email
        VerificationToken verificationToken = verificationTokenService.createToken(user);
        emailService.sendVerificationEmail(user, verificationToken.getToken());
        return user.getId();
    }

    /*
     * Update user, save new addresses or update addresses to db
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UserUpdateRequest req) {
        log.info("Updating user: {}", req);
        // get user by id
        UserEntity userEntity = getUserEntityById(req.getId());
        userEntity.setFirstName(req.getFirstName());
        userEntity.setLastName(req.getLastName());
        userEntity.setGender(req.getGender());

        // parse String thành LocalDate
        LocalDate localDate = LocalDate.parse(req.getBirthday(), FORMATTER);
        // chuyển LocalDate sang java.sql.Date
        java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
        userEntity.setBirthday(sqlDate);
        userEntity.setEmail(req.getEmail());
        userEntity.setPhone(req.getPhone());
        userEntity.setUsername(req.getUsername());
        userRepository.save(userEntity);
        log.info("Updated user: {}", userEntity);

        List<AddressEntity> addresses = new ArrayList<>();
        req.getAddresses().forEach(address -> {
            AddressEntity addressEntity = addressRepository.findByUserIdAndAddressType(userEntity.getId(), address.getAddressType());
            if(addressEntity == null) {
                addressEntity = new AddressEntity();
            }
            addressEntity.setApartmentNumber(address.getApartmentNumber());
            addressEntity.setFloor(address.getFloor());
            addressEntity.setBuilding(address.getBuilding());
            addressEntity.setStreetNumber(address.getStreetNumber());
            addressEntity.setStreet(address.getStreet());
            addressEntity.setCity(address.getCity());
            addressEntity.setCountry(address.getCountry());
            addressEntity.setAddressType(address.getAddressType());
            addressEntity.setUserId(userEntity.getId());

            addresses.add(addressEntity);
            log.info("Updated address: {}", addressEntity);
        });

        addressRepository.saveAll(addresses);
    }

    /*
     * Update user status
     * */
    @Override
    public void updateUserStatus(Long id, String status) {
        UserEntity userEntity = getUserEntityById(id);
        userEntity.setStatus(status);
        userRepository.save(userEntity);
    }

    /*
     * Update user type
     * */
    @Override
    public void updateUserType(Long id, String type) {
        UserEntity userEntity = getUserEntityById(id);
        userEntity.setType(type);
        userRepository.save(userEntity);
    }


    /*
    * Change password
    * encoder
    * */
    @Override
    public void changePassword(UserPasswordRequest req) {
        log.info("Changing password for user: {}", req);
        UserEntity userEntity = getUserEntityById(req.getId());

        if(!req.getPassword().equals(req.getConfirmPassword())) {
            throw new IllegalArgumentException("Password does not match");
        }
        userEntity.setPassword(passwordEncoder.encode(req.getPassword()));
        userRepository.save(userEntity);
        log.info("Changed password for user: {}", userEntity);
    }

    /*
    * Delete user => Update user status is inactive
    * */
    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user: {}", id);
        UserEntity userEntity = getUserEntityById(id);
        userEntity.setStatus(String.valueOf(UserStatus.INACTIVE));
        userRepository.save(userEntity);
        log.info("Deleted user: {}", userEntity);

    }


    /**
    *   get UserEntity by id
    */
    private UserEntity getUserEntityById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /*
    * Convert UserEntity Page to UserPageResponse
    * @param page
    * @param size
    * @param userEntityPage
    * */
    private static UserPageResponse getUserPageResponse(int page, int size, Page<UserEntity> userEntityPage) {
        log.info("Convert UserEntity Page to UserPageResponse");
        List<UserResponse> userResponses = userEntityPage.stream().map(userEntity -> UserResponse.builder()
                .id(userEntity.getId())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .gender(userEntity.getGender())
                .birthday(String.valueOf(userEntity.getBirthday()))
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .phone(userEntity.getPhone())
                .build()
        ).toList();

        UserPageResponse userPageResponse = new UserPageResponse();
        userPageResponse.setPageNumber(page);
        userPageResponse.setSize(size);
        userPageResponse.setTotalElements(userEntityPage.getTotalElements());
        userPageResponse.setTotalPages(userEntityPage.getTotalPages());
        userPageResponse.setUsers(userResponses);
        return userPageResponse;
    }



}
