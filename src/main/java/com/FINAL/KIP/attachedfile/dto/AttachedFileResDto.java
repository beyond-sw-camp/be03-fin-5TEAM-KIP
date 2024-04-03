package com.FINAL.KIP.attachedfile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 클라이언트에게 파일 메타데이터를 전달할 때 사용하는 데이터 전송 객체(DTO)입니다.
 */
@Data // Lombok 라이브러리를 사용하여 getter, setter, toString, equals, hashCode 메서드를 자동으로 생성합니다.
@NoArgsConstructor // 인자 없는 생성자를 자동으로 생성합니다.
@AllArgsConstructor // 모든 필드 값을 인자로 받는 생성자를 자동으로 생성합니다.
public class AttachedFileResDto {
    private Long id; // 파일의 고유 ID
    private String fileName; // 파일명
    private String fileType; // 파일 타입(MIME 타입)
    private Long fileSize; // 파일 크기
    private String fileUrl; // 파일이 저장된 경로
}