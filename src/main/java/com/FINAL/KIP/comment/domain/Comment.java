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

    @Setter
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

    public Comment() {}

    @OneToMany(mappedBy = "superComment", cascade = CascadeType.PERSIST)
    private final List<Comment> childComments = new ArrayList<>();

    @Builder
    public Comment(String comment, String userName, Long documentId) {
        this.comment = comment;
        this.userName = userName;
        this.documentId = documentId;
    }
}
