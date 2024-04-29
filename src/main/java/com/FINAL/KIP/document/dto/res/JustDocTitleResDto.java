package com.FINAL.KIP.document.dto.res;

import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.document.domain.KmsDocType;
import com.FINAL.KIP.hashtag.domain.DocHashTag;
import com.FINAL.KIP.hashtag.domain.HashTag;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class JustDocTitleResDto {

    private final Long documentId;
    private final String documentUUID;
    private final KmsDocType docType;
    private final String title;
    private final List<Long> hashTagIds;

    public JustDocTitleResDto(Document document){
        this.documentId = document.getId();
        this.documentUUID = document.getUuid().toString();
        this.docType = document.getKmsDocType();
        this.title = document.getTitle();
        this.hashTagIds = document.getDocHashTags().stream()
                .map(DocHashTag::getHashTag)
                .map(HashTag::getId)
                .collect(Collectors.toList());
	}
}
