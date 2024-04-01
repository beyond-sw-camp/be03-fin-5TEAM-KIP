package com.FINAL.KIP.hashtag.domain;


import com.FINAL.KIP.common.domain.BaseEntity;
import com.FINAL.KIP.document.domain.Document;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
@IdClass(DocHashTagId.class)
public class DocHashTag extends BaseEntity {

    @ManyToOne
    @JoinColumn(nullable = false)
    @Id private Document document;

    @ManyToOne
    @JoinColumn(nullable = false)
    @Id private HashTag hashTag;

    public DocHashTag(){}

    @Builder
    public DocHashTag(Document document, HashTag hashTag) {
        this.document = document;
        this.hashTag = hashTag;
    }
}
