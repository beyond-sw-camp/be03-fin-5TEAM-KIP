package com.FINAL.KIP.attachedfile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 클라이언트에게 파일 메타데이터를 전달할 때 사용하는 DTO입니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttachedFileResDto {
    private Long id;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String fileUrl;
}