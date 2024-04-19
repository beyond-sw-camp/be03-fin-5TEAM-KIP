package com.FINAL.KIP.attachedfile.repository;

import com.FINAL.KIP.attachedfile.domain.AttachedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachedFileRepository extends JpaRepository<AttachedFile, Long> {
    List<AttachedFile> findByDocumentId(Long documentId);

    AttachedFile findByFileName(String originName);
}