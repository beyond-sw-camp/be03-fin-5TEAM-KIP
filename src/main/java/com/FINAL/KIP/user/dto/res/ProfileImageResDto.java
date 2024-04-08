package com.FINAL.KIP.user.dto.res;

import lombok.AllArgsConstructor;
import lombok.Data;

//프로필 이미지 ResDto
@Data
@AllArgsConstructor
public class ProfileImageResDto {
    private Long userId;
    private String profileImageUrl;
}