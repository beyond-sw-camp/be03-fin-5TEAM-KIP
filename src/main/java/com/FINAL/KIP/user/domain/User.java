package com.FINAL.KIP.user.domain;

import com.FINAL.KIP.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Entity
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    private String profileImageUrl;

    private LocalDateTime employedDay;

    @Column(nullable = false)
    private String employeeId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Setter
    private String delYn = "N";
    public User(){}

    @Builder
    public User(String name, String email, String password,
                String phoneNumber, String profileImageUrl,
                LocalDateTime employedDay, String employeeId, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
        this.employedDay = employedDay;
        this.employeeId = employeeId;
        this.role = role;
    }

}
