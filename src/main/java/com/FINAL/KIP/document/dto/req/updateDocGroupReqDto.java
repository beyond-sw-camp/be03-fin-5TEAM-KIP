package com.FINAL.KIP.document.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class updateDocGroupReqDto {
    private Long targetDocumentId;
    private Long targetGroupId;
}
