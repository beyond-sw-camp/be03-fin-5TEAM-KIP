package com.FINAL.KIP.user.dto.res;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookResDto {
    private Long documentId;
    private String title;
    private String groupName;
}
