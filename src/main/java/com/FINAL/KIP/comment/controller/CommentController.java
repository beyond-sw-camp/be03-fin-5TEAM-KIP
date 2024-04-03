package com.FINAL.KIP.comment.controller;

import com.FINAL.KIP.comment.dto.CommentReqDto;
import com.FINAL.KIP.comment.dto.CommentResDto;
import com.FINAL.KIP.comment.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("doc")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

//    comment create
    @PostMapping("{DocumentId}/comment")
    public ResponseEntity<CommentResDto> docCommentCreate(@PathVariable Long DocumentId, CommentReqDto commentReqDto) {
        CommentResDto commentResDto = commentService.docCommentCreate(DocumentId, commentReqDto);
        return new ResponseEntity<>(commentResDto, HttpStatus.OK);
    }

}
