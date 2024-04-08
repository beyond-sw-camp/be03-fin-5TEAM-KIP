package com.FINAL.KIP.comment.dto.req;

import com.FINAL.KIP.comment.domain.Comment;
import lombok.Getter;
import lombok.Setter;

@Setter
public class CreateCommentReqDto {
    private String comment;

    @Getter
    private Long superCommentId;

    public Comment makeAuthorityReqDtoToComment(Long id, String name){
        return Comment.builder()
                .comment(this.comment)
                .documentId(id)
                .userName(name)
                .build();
    }
}
