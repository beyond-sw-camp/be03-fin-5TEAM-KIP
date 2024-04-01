package com.FINAL.KIP.repository;

import com.FINAL.KIP.domain.AttachmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<AttachmentEntity, Long> {
}