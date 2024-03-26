package com.FINAL.KIP.document.dto.res;

import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.document.domain.KmsDocType;
import lombok.Getter;

@Getter
public class PublicDocResDto {

    private final String discription = "전체공개";
    private final String title;
    private final KmsDocType kmsDocType;


    public PublicDocResDto(Document document) {
        this.title = document.getTitle();
        this.kmsDocType = document.getKmsDocType();
    }
}
