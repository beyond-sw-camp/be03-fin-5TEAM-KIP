package com.FINAL.KIP.attachedfile.service;

import com.FINAL.KIP.attachedfile.domain.AttachedFile;
import com.FINAL.KIP.attachedfile.dto.AttachedFileResDto;
import com.FINAL.KIP.attachedfile.repository.AttachedFileRepository;
import com.FINAL.KIP.common.s3.S3Config;
import com.FINAL.KIP.document.repository.DocumentRepository;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AttachedFileService {

    private final AttachedFileRepository attachedFileRepository;
    private final DocumentRepository documentRepository;

    //s3 연결 config
    private final S3Config s3Config;

    // s3 bucket 이름
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Autowired
    public AttachedFileService(AttachedFileRepository attachedFileRepository, DocumentRepository documentRepository, S3Config s3Config) {
        this.attachedFileRepository = attachedFileRepository;
        this.documentRepository = documentRepository;
        this.s3Config = s3Config;
    }

    //    파일 업로드
    public String uploadFile(MultipartFile file, Long documentId) throws IOException {
        String originalFilename = file.getOriginalFilename();

//        파일 길이(String)이 길면 DB에 들어가지 않기 때문에 길이 잘라서 DB 삽입(...)
        int maxFilenameLength = 30; // String 클래스의 최대 길이를 사용합니다.
        if (originalFilename.length() > maxFilenameLength) {
            originalFilename = originalFilename.substring(0, maxFilenameLength - 3) + "...";
        }

        // 파일 확장자 추출
        String fileExtension = "";
        String contentType = file.getContentType();
        int slashIndex = contentType.lastIndexOf('/');
        if (slashIndex != -1 && slashIndex < contentType.length() - 1) {
            fileExtension = contentType.substring(slashIndex + 1);
        }

//        // 파일 이름에 파일 확장자를 추가
//        if (!fileExtension.isEmpty()) {
//            originalFilename += "." + fileExtension;
//        }

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        AttachedFile attachedFile = new AttachedFile();
        attachedFile.setFileName(originalFilename);
        attachedFile.setFileType(file.getContentType());
        attachedFile.setFileUrl(s3Config.amazonS3Client().getUrl(bucket, originalFilename).toString());
        attachedFile.setDocumentId(documentId);

        attachedFileRepository.save(attachedFile);

//        S3로 올라갈때는 String 자르지 않고 통 이름으로 올라감
//        s3Config.amazonS3Client().putObject(bucket, file.getOriginalFilename(), file.getInputStream(), metadata);
//        return s3Config.amazonS3Client().getUrl(bucket, file.getOriginalFilename()).toString();


        s3Config.amazonS3Client().putObject(bucket, originalFilename, file.getInputStream(), metadata);
        return s3Config.amazonS3Client().getUrl(bucket, originalFilename).toString();
    }

    //    파일 조회
    public List<AttachedFileResDto> fileList(Long documentId) throws IOException{
        List<AttachedFile> files = attachedFileRepository.findByDocumentId(documentId);
        if(files.isEmpty()){
            throw new IOException(documentId + "번 문서에 첨부파일이 없습니다.");
        }

        // 파일 목록을 AttachedFileResDto로 변환합니다.
        List<AttachedFileResDto> fileList = files.stream()
                .map(attachedFile -> AttachedFileResDto.builder()
                        .id(attachedFile.getId())
                        .fileName(attachedFile.getFileName())
                        .fileType(attachedFile.getFileType())
                        .fileUrl(attachedFile.getFileUrl())
                        .build())
                .collect(Collectors.toList());

        return fileList;
    }

    //    파일 다운로드
    public ResponseEntity<byte[]> downloadFile(String originName) throws IOException {
        // AWS S3에서 파일의 URL을 가져옵니다.
        URL fileUrl = s3Config.amazonS3Client().getUrl(bucket, originName);

        // 파일의 내용을 가져옵니다.
        URLConnection connection = fileUrl.openConnection();
        InputStream inputStream = connection.getInputStream();

        // 파일 이름을 인코딩합니다.
        String encodedFilename = URLEncoder.encode(originName, StandardCharsets.UTF_8);

        // 응답 헤더를 설정합니다.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", encodedFilename);

        // InputStreamResource를 사용하여 ResponseEntity를 생성하여 반환합니다.
        InputStreamResource resource = new InputStreamResource(inputStream);
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource.getContentAsByteArray());
    }

    //    파일 삭제
    public void deleteFile(String originName)  {
        AttachedFile attachedFile = attachedFileRepository.findByFileName(originName);
        attachedFileRepository.delete(attachedFile);
        s3Config.amazonS3Client().deleteObject(bucket, originName);
    }
}
