package com.FINAL.KIP.user.dto.req;

import lombok.Data;

@Data
public class PasswordChangeRequest {
    private String findByEmployeeId;
    private String currentPassword;
    private String newPassword;
}

