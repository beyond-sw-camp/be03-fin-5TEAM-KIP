package com.FINAL.KIP.user.dto.req;

import lombok.Data;

@Data
public class UserInfoUpdateReqDto {
    private String name;
    private String email;
    private String phoneNumber;
}
