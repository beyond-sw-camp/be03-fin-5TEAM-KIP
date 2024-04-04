package com.FINAL.KIP.attachedfile.controller;

import com.FINAL.KIP.attachedfile.dto.AttachedFileResDto;
import com.FINAL.KIP.attachedfile.dto.DocumentFileLinkReqDto;
import com.FINAL.KIP.attachedfile.dto.TempFileUploadResDto;
import com.FINAL.KIP.attachedfile.service.AttachedFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/files")
public class AttachedFileController {

    private final AttachedFileService attachedFileService;

    @Autowired
    public AttachedFileController(AttachedFileService attachedFileService) {
        this.attachedFileService = attachedFileService;
    }

    @PostMapping("/temp-upload")
    public ResponseEntity<?> uploadTempFile(@RequestParam("file") MultipartFile file) {
        try {
            TempFileUploadResDto response = attachedFileService.uploadTempFile(file);
            return ResponseEntity.ok().body(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 업로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @PostMapping("/link-to-document")
    public ResponseEntity<?> linkTempFileToDocument(@RequestBody DocumentFileLinkReqDto linkReqDto) {
        try {
            AttachedFileResDto response = attachedFileService.linkTempFileToDocument(linkReqDto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/upload/{docId}")
    public ResponseEntity<?> uploadFile(@PathVariable Long docId, @RequestParam("file") MultipartFile file) {
        try {
            AttachedFileResDto response = attachedFileService.uploadFile(file, docId);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 업로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFile(@PathVariable Long id) {
        try {
            Resource file = attachedFileService.getFile(id);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + file.getFilename() + "\"").body(file);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("조회할 파일이 존재하지 않습니다.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable Long id) {
        try {
            String message = attachedFileService.deleteFile(id);
            return ResponseEntity.ok().body(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("삭제할 파일이 존재하지 않습니다.");
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateFile(@PathVariable Long id, @RequestParam("file") MultipartFile newFile) {
        try {
            AttachedFileResDto response = attachedFileService.updateFile(id, newFile);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("수정할 파일이 존재하지 않습니다.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 업데이트 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> getAllFiles() {
        try {
            List<AttachedFileResDto> files = attachedFileService.getAllFiles();
            return ResponseEntity.ok(files);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("조회할 파일이 존재하지 않습니다.");
        }
    }
}