package com.FINAL.KIP.user.dto.res;

import com.FINAL.KIP.user.domain.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserResDto {
    private final String name;
    private final String email;
    private final String phoneNumber;
    private final String password;
    private final String profileUrl;
    private final LocalDateTime employedDay;

    public UserResDto(User user){
        this.name = user.getName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.password = user.getPassword();
        this.profileUrl = user.getProfileUrl();
        this.employedDay = user.getEmployedDay();
    }
}
