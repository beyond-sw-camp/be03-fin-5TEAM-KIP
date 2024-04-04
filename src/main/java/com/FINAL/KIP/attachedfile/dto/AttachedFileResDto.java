package com.FINAL.KIP.attachedfile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttachedFileResDto {
    private Long id;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String fileUrl;
    private String message; // 성공 메시지 필드 추가
}