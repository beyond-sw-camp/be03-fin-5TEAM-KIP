package com.FINAL.KIP.bookmark.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@Builder
public class BookResDto {
    private HttpStatus httpStatus;
    private String message;
    private Object result;
}
