package com.FINAL.KIP.attachedfile.service;

import com.FINAL.KIP.attachedfile.domain.AttachedFile;
import com.FINAL.KIP.attachedfile.dto.AttachedFileResDto;
import com.FINAL.KIP.attachedfile.dto.DocumentFileLinkReqDto;
import com.FINAL.KIP.attachedfile.dto.TempFileUploadResDto;
import com.FINAL.KIP.attachedfile.repository.AttachedFileRepository;
import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.document.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AttachedFileService {

    private final AttachedFileRepository attachedFileRepository;
    private final DocumentRepository documentRepository;

    @Autowired
    public AttachedFileService(AttachedFileRepository attachedFileRepository, DocumentRepository documentRepository) {
        this.attachedFileRepository = attachedFileRepository;
        this.documentRepository = documentRepository;
    }

    // 파일 업로드

    public TempFileUploadResDto uploadTempFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("업로드된 파일이 비어 있습니다.");
        }
        String originalFileName = file.getOriginalFilename();
        String fileType = file.getContentType();
        Long fileSize = file.getSize();
        String tempFileId = UUID.randomUUID().toString();
        Path filePath = Paths.get("uploads/temp/" + tempFileId + "_" + originalFileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes(), StandardOpenOption.CREATE);

        AttachedFile savedFile = AttachedFile.builder()
                .fileName(originalFileName)
                .fileType(fileType)
                .fileSize(fileSize)
                .fileUrl(filePath.toString())
                .tempFileId(tempFileId)
                .isTemp(true)
                .build();

        attachedFileRepository.save(savedFile);

        return TempFileUploadResDto.builder()
                .id(savedFile.getId())
                .tempFileId(savedFile.getTempFileId())
                .message("파일이 성공적으로 업로드 되었습니다.")
                .build();
    }

    // 문서에 파일 연결
    public AttachedFileResDto linkTempFileToDocument(DocumentFileLinkReqDto linkReqDto) {
        AttachedFile file = attachedFileRepository.findByTempFileId(linkReqDto.getTempFileId())
                .orElseThrow(() -> new IllegalArgumentException("임시 파일을 찾을 수 없습니다: " + linkReqDto.getTempFileId()));
        Document document = documentRepository.findById(linkReqDto.getDocId())
                .orElseThrow(() -> new IllegalArgumentException("문서를 찾을 수 없습니다: " + linkReqDto.getDocId()));

        file.setDocument(document);
        file.setIsTemp(false);
        attachedFileRepository.save(file);

        return AttachedFileResDto.builder()
                .id(file.getId())
                .fileName(file.getFileName())
                .fileType(file.getFileType())
                .fileSize(file.getFileSize())
                .fileUrl(file.getFileUrl())
                .message("파일이 문서에 성공적으로 연결되었습니다.")
                .build();
    }

    // 파일 업로드 및 문서에 바로 연결
    public AttachedFileResDto uploadFile(MultipartFile file, Long docId) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("업로드된 파일이 비어 있습니다.");
        }
        Document document = documentRepository.findById(docId)
                .orElseThrow(() -> new IllegalArgumentException("문서를 찾을 수 없습니다: " + docId));

        String originalFileName = file.getOriginalFilename();
        String fileType = file.getContentType();
        Long fileSize = file.getSize();
        String storedFileName = UUID.randomUUID().toString() + "_" + originalFileName;
        Path filePath = Paths.get("uploads/" + storedFileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes(), StandardOpenOption.CREATE);

        AttachedFile savedFile = AttachedFile.builder()
                .fileName(originalFileName)
                .fileType(fileType)
                .fileSize(fileSize)
                .fileUrl(filePath.toString())
                .document(document)
                .isTemp(false)
                .build();

        attachedFileRepository.save(savedFile);

        return AttachedFileResDto.builder()
                .id(savedFile.getId())
                .fileName(savedFile.getFileName())
                .fileType(savedFile.getFileType())
                .fileSize(savedFile.getFileSize())
                .fileUrl(savedFile.getFileUrl())
                .message("파일이 성공적으로 업로드되어 문서에 연결되었습니다.")
                .build();
    }

    // 특정 파일 조회
    public Resource getFile(Long id) throws IOException {
        AttachedFile file = attachedFileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다: " + id));

        Path filePath = Paths.get(file.getFileUrl());
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new IOException("파일을 읽을 수 없습니다: " + file.getFileName());
        }
        return resource;
    }

    // 모든 파일 조회
    public List<AttachedFileResDto> getAllFiles() {
        List<AttachedFile> files = attachedFileRepository.findAll();
        if (files.isEmpty()) {
            throw new RuntimeException("조회할 파일이 없습니다.");
        }
        return files.stream()
                .map(file -> AttachedFileResDto.builder()
                        .id(file.getId())
                        .fileName(file.getFileName())
                        .fileType(file.getFileType())
                        .fileSize(file.getFileSize())
                        .fileUrl(file.getFileUrl())
                        .message("파일 조회 성공")
                        .build())
                .collect(Collectors.toList());
    }

    // 파일 수정(업데이트)
    public AttachedFileResDto updateFile(Long id, MultipartFile newFile) throws IOException {
        if (newFile.isEmpty()) {
            throw new IOException("업데이트할 새 파일이 비어 있습니다.");
        }
        AttachedFile existingFile = attachedFileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다: " + id));

        String originalFileName = newFile.getOriginalFilename();
        String fileType = newFile.getContentType();
        Long fileSize = newFile.getSize();

        Path oldFilePath = Paths.get(existingFile.getFileUrl());
        Files.deleteIfExists(oldFilePath);

        String storedFileName = UUID.randomUUID().toString() + "_" + originalFileName;
        Path newFilePath = Paths.get("uploads/" + storedFileName);
        Files.createDirectories(newFilePath.getParent());
        Files.write(newFilePath, newFile.getBytes(), StandardOpenOption.CREATE);

        existingFile.setFileName(originalFileName);
        existingFile.setFileType(fileType);
        existingFile.setFileSize(fileSize);
        existingFile.setFileUrl(newFilePath.toString());
        attachedFileRepository.save(existingFile);

        return AttachedFileResDto.builder()
                .id(existingFile.getId())
                .fileName(existingFile.getFileName())
                .fileType(existingFile.getFileType())
                .fileSize(existingFile.getFileSize())
                .fileUrl(existingFile.getFileUrl())
                .message("파일이 성공적으로 업데이트되었습니다.")
                .build();
    }

    // 파일 삭제
    public String deleteFile(Long id) {
        AttachedFile file = attachedFileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다: " + id));
        try {
            Path path = Paths.get(file.getFileUrl());
            Files.deleteIfExists(path);
            attachedFileRepository.delete(file);
            return "파일이 성공적으로 삭제되었습니다.";
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 중 오류가 발생했습니다.", e);
        }
    }
}
