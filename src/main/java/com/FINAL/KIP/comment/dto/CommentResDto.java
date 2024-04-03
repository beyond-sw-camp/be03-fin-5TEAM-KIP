package com.FINAL.KIP.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@Builder
public class CommentResDto {
    private HttpStatus httpStatus;
    private String message;
    private Object result;
}
