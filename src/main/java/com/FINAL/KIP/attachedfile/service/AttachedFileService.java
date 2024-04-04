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

    public TempFileUploadResDto uploadTempFile(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String fileType = file.getContentType();
        Long fileSize = file.getSize();
        String tempFileId = UUID.randomUUID().toString();

        Path filePath = Paths.get("uploads/temp/" + tempFileId + "_" + originalFileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes(), StandardOpenOption.CREATE);

        AttachedFile savedFile = new AttachedFile();
        savedFile.setFileName(originalFileName);
        savedFile.setFileType(fileType);
        savedFile.setFileSize(fileSize);
        savedFile.setFileUrl(filePath.toString());
        savedFile.setTempFileId(tempFileId);
        savedFile.setIsTemp(true);

        attachedFileRepository.save(savedFile);

        return new TempFileUploadResDto(savedFile.getId(), savedFile.getTempFileId(), "파일이 성공적으로 업로드 되었습니다.");
    }

    public AttachedFileResDto linkTempFileToDocument(DocumentFileLinkReqDto linkReqDto) {
        AttachedFile file = attachedFileRepository.findByTempFileId(linkReqDto.getTempFileId())
                .orElseThrow(() -> new IllegalArgumentException("Temporary file not found with id: " + linkReqDto.getTempFileId()));
        Document document = documentRepository.findById(linkReqDto.getDocId())
                .orElseThrow(() -> new IllegalArgumentException("Document not found with id: " + linkReqDto.getDocId()));

        file.setDocument(document);
        file.setIsTemp(false);

        AttachedFile updatedFile = attachedFileRepository.save(file);

        return new AttachedFileResDto(updatedFile.getId(), updatedFile.getFileName(), updatedFile.getFileType(), updatedFile.getFileSize(), updatedFile.getFileUrl(), "파일이 문서에 성공적으로 연결되었습니다.");
    }

    public AttachedFileResDto uploadFile(MultipartFile file, Long docId) throws IOException {
        Document document = documentRepository.findById(docId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with id: " + docId));

        String originalFileName = file.getOriginalFilename();
        String fileType = file.getContentType();
        Long fileSize = file.getSize();

        Path filePath = Paths.get("uploads/" + docId + "_" + originalFileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes(), StandardOpenOption.CREATE);

        AttachedFile savedFile = new AttachedFile();
        savedFile.setFileName(originalFileName);
        savedFile.setFileType(fileType);
        savedFile.setFileSize(fileSize);
        savedFile.setFileUrl(filePath.toString());
        savedFile.setDocument(document);
        savedFile.setIsTemp(false);

        attachedFileRepository.save(savedFile);

        return new AttachedFileResDto(savedFile.getId(), savedFile.getFileName(), savedFile.getFileType(), savedFile.getFileSize(), savedFile.getFileUrl(), "파일이 성공적으로 업로드되어 문서에 연결되었습니다.");
    }

    public Resource getFile(Long id) throws IOException {
        AttachedFile file = attachedFileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("조회할 파일이 존재하지 않습니다."));

        Path filePath = Paths.get(file.getFileUrl());
        Resource resource = new UrlResource(filePath.toUri());
        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new IllegalArgumentException("파일을 읽을 수 없습니다.");
        }
    }

    public String deleteFile(Long id) {
        AttachedFile file = attachedFileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 파일이 존재하지 않습니다."));

        try {
            Path path = Paths.get(file.getFileUrl());
            Files.deleteIfExists(path);
            attachedFileRepository.deleteById(id);
            return "파일이 성공적으로 삭제되었습니다.";
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 중 오류가 발생했습니다.");
        }
    }

    public AttachedFileResDto updateFile(Long id, MultipartFile newFile) throws IOException {
        AttachedFile existingFile = attachedFileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("수정할 파일이 존재하지 않습니다."));

        Path oldFilePath = Paths.get(existingFile.getFileUrl());
        Files.deleteIfExists(oldFilePath);

        String originalFileName = newFile.getOriginalFilename();
        String fileType = newFile.getContentType();
        Long fileSize = newFile.getSize();

        Path newFilePath = Paths.get("uploads/" + id + "_" + originalFileName);
        Files.createDirectories(newFilePath.getParent());
        Files.write(newFilePath, newFile.getBytes(), StandardOpenOption.CREATE);

        existingFile.setFileName(originalFileName);
        existingFile.setFileType(fileType);
        existingFile.setFileSize(fileSize);
        existingFile.setFileUrl(newFilePath.toString());

        attachedFileRepository.save(existingFile);

        return new AttachedFileResDto(existingFile.getId(), existingFile.getFileName(), existingFile.getFileType(), existingFile.getFileSize(), existingFile.getFileUrl(), "파일이 성공적으로 업데이트되었습니다.");
    }

    public List<AttachedFileResDto> getAllFiles() {
        List<AttachedFile> files = attachedFileRepository.findAll();
        if (files.isEmpty()) {
            throw new RuntimeException("조회할 파일이 존재하지 않습니다.");
        }

        return files.stream()
                .map(file -> new AttachedFileResDto(file.getId(), file.getFileName(), file.getFileType(), file.getFileSize(), file.getFileUrl(), "모든 파일이 성공적으로 조회되었습니다."))
                .collect(Collectors.toList());
    }
}