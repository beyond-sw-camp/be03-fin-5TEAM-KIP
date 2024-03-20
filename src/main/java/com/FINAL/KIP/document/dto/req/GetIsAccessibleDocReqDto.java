package com.FINAL.KIP.document.dto.req;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GetIsAccessibleDocReqDto {
    private Long documnetId;
    private Long userId;
}
