package com.FINAL.KIP.comment.service;

import com.FINAL.KIP.comment.domain.Comment;
import com.FINAL.KIP.comment.dto.*;
import com.FINAL.KIP.comment.repository.CommentRepository;
import com.FINAL.KIP.common.aspect.UserAdmin;
import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.document.repository.DocumentRepository;
import com.FINAL.KIP.user.domain.User;
import com.FINAL.KIP.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @UserAdmin
    @Transactional
    public CommentResDto docCommentCreate(Long documentId, CreateCommentReqDto createCommentReqDto) {
        Document document = documentRepository.findById(documentId).orElseThrow(() -> new EntityNotFoundException("없는 문서입니다."));

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        String[] split = name.split(":");
        String employeeId = split[0];
        User user = userRepo.findByEmployeeId(employeeId).orElseThrow(() -> new EntityNotFoundException("user not found"));

        Comment newComment = createNewComment(createCommentReqDto, document.getId(), user.getName());
        Comment savedNewComment = commentRepository.save(newComment);
        return new CommentResDto(savedNewComment);
    }

//    댓글 조회(Hierarchy)
//    superCommentId가 null인 모든 댓글을 조회하여 CommentListResDto로 매핑하여 반환합니다.
    @UserAdmin
    public List<CommentListResDto> docCommentList(Long documentId) {
        getDocCommentById(documentId);
        List<Comment> superComments = commentRepository.findByDocumentIdAndSuperCommentIsNull(documentId);
        if (superComments.isEmpty())
            throw new IllegalStateException(documentId + "번 문서에 댓글이 없습니다.");

        return superComments.stream()
                .map(CommentListResDto::new)
                .collect(Collectors.toList());
    }

//    댓글 수정
    @UserAdmin
    public CommentResDto docCommentUpdate(Long documentId, UpdateCommentReqDto updateCommentReqDto) {

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        String[] split = name.split(":");
        String employeeId = split[0];
        User user = userRepo.findByEmployeeId(employeeId).orElseThrow(() -> new EntityNotFoundException("user not found"));

//        사용자의 이름과 일치하는 댓글을 찾고 댓글이 없으면 예외발생
        List<Comment> commentOptional = commentRepository.findByUserName(user.getName());
        if (commentOptional.isEmpty()) {
            throw new IllegalArgumentException("댓글 수정 권한이 없습니다.");
        }

        getDocCommentById(documentId);
        Comment comment = getCommentById(updateCommentReqDto.getCommentId());
        comment.setComment(updateCommentReqDto.getComment());
        return new CommentResDto(commentRepository.save(comment));
    }

//    댓글 삭제
    @UserAdmin
    public void docCommentDelete(Long documentId, Long commentId){

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        String[] split = name.split(":");
        String employeeId = split[0];
        User user = userRepo.findByEmployeeId(employeeId).orElseThrow(() -> new EntityNotFoundException("user not found"));

//        사용자의 이름과 일치하는 댓글을 찾고 댓글이 없으면 예외발생
        List<Comment> commentOptional = commentRepository.findByUserName(user.getName());
        if (commentOptional.isEmpty()) {
            throw new IllegalArgumentException("댓글 삭제 권한이 없습니다.");
        }

        getDocCommentById(documentId);
        Comment comment = getCommentById(commentId);
        commentRepository.delete(comment);
    }

//    ===== 공통함수 =====

//    docCommentCreate 필요한 값 주입
    public Comment createNewComment(CreateCommentReqDto createCommentReqDto, Long id, String name) {
        Comment newComment = createCommentReqDto.makeAuthorityReqDtoToComment(id, name);
        Optional.ofNullable(createCommentReqDto.getSuperCommentId())
                .map(this::getSuperCommentById)
                .ifPresent(newComment::setSuperComment);
        return newComment;
    }

//    createNewComment에서 상위 댓글 유무 확인
    public Comment getSuperCommentById(Long superCommentId) {
        return commentRepository.findById(superCommentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "상위 댓글이 없습니다. " + superCommentId));
    }

//    문서 유무 확인
    public Comment getDocCommentById(Long documentId) {
        return commentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "없는 문서입니다. " + documentId));
    }

//    댓글 유무 확인
    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "없는 댓글입니다. " + commentId));
    }
}
