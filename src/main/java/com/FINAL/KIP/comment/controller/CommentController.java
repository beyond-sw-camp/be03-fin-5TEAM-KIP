package com.FINAL.KIP.comment.controller;

import com.FINAL.KIP.comment.dto.CommentResDto;
import com.FINAL.KIP.comment.dto.CreateCommentReqDto;
import com.FINAL.KIP.comment.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

//    댓글 조회
//    @GetMapping("{DocumentId}/comment")
//    public ResponseEntity<CommentResDto> docCommentList(@PathVariable Long DocumentId){
//        CommentResDto commentResDto = commentService.docCommentList(DocumentId);
//        return new ResponseEntity<>(commentResDto, HttpStatus.OK);
//    }

}
