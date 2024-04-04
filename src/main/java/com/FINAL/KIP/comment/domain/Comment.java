package com.FINAL.KIP.comment.domain;

import com.FINAL.KIP.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "comment")
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String comment;

    @Column(nullable = false)
    private String userName;

    @Setter
    @ManyToOne
    @JoinColumn
    private Comment superComment;

    @Column(nullable = false)
    private Long documentId;

    private String delYn="N"; // comment 삭제 유무

    public Comment() {}

    @OneToMany(mappedBy = "superComment", cascade = CascadeType.PERSIST)
    private final List<Comment> childComments = new ArrayList<>();

    @Builder
    public Comment(String comment, String userName, Long documentId) {
        this.comment = comment;
        this.userName = userName;
        this.documentId = documentId;
    }

    public void deleteComment(){ // item 삭제 시 호출
        this.delYn = "Y";
    }
    public void update(String comment){
        this.comment = comment;
    }

//    public static Comment CreateComment(String comment, Comment superComment, String userName, Long documentId) {
//        return new Comment(comment, superComment, userName, documentId);
//    }
}
