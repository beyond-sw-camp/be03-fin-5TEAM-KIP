package com.FINAL.KIP.comment.service;

import com.FINAL.KIP.comment.domain.Comment;
import com.FINAL.KIP.comment.dto.CommentResDto;
import com.FINAL.KIP.comment.dto.CreateCommentReqDto;
import com.FINAL.KIP.comment.repository.CommentRepository;
import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.document.repository.DocumentRepository;
import com.FINAL.KIP.user.domain.User;
import com.FINAL.KIP.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

//    댓글 생성
    @Transactional
    public CommentResDto docCommentCreate(Long documentId, CreateCommentReqDto createCommentReqDto){
        Document document = documentRepository.findById(documentId).orElseThrow(() -> new EntityNotFoundException("없는 문서입니다."));

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        String[] split = name.split(":");
        String employeeId = split[0];
        User user = userRepo.findByEmployeeId(employeeId).orElseThrow(() -> new EntityNotFoundException("user not found"));

        Comment newComment = createNewComment(createCommentReqDto, document.getId(), user.getName());
        Comment savedNewComment = commentRepository.save(newComment);
        return new CommentResDto(savedNewComment);

//        Comment comment = Comment.CreateComment(createCommentReqDto.getComment(), createCommentReqDto.getSuperCommentId(), user.getName(), document.getId());
//        commentRepository.save(comment);
//
//        return new CommentResDto(HttpStatus.OK, "User Comment Create successfully!", comment.getComment());
    }

//    댓글 조회
//    public CommentResDto docCommentList(Long documentId){
//        return new CommentResDto(HttpStatus.OK, "User Comment Create successfully!", documentId);
//    }

    //  공통함수
    public Comment createNewComment(CreateCommentReqDto createCommentReqDto, Long id, String name) {
        Comment newComment = createCommentReqDto.makeAuthorityReqDtoToComment(id, name);
        Optional.ofNullable(createCommentReqDto.getSuperCommentId())
                .map(this::getCommentById)
                .ifPresent(newComment::setSuperComment);
        return newComment;
    }

    public Comment getCommentById(Long superCommentId) {
        return commentRepository.findById(superCommentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "상위 댓글이 없습니다. " + superCommentId));
    }
}
