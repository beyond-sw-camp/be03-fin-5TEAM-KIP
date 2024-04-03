package com.FINAL.KIP.document.dto.req;

import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.document.domain.KmsDocType;
import com.FINAL.KIP.hashtag.dto.req.HashTagReqDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CreateDocumentReqDto {

    private String title;
    private Long upLinkId;
    private Long groupId;
    private List<HashTagReqDto> hashTags;

    public Document makeDocDtoToDocument(){
        return Document.builder()
                .title(this.title)
                .kmsDocType(KmsDocType.CONTENT)
                .build();
    }
}
