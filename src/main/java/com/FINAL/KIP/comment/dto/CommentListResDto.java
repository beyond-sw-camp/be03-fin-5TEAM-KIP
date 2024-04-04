package com.FINAL.KIP.comment.dto;

import com.FINAL.KIP.comment.domain.Comment;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CommentListResDto {
    private final Long commentId;
    private final String comment;
    private final String userName;
    private final Long superCommentId;
    private final List<CommentListResDto> childComments;

    public CommentListResDto(Comment comment) {
        this.commentId = comment.getId();
        this.comment = comment.getComment();
        this.userName = comment.getUserName();
        this.superCommentId = comment.getSuperComment() != null ? comment.getSuperComment().getId() : null;
        this.childComments = new ArrayList<>();

        // 자식 댓글이 있으면 출력
        for (Comment childComment : comment.getChildComments()) {
            this.childComments.add(new CommentListResDto(childComment));
        }
    }
}
