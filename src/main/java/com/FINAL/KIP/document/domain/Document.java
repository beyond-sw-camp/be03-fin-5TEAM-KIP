package com.FINAL.KIP.document.domain;

import com.FINAL.KIP.common.domain.BaseEntity;
import com.FINAL.KIP.group.domain.Group;
import com.FINAL.KIP.hashtag.domain.DocHashTag;
import com.FINAL.KIP.request.domain.Request;
import com.FINAL.KIP.version.domain.Version;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Document extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private UUID uuid;

    @Column(nullable = false)
    private String title;

    @Enumerated(value = EnumType.STRING)
    private KmsDocType kmsDocType;
  
    @Column(nullable = false)
    private int bookCount = 0; // 문서 북마크 갯수 체크

    @ManyToOne // OneToOne은 유니크 걸림
    @JoinColumn
    private Document upLink;

    @ManyToOne
    @JoinColumn
    private Document downLink;

    @ManyToOne
    @JoinColumn
    private Group group;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL)
    private final List<Version> versions = new ArrayList<>();

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Request> requests = new ArrayList<>();
  
    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL)
    private final List<DocHashTag> docHashTags = new ArrayList<>();
    public void addAllDocHashTags(List<DocHashTag> docHashTags) {
        this.docHashTags.addAll(docHashTags);
    }

    public Document () {}

    @Builder
    public Document(String title, KmsDocType kmsDocType, Group group) {
        this.title = title;
        this.kmsDocType = kmsDocType;
        this.group = group;
    }

    @PrePersist
    public void prePersist(){
        this.uuid = UUID.randomUUID();
    }

    public void addBookCount(){
        this.bookCount+=1;
    }

    public void reduceLikeCount(){
        this.bookCount-=1;
    }
}
