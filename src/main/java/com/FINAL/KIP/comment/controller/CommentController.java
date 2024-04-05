package com.FINAL.KIP.comment.controller;

import com.FINAL.KIP.comment.dto.req.CreateCommentReqDto;
import com.FINAL.KIP.comment.dto.req.UpdateCommentReqDto;
import com.FINAL.KIP.comment.dto.res.CommentListResDto;
import com.FINAL.KIP.comment.dto.res.CommentResDto;
import com.FINAL.KIP.comment.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("doc")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

//    댓글 생성
    @PostMapping("{DocumentId}/comment")
    public ResponseEntity<CommentResDto> docCommentCreate(@PathVariable Long DocumentId, @RequestBody CreateCommentReqDto createCommentReqDto) {
        return ResponseEntity.ok(commentService.docCommentCreate(DocumentId, createCommentReqDto));
    }

//    댓글 조회(Hierarchy)
//    DocumentId에 해당하는 부모댓글과 그 자식댓글들을 조회하여 반환
    @GetMapping("/{DocumentId}/comment")
    public ResponseEntity<List<CommentListResDto>> docCommentList(@PathVariable Long DocumentId) {
        List<CommentListResDto> comments = commentService.docCommentList(DocumentId);
        return ResponseEntity.ok(comments);
    }

//    댓글 수정
    @PatchMapping("/{DocumentId}/comment")
    public ResponseEntity<CommentResDto> docCommentUpdate(@PathVariable Long DocumentId, @RequestBody UpdateCommentReqDto updateCommentReqDto) {
        return ResponseEntity.ok(commentService.docCommentUpdate(DocumentId, updateCommentReqDto));
    }

//    댓글 삭제
    @DeleteMapping("/{DocumentId}/{CommentId}")
    public String docCommentDelete(@PathVariable Long DocumentId, @PathVariable Long CommentId){
        commentService.docCommentDelete(DocumentId, CommentId);

        return CommentId + "번 댓글이 삭제되었습니다.";
    }
}
