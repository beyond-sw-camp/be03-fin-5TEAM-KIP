package com.FINAL.KIP.hashtag.domain;

import com.FINAL.KIP.document.domain.Document;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

@Getter
@EqualsAndHashCode
public class DocHashTagId implements Serializable {

    private Document document;
    private HashTag hashTag;

    public DocHashTagId() {}

    @Builder
    public DocHashTagId (Document document, HashTag hashTag) {
        this.document = document;
        this.hashTag = hashTag;
    }

}
