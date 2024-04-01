package com.FINAL.KIP.attachedfile.service;

import com.FINAL.KIP.attachedfile.domain.AttachedFile;
import com.FINAL.KIP.attachedfile.dto.AttachedFileResDto;
import com.FINAL.KIP.attachedfile.repository.AttachedFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

// 필요한 Java 및 Spring Framework의 클래스들을 import합니다.
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
@Transactional
public class AttachedFileService {

    private final AttachedFileRepository attachedFileRepository;

    @Autowired
    public AttachedFileService(AttachedFileRepository attachedFileRepository) {
        this.attachedFileRepository = attachedFileRepository;
    }

    /**
     * 파일 업로드 로직
     * 사용자가 업로드한 파일을 서버의 지정된 위치에 저장하고, 파일의 메타데이터를 데이터베이스에 저장합니다.
     *
     * @param file 사용자가 업로드한 파일
     * @return 저장된 파일의 메타데이터 정보를 포함하는 DTO
     * @throws IOException 파일 시스템 작업 중 발생할 수 있는 예외를 처리합니다.
     */
    public AttachedFileResDto uploadFile(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename(); // 업로드된 파일의 원본 파일 이름을 얻습니다.
        String fileType = file.getContentType(); // 파일의 MIME 타입을 얻습니다.
        Long fileSize = file.getSize(); // 파일의 크기를 얻습니다.

        AttachedFile savedFile = AttachedFile.builder()
                .fileName(originalFileName)
                .fileType(fileType)
                .fileSize(fileSize)
                .build(); // 파일의 메타데이터를 생성합니다.
        savedFile = attachedFileRepository.save(savedFile); // 메타데이터를 데이터베이스에 저장합니다.

        Path filePath = Paths.get("C:/Users/Playdata/Desktop/tmp/", savedFile.getId() + "_" + originalFileName);
        Files.write(filePath, file.getBytes(), StandardOpenOption.CREATE); // 파일을 서버에 저장합니다.
        savedFile.setFileUrl(filePath.toString()); // 파일의 저장 경로를 메타데이터에 업데이트합니다.

        attachedFileRepository.save(savedFile); // 업데이트된 메타데이터를 다시 저장합니다.
        return new AttachedFileResDto(savedFile.getId(), savedFile.getFileName(), savedFile.getFileType(), savedFile.getFileSize(), savedFile.getFileUrl());
    }

    /**
     * 파일 조회 로직
     * 데이터베이스에서 파일의 메타데이터를 조회하고, 해당 파일을 서버에서 로드하여 리턴합니다.
     *
     * @param id 조회할 파일의 ID
     * @return 조회된 파일의 리소스
     */
    public Resource getFile(Long id) {
        AttachedFile file = attachedFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + id)); // 파일 ID로 메타데이터 조회

        try {
            Path filePath = Paths.get(file.getFileUrl());
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource; // 파일이 존재하고, 읽을 수 있다면 리소스를 반환
            } else {
                throw new RuntimeException("File not found or not readable");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    /**
     * 파일 삭제 로직
     * 데이터베이스와 파일 시스템에서 파일의 메타데이터와 실제 파일을 삭제합니다.
     *
     * @param id 삭제할 파일의 ID
     */
    public void deleteFile(Long id) {
        AttachedFile file = attachedFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + id)); // 파일 ID로 메타데이터 조회

        try {
            Path path = Paths.get(file.getFileUrl());
            Files.deleteIfExists(path); // 파일 시스템에서 실제 파일 삭제
            attachedFileRepository.deleteById(id); // 데이터베이스에서 파일 메타데이터 삭제
        } catch (IOException e) {
            throw new RuntimeException("Error while deleting the file", e);
        }
    }

    /**
     * 파일 업데이트 로직
     * 기존 파일을 새 파일로 교체하고, 파일의 메타데이터를 업데이트합니다.
     *
     * @param id 업데이트할 파일의 ID
     * @param newFile 새로운 파일
     * @return 업데이트된 파일의 메타데이터 정보를 포함하는 DTO
     * @throws IOException 파일 시스템 작업 중 발생할 수 있는 예외를 처리합니다.
     */
    public AttachedFileResDto updateFile(Long id, MultipartFile newFile) throws IOException {
        AttachedFile existingFile = attachedFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + id)); // 파일 ID로 기존 메타데이터 조회

        Path oldFilePath = Paths.get(existingFile.getFileUrl());
        Files.deleteIfExists(oldFilePath); // 기존 파일 삭제

        String originalFileName = newFile.getOriginalFilename();
        Long fileSize = newFile.getSize();
        String fileType = newFile.getContentType();

        Path newFilePath = Paths.get("C:/Users/Playdata/Desktop/tmp/", existingFile.getId() + "_" + originalFileName);
        Files.write(newFilePath, newFile.getBytes(), StandardOpenOption.CREATE); // 새 파일 저장

        existingFile.setFileName(originalFileName); // 메타데이터 업데이트
        existingFile.setFileType(fileType);
        existingFile.setFileSize(fileSize);
        existingFile.setFileUrl(newFilePath.toString());
        attachedFileRepository.save(existingFile); // 업데이트된 메타데이터 저장

        return new AttachedFileResDto(existingFile.getId(), existingFile.getFileName(), existingFile.getFileType(), existingFile.getFileSize(), existingFile.getFileUrl());
    }
}