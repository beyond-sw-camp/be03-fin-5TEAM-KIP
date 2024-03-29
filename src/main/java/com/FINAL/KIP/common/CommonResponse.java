package com.FINAL.KIP.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@Builder
public class CommonResponse {
    private HttpStatus status;
    private String message;
    private Object result;
}
