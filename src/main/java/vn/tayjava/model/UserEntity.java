package vn.tayjava.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import vn.tayjava.common.UserStatus;

import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "tbl_user")
@Slf4j(topic = "UserEntity")
public class UserEntity extends AbstractEntity<Long> implements UserDetails, Serializable {

    @Column(name = "first_name")
    private String firstName;

    private String lastName;

    @Column(nullable = false)
    private String gender;

    @Column(name = "date_of_birth")
    @Temporal(TemporalType.DATE)
    private Date birthday;

    private String email;

    private String phone;

    @Column(name = "username", unique = true, nullable = false, length = 255)
    private String username;

    private String password;


    private String avatarUrl;


    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String status;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<UserHasRole> roles = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<GroupHasUser> groups = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        // Get roles by user_id
        List<Role> roleList = roles.stream().map(UserHasRole::getRole).toList();

        // Get role name
        List<String> roleNames = roleList.stream().map(Role::getName).toList();
        log.info("User roles: {}", roleNames);

        return roleNames.stream().map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return String.valueOf(UserStatus.ACTIVE).equalsIgnoreCase(getStatus());
    }
}
