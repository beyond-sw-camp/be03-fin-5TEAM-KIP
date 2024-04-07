package com.FINAL.KIP.attachedfile.controller;

import com.FINAL.KIP.attachedfile.service.AttachedFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class AttachedFileController {

    private final AttachedFileService attachedFileService;

    @Autowired
    public AttachedFileController(AttachedFileService attachedFileService) {
        this.attachedFileService = attachedFileService;
    }

    // 파일 업로드
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String originName = file.getOriginalFilename();
            String fileUrl = attachedFileService.uploadFile(file);
            return ResponseEntity.ok(Map.of(
                    "uploaded", true,
                    "originName", originName,
                    "url", fileUrl));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }





//    // 파일 업로드
//    @PostMapping("/upload")
//    public ResponseEntity<?> uploadTempFile(@RequestParam("file") MultipartFile file) {
//        try {
//            TempFileUploadResDto response = attachedFileService.uploadTempFile(file);
//            return ResponseEntity.ok(response);
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 중 오류가 발생했습니다: " + e.getMessage());
//        }
//    }

    // 파일 조회
//    @GetMapping("/{id}")
//    public ResponseEntity<?> getFile(@PathVariable Long id) {
//        try {
//            Resource file = attachedFileService.getFile(id);
//            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
//        } catch (IOException | IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body("파일 조회 중 오류가 발생했습니다: " + e.getMessage());
//        }
//    }
//
//    // 파일 전체 조회
//    @GetMapping("/list")
//    public ResponseEntity<?> getAllFiles() {
//        try {
//            List<AttachedFileResDto> files = attachedFileService.getAllFiles();
//            return ResponseEntity.ok(files);
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("조회할 파일이 존재하지 않습니다.");
//        }
//    }
//
//    // 파일 수정
//    @PatchMapping("/{id}")
//    public ResponseEntity<?> updateFile(@PathVariable Long id, @RequestParam("file") MultipartFile newFile) {
//        try {
//            AttachedFileResDto response = attachedFileService.updateFile(id, newFile);
//            return ResponseEntity.ok(response);
//        } catch (IOException | IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body("파일 업데이트 중 오류가 발생했습니다: " + e.getMessage());
//        }
//    }
//
//    // 파일 삭제
//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deleteFile(@PathVariable Long id) {
//        try {
//            String message = attachedFileService.deleteFile(id);
//            return ResponseEntity.ok().body(message);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body("삭제할 파일이 존재하지 않습니다: " + e.getMessage());
//        }
//    }
}