package com.FINAL.KIP.document.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class updateDocTitleReqDto {
    private Long targetDocumentId;
    private String newTitle;
}
