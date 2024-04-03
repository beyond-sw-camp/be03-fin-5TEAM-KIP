package com.FINAL.KIP.attachedfile.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 파일 업로드 요청 시 클라이언트로부터 받는 데이터를 담는 DTO입니다.
 */
@Data // Lombok을 사용하여 getter와 setter를 자동으로 생성합니다.
public class AttachedFileUploadReqDto {
    private MultipartFile file; // 업로드할 파일 데이터

    public AttachedFileUploadReqDto(MultipartFile file) {
        this.file = file;
    }
}

