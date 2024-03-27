package com.FINAL.KIP.user.dto.res;

import com.FINAL.KIP.user.domain.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
public class UserResDto {
    private final Long userId;
    private final String name;
    private final String email;
    private final String phoneNumber;
    private final String profileImageUrl;
    private final LocalDateTime employedDay;
    private final String employeeId;

    public UserResDto(User user){
        this.userId = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.profileImageUrl = user.getProfileImageUrl();
        this.employedDay = user.getEmployedDay();
        this.employeeId = user.getEmployeeId();
    }
}
