package com.FINAL.KIP.document.dto.res;

import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.document.domain.KmsDocType;
import lombok.Getter;

@Getter
public class GetDocumentResDto {

    private final Long documentId;
    private final KmsDocType docType;
    private final String title;

    public GetDocumentResDto(Document document, Boolean isAccssible){
        this.documentId = document.getId();
        this.docType = document.getKmsDocType();
        if(isAccssible)
            this.title = document.getTitle();
        else
            this.title = "접근할 수 없는 유저입니다";
    }
}
