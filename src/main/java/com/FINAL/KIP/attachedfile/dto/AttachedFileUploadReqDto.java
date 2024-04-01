package com.FINAL.KIP.attachedfile.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 파일 업로드 요청 시 사용하는 DTO입니다.
 * 사용자로부터 MultipartFile 타입의 파일을 받아 처리합니다.
 */
@Data
public class AttachedFileUploadReqDto {
    private MultipartFile file;

    public AttachedFileUploadReqDto(MultipartFile file) {
        this.file = file;
    }
}