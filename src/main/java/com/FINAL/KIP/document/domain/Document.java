package com.FINAL.KIP.document.domain;

import com.FINAL.KIP.common.domain.BaseEntity;
import com.FINAL.KIP.group.domain.Group;
import com.FINAL.KIP.request.domain.Request;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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

    @ManyToOne // OneToOne은 유니크 걸림
    @JoinColumn
    private Document upLink;

    @ManyToOne
    @JoinColumn
    private Document downLink;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Group group;

    private String delYn = "N";

    @OneToMany(mappedBy = "documentId", cascade = CascadeType.ALL)
    private final List<Request> requests = new ArrayList<>();
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
}
