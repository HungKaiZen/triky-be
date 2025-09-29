package vn.tayjava.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "tbl_user_avatars")
public class UserAvatar extends AbstractEntity<Long> implements Serializable {
    private Long userId;
    private String avatarUrl;
}
