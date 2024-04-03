package com.FINAL.KIP.attachedfile.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TempFileUploadResDto {
    private Long id; // 파일의 고유 ID
    private String tempFileId; // 임시 파일의 고유 ID


    @Builder
    public TempFileUploadResDto(Long id, String tempFileId) {
        this.id = id;
        this.tempFileId = tempFileId;
    }
}