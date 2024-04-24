package com.FINAL.KIP.document.dto.res;

import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.document.domain.KmsDocType;
import lombok.Getter;

@Getter
public class JustDocTitleResDto {

    private final Long documentId;
    private final String documentUUID;
    private final KmsDocType docType;
    private final String title;

    public JustDocTitleResDto(Document document){
        this.documentId = document.getId();
        this.documentUUID = document.getUuid().toString();
        this.docType = document.getKmsDocType();
        this.title = document.getTitle();
	}
}
