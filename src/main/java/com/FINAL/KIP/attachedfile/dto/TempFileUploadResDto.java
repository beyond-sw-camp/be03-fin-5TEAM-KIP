package com.FINAL.KIP.attachedfile.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TempFileUploadResDto {
    private Long id;
    private String tempFileId;
    private String message; // 성공 메시지 필드 추가

    @Builder
    public TempFileUploadResDto(Long id, String tempFileId, String message) {
        this.id = id;
        this.tempFileId = tempFileId;
        this.message = message;
    }
}