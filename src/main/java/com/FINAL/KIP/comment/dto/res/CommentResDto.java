package com.FINAL.KIP.comment.dto.res;

import com.FINAL.KIP.comment.domain.Comment;
import lombok.Getter;

@Getter
public class CommentResDto {
    private final Long commentId;
    private final String comment;
    private final String userName;
    private final Long superCommentId;
    private final Long documentId;

    public CommentResDto(Comment comment) {
        this.commentId = comment.getId();
        this.comment = comment.getComment();
        this.userName = comment.getUserName();
        this.superCommentId = comment.getSuperComment() != null ? comment.getSuperComment().getId() : null;
        this.documentId = comment.getDocumentId();
    }
}
