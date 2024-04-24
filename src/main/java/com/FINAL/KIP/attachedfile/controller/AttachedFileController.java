package com.FINAL.KIP.attachedfile.controller;

import com.FINAL.KIP.attachedfile.dto.AttachedFileResDto;
import com.FINAL.KIP.attachedfile.service.AttachedFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/doc")
public class AttachedFileController {

    private final AttachedFileService attachedFileService;

    @Autowired
    public AttachedFileController(AttachedFileService attachedFileService) {
        this.attachedFileService = attachedFileService;
    }

    // 파일 업로드
    @PostMapping("{documentId}/fileUpload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @PathVariable Long documentId) {
        try {
            String originName = file.getOriginalFilename();
            String fileUrl = attachedFileService.uploadFile(file, documentId);
            return ResponseEntity.ok(Map.of(
                    "uploaded", true,
                    "originName", originName,
                    "url", fileUrl));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 파일 조회
    @GetMapping("{documentId}/fileList")
    public ResponseEntity<?> fileList(@PathVariable Long documentId) {
        try {
            List<AttachedFileResDto> files = attachedFileService.fileList(documentId);
            return ResponseEntity.ok(files);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("파일 조회 중 오류가 발생했습니다: " + e.getMessage()).getBytes());
        }
    }


    // 파일 다운로드
    @GetMapping("/download/{originName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String originName) throws IOException {
        try {
            return attachedFileService.downloadFile(originName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("파일 다운로드 중 오류가 발생했습니다: " + e.getMessage()).getBytes());
        }
    }

    // 파일 삭제
    @DeleteMapping("/file/{fileId}")
    public String deleteFile(@PathVariable Long fileId) {
        attachedFileService.deleteFile(fileId);

        return fileId + " 파일이 삭제 되었습니다.";
    }
}