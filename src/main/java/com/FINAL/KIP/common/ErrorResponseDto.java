package com.FINAL.KIP.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ErrorResponseDto { //공통 에러 메세지 사용
    public static ResponseEntity<Map<String, Object>> makeMessage(HttpStatus status, String message){
        Map<String, Object> body = new HashMap<>();
        body.put("status", Integer.toString(status.value()));
        body.put("status_message", status.getReasonPhrase());
        body.put("error_message", message);
        return new ResponseEntity<>(body, status);
    }
}
