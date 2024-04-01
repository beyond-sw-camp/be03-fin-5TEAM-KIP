package com.FINAL.KIP.attachedfile.controller;

import com.FINAL.KIP.attachedfile.service.AttachedFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

import com.FINAL.KIP.attachedfile.domain.AttachedFile;
import com.FINAL.KIP.attachedfile.dto.AttachedFileResDto;
import com.FINAL.KIP.attachedfile.repository.AttachedFileRepository;
@RestController
@RequestMapping("/api/files")
public class AttachedFileController {

    private final AttachedFileService attachedFileService;

    @Autowired
    public AttachedFileController(AttachedFileService attachedFileService) {
        this.attachedFileService = attachedFileService;
    }

    /**
     * 파일 업로드를 처리하는 엔드포인트입니다.
     * @param file 사용자가 업로드한 파일.
     * @return 업로드된 파일의 메타데이터.
     * @throws Exception 파일 업로드 중 발생할 수 있는 예외.
     */
    @PostMapping("/upload")
    public ResponseEntity<AttachedFileResDto> uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        AttachedFileResDto fileDto = attachedFileService.uploadFile(file);
        return ResponseEntity.ok(fileDto);
    }

    /**
     * 저장된 파일을 조회하는 엔드포인트입니다.
     * @param id 조회할 파일의 ID.
     * @return 파일의 리소스와 함께 파일을 다운로드할 수 있도록 설정된 헤더.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Resource> getFile(@PathVariable Long id) {
        Resource file = attachedFileService.getFile(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    /**
     * 파일을 삭제하는 엔드포인트입니다.
     * @param id 삭제할 파일의 ID.
     * @return 삭제 성공 시 HTTP 상태 코드 200 반환.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        attachedFileService.deleteFile(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 파일을 업데이트하는 엔드포인트입니다.
     * @param id 업데이트할 파일의 ID.
     * @param file 새로운 파일.
     * @return 업데이트된 파일의 메타데이터.
     * @throws Exception 파일 업데이트 중 발생할 수 있는 예외.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AttachedFileResDto> updateFile(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws Exception {
        AttachedFileResDto fileDto = attachedFileService.updateFile(id, file);
        return ResponseEntity.ok(fileDto);
    }
}