package com.FINAL.KIP.user.dto.res;

import com.FINAL.KIP.user.domain.Role;
import com.FINAL.KIP.user.domain.User;
import lombok.Data;

@Data
public class UserResDto {
    private final Long userId;
    private final String name;
    private final String email;
    private final String phoneNumber;
    private final String profileImageUrl;
    private final String employedDay;
    private final String employeeId;
    private final Role userRole;

    public UserResDto(User user){
        this.userId = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.profileImageUrl = user.getProfileImageUrl();
        this.employedDay = user.getEmployedDay();
        this.employeeId = user.getEmployeeId();
        this.userRole = user.getRole();
    }
}
