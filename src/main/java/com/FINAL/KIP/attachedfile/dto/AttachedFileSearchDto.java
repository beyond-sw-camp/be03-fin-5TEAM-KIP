package com.FINAL.KIP.attachedfile.dto;

import lombok.Data;

/**
 * 파일 검색에 사용되는 조건을 담는 DTO입니다.
 * 검색 기능 구현 시, 이 DTO를 통해 검색 조건을 전달받습니다.
 */
@Data
public class AttachedFileSearchDto {
    private String fileName; // 검색할 파일 이름
    private String fileType; // 검색할 파일 타입
}