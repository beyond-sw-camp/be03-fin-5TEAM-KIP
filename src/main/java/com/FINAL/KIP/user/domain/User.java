package com.FINAL.KIP.user.domain;

import com.FINAL.KIP.common.domain.BaseEntity;
import com.FINAL.KIP.group.domain.GroupUser;
import com.FINAL.KIP.note.domain.Note;
import com.FINAL.KIP.request.domain.Request;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


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

    @Setter
    @Column(nullable = false)
    private String password;

    @Setter
    private String profileImageUrl;

    private String employedDay;

    @Column(unique = true, nullable = false)
    private String employeeId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "requester", cascade = CascadeType.ALL)
    private final List<Request> requests = new ArrayList<>();

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    private final List<Note> notes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private final List<GroupUser> groupUsers = new ArrayList<>();

    public User() {

    }

    @Builder
    public User(String name, String email, String password,
                String phoneNumber, String profileImageUrl,
                String employedDay, String employeeId, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
        this.employedDay = employedDay;
        this.employeeId = employeeId;
        this.role = role;
    }

    public void updateUserInfo(String name, String email, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

}

