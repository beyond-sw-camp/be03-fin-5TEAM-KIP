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

//        return new TempFileUploadResDto(savedFile.getId(), savedFile.getTempFileId());

        return TempFileUploadResDto.builder()
                .tempFileId(savedFile.getTempFileId())
                .id(savedFile.getId())
                .build();

    }

    public AttachedFileResDto linkTempFileToDocument(DocumentFileLinkReqDto linkReqDto) {
        AttachedFile file = attachedFileRepository.findByTempFileId(linkReqDto.getTempFileId())
                .orElseThrow(() -> new IllegalArgumentException("Temporary file not found with id: " + linkReqDto.getTempFileId()));
        Document document = documentRepository.findById(linkReqDto.getDocId())
                .orElseThrow(() -> new IllegalArgumentException("Document not found with id: " + linkReqDto.getDocId()));

        file.setDocument(document);
        file.setIsTemp(false);

        AttachedFile updatedFile = attachedFileRepository.save(file);

        return new AttachedFileResDto(updatedFile.getId(), updatedFile.getFileName(), updatedFile.getFileType(), updatedFile.getFileSize(), updatedFile.getFileUrl());
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

        return new AttachedFileResDto(savedFile.getId(), savedFile.getFileName(), savedFile.getFileType(), savedFile.getFileSize(), savedFile.getFileUrl());
    }

    public Resource getFile(Long id) throws IOException {
        AttachedFile file = attachedFileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("File not found with id: " + id));

        Path filePath = Paths.get(file.getFileUrl());
        Resource resource = new UrlResource(filePath.toUri());
        if(resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new IllegalArgumentException("Could not read the file");
        }
    }

    public void deleteFile(Long id) throws IOException {
        AttachedFile file = attachedFileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("File not found with id: " + id));

        Path path = Paths.get(file.getFileUrl());
        Files.deleteIfExists(path);
        attachedFileRepository.deleteById(id);
    }

    public AttachedFileResDto updateFile(Long id, MultipartFile newFile) throws IOException {
        AttachedFile existingFile = attachedFileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("File not found with id: " + id));

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

        return new AttachedFileResDto(existingFile.getId(), existingFile.getFileName(), existingFile.getFileType(), existingFile.getFileSize(), existingFile.getFileUrl());
    }

    public List<AttachedFileResDto> getAllFiles() {
        return attachedFileRepository.findAll().stream()
                .map(file -> new AttachedFileResDto(file.getId(), file.getFileName(), file.getFileType(), file.getFileSize(), file.getFileUrl()))
                .collect(Collectors.toList());
    }
}