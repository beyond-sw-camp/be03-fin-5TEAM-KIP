package com.FINAL.KIP.comment.service;

import com.FINAL.KIP.comment.domain.Comment;
import com.FINAL.KIP.comment.dto.CommentReqDto;
import com.FINAL.KIP.comment.dto.CommentResDto;
import com.FINAL.KIP.comment.repository.CommentRepository;
import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.document.repository.DocumentRepository;
import com.FINAL.KIP.user.domain.User;
import com.FINAL.KIP.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {
    private final DocumentRepository documentRepository;
    private final UserRepository userRepo;
    private final CommentRepository commentRepository;

    public CommentService(DocumentRepository documentRepository, UserRepository userRepo, CommentRepository commentRepository) {
        this.documentRepository = documentRepository;
        this.userRepo = userRepo;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public CommentResDto docCommentCreate(Long documentId, CommentReqDto commentReqDto){
        Document document = documentRepository.findById(documentId).orElseThrow(() -> new EntityNotFoundException("없는 문서입니다."));

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        String[] split = name.split(":");
        String employeeId = split[0];
        User user = userRepo.findByEmployeeId(employeeId).orElseThrow(() -> new EntityNotFoundException("user not found"));

        Comment comment = Comment.CreateComment(commentReqDto.getComment(), commentReqDto.getSuperComment(), user.getName(), document.getId());
        commentRepository.save(comment);

        return new CommentResDto(HttpStatus.OK, "User Comment Create successfully!", comment.getComment());

    }
}
