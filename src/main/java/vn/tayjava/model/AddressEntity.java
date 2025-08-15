package vn.tayjava.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "tbl_address")
public class AddressEntity extends AbstractEntity<Long>{

    @Column(name = "apartment_number")
    private String apartmentNumber;
    private String floor;
    private String building;
    @Column(name = "street_number")
    private String streetNumber;
    private String street;
    private String city;
    private String country;
    @Column(name = "address_type")
    private Integer addressType;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updatedAt;


}
