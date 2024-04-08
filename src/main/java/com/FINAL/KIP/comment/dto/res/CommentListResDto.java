package com.FINAL.KIP.comment.dto.res;

import com.FINAL.KIP.comment.domain.Comment;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

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
        this.childComments = comment.getChildComments().stream()
                .map(CommentListResDto::new) // 자식 댓글을 CommentListResDto로 변환
                .collect(Collectors.toList()); // 변환된 객체들을 List로 수집
    }
}

