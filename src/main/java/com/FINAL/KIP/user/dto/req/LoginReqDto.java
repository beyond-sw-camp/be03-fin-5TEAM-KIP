package com.FINAL.KIP.user.dto.req;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class LoginReqDto {

    @NotEmpty(message = "employeeId is essential")
    private String employeeId;

    @NotEmpty(message = "password is essential")
    @Size(min = 4, message = "minimum length is 4")
    private String password;

}
