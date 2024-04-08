package com.FINAL.KIP.comment.repository;

import com.FINAL.KIP.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByDocumentIdAndSuperCommentIsNull(Long documentId);

    List<Comment> findByUserName(String name);
}
