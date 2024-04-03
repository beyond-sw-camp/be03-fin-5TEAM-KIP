package com.FINAL.KIP.attachedfile.controller;

import com.FINAL.KIP.attachedfile.dto.AttachedFileResDto;
import com.FINAL.KIP.attachedfile.dto.DocumentFileLinkReqDto;
import com.FINAL.KIP.attachedfile.dto.TempFileUploadResDto;
import com.FINAL.KIP.attachedfile.service.AttachedFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
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
    public ResponseEntity<TempFileUploadResDto> uploadTempFile(@RequestParam("file") MultipartFile file) throws IOException {
        TempFileUploadResDto tempFileResDto = attachedFileService.uploadTempFile(file);
        return ResponseEntity.ok(tempFileResDto);
    }

    @PostMapping("/link-to-document")
    public ResponseEntity<AttachedFileResDto> linkFileToDocument(@RequestBody DocumentFileLinkReqDto linkReqDto) {
        AttachedFileResDto fileDto = attachedFileService.linkTempFileToDocument(linkReqDto);
        return ResponseEntity.ok(fileDto);
    }

    @PostMapping("/upload/{docId}")
    public ResponseEntity<AttachedFileResDto> uploadFile(@PathVariable Long docId, @RequestParam("file") MultipartFile file) throws IOException {
        AttachedFileResDto fileDto = attachedFileService.uploadFile(file, docId);
        return ResponseEntity.ok(fileDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getFile(@PathVariable Long id) throws IOException {
        Resource file = attachedFileService.getFile(id);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFile(@PathVariable Long id) {
        String message = attachedFileService.deleteFile(id);
        return ResponseEntity.ok(message); // 메시지와 함께 응답 반환
    }

    @PutMapping("/{id}")
    public ResponseEntity<AttachedFileResDto> updateFile(@PathVariable Long id, @RequestParam("file") MultipartFile newFile) throws IOException {
        AttachedFileResDto fileDto = attachedFileService.updateFile(id, newFile);
        return ResponseEntity.ok(fileDto);
    }

    @GetMapping("/list")
    public ResponseEntity<List<AttachedFileResDto>> getAllFiles() {
        List<AttachedFileResDto> files = attachedFileService.getAllFiles();
        return ResponseEntity.ok(files);
    }
}
