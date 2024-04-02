package com.FINAL.KIP.document.dto.res;

import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.document.domain.KmsDocType;
import lombok.Getter;

@Getter
public class LinkedDocumentResDto {

    private final Long documentId;
    private final KmsDocType docType;
    private final String title;

    public LinkedDocumentResDto(Document document){
        this.documentId = document.getId();
        this.docType = document.getKmsDocType();
        this.title = document.getTitle();
    }
}
