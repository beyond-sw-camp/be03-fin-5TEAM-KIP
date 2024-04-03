package com.FINAL.KIP.comment.dto;

import com.FINAL.KIP.comment.domain.Comment;
import lombok.Data;

@Data
public class CommentReqDto {
    private String comment;
    private Comment superComment;
}
