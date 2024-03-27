package com.FINAL.KIP.document.dto.req;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class moveDocInGroupReqDto {
    private Long startDocId;
    private Long endDocId;
}
