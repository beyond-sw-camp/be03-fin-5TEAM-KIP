package com.FINAL.KIP.document.dto.req;

import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.document.domain.KmsDocType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateDocumentReqDto {

    private String title;
    private String kmsDocType;
    private Long upLinkId;
    private Long downLinkId;
    private Long groupId;

    public Document makeDocDtoToDocument (CreateDocumentReqDto this){
        return Document.builder()
                .title(this.title)
                .kmsDocType(KmsDocType.valueOf(this.kmsDocType))
                .build();
    }
}
