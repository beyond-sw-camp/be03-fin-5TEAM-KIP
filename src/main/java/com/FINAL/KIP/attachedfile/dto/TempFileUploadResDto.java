package com.FINAL.KIP.attachedfile.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder // 클래스 레벨에 Builder 어노테이션 추가
public class TempFileUploadResDto {
    private Long id; // 파일의 고유 ID
    private String tempFileId; // 임시 파일의 고유 ID
    private String message; // 추가된 메시지 필드

    // 모든 필드를 포함하는 생성자
    @Builder
    public TempFileUploadResDto(Long id, String tempFileId, String message) {
        this.id = id;
        this.tempFileId = tempFileId;
        this.message = message;
    }
}