package com.FINAL.KIP.user.dto.req;


import com.FINAL.KIP.user.domain.User;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
public class CreateUserReqDto {
    private String name;
    private String email;
    private String phoneNumber;
    private String password;
    private String profileUrl;
    private LocalDateTime employedDay;

    public User makeUserReqDtoToUser(CreateUserReqDto this) {
        return User.builder()
                .name(this.name)
                .email(this.email)
                .phoneNumber(this.phoneNumber)
                .password(this.password)
                .profileUrl(this.profileUrl)
                .employedDay(this.employedDay)
                .build();
    }
}
