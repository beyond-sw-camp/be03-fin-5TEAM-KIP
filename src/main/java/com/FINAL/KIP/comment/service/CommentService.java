package com.FINAL.KIP.comment.service;

import com.FINAL.KIP.comment.domain.Comment;
import com.FINAL.KIP.comment.dto.CommentListResDto;
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
    }

//    댓글 조회(Hierarchy)
// superCommentId가 null인 모든 댓글을 조회하여 CommentListResDto로 매핑하여 반환합니다.
    public List<CommentListResDto> docCommentList(Long documentId) {
        getDocCommentById(documentId);
        List<Comment> superComments = commentRepository.findBySuperCommentIsNull();

        return superComments.stream()
                .map(CommentListResDto::new)
                .collect(Collectors.toList());
}

//공통함수

//    docCommentCreate 필요한 값 주입
    public Comment createNewComment(CreateCommentReqDto createCommentReqDto, Long id, String name) {
        Comment newComment = createCommentReqDto.makeAuthorityReqDtoToComment(id, name);
        Optional.ofNullable(createCommentReqDto.getSuperCommentId())
                .map(this::getCommentById)
                .ifPresent(newComment::setSuperComment);
        return newComment;
    }

//    createNewComment에서 상위 댓글 유무 확인
    public Comment getCommentById(Long superCommentId) {
        return commentRepository.findById(superCommentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "상위 댓글이 없습니다. " + superCommentId));
    }

    public Comment getDocCommentById(Long documentId) {
        return commentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "없는 문서입니다. " + documentId));
    }


}
