package com.FINAL.KIP.user.dto.req;


import com.FINAL.KIP.user.domain.Role;
import com.FINAL.KIP.user.domain.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter
public class CreateUserReqDto {
    private String name;
    private String email;
    private String phoneNumber;
    private String password;
    private String profileImageUrl;
    private LocalDateTime employedDay;
    private String employeeId;

    public User makeUserReqDtoToUser() {
        return User.builder()
                .name(this.name)
                .email(this.email)
                .phoneNumber(this.phoneNumber)
                .password(this.password)
                .profileImageUrl(this.profileImageUrl)
                .employedDay(this.employedDay)
                .employeeId(this.employeeId)
                .role(Role.USER)
                .build();
    }
}
