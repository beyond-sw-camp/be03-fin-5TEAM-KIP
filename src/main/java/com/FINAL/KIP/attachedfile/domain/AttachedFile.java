package com.FINAL.KIP.attachedfile.domain;

import com.FINAL.KIP.document.domain.Document;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachedFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String fileUrl;
    private String tempFileId; // 임시 파일의 고유 ID
    private Boolean isTemp; // 임시 파일 여부

    @ManyToOne
    @JoinColumn(name = "document_id", nullable = true)
    private Document document;

    @CreationTimestamp
    private LocalDateTime createdTime;

    @UpdateTimestamp
    private LocalDateTime updatedTime;
}