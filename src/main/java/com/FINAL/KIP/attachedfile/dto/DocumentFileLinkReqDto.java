package com.FINAL.KIP.attachedfile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentFileLinkReqDto {
    private Long docId; // 연결할 문서의 ID
    private String tempFileId; // 연결할 임시 파일의 고유 ID
}