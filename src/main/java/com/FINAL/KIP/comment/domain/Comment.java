package com.FINAL.KIP.comment.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String comment;

    @Column(nullable = false)
    private String userName;

    @ManyToOne
    @JoinColumn
    private Comment superComment;

    @Column(nullable = false)
    private Long documentId;

    private String delYn="N"; // comment 삭제 유무

    public Comment(String comment, Comment superComment, String userName, Long documentId) {
        this.comment = comment;
        this.superComment = superComment;
        this.userName = userName;
        this.documentId = documentId;
    }

//    @CreatedDate
//    private LocalDateTime createdAt;
//
//    @LastModifiedDate
//    private LocalDateTime updatedAt;


    public void deleteComment(){ // item 삭제 시 호출
        this.delYn = "Y";
    }
    public void update(String comment){
        this.comment = comment;
    }

    public static Comment CreateComment(String comment, Comment superComment, String userName, Long documentId) {
        return new Comment(comment, superComment, userName, documentId);
    }
}
