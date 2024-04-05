package com.FINAL.KIP.comment.dto;

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



//        this.childComments = comment.getChildComments().stream()
//                .map(Comment::getId) // commentId 추출
//                .distinct() // 중복된 commentId 제거
//                .map(commentId -> new CommentListResDto(
//                        comment.getChildComments().stream()
//                                .filter(childComment -> childComment.getId().equals(commentId))
//                                .findFirst()
//                                .orElseThrow())) // 중복되지 않은 commentId를 가진 자식 댓글로 CommentListResDto 생성
//                .collect(Collectors.toList());
//        this.childComments = comment.getChildComments().stream()
//                .distinct()
//                .map(CommentListResDto::new)
//                .collect(Collectors.toList());

//        this.childComments = new ArrayList<>();

//            if (comment.getSuperComment() == null) {
//                for (Comment childComment : comment.getChildComments()) {
//                    this.childComments.add(new CommentListResDto(childComment));
//                }
//            }
//        }

//        // 자식 댓글이 있으면 출력
//        for (Comment childComment : comment.getChildComments()) {
//            this.childComments.add(new CommentListResDto(childComment));
//        }
    }
}

