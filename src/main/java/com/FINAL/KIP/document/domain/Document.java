package com.FINAL.KIP.document.domain;

import com.FINAL.KIP.group.domain.Group;
import com.FINAL.KIP.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Document extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Enumerated(value = EnumType.STRING)
    private KmsDocType kmsDocType;

    @OneToOne
    @JoinColumn
    private Document upLink;

    @OneToOne
    @JoinColumn
    private Document downLink;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Group group;

    private String delYn = "N";
    public Document () {}

    @Builder
    public Document(String title, KmsDocType kmsDocType, Group group) {
        this.title = title;
        this.kmsDocType = kmsDocType;
        this.group = group;
    }
}
