package com.FINAL.KIP.comment.dto.req;

import lombok.Data;

@Data
public class UpdateCommentReqDto {
    private final Long commentId;
    private final String comment;
}
