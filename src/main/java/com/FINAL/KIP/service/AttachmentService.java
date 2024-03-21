package com.FINAL.KIP.service;

import com.FINAL.KIP.domain.AttachmentEntity;
import com.FINAL.KIP.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;

    @Autowired
    public AttachmentService(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    public Optional<AttachmentEntity> findAttachmentById(Long id) {
        return attachmentRepository.findById(id);
    }

    public AttachmentEntity saveOrUpdateAttachment(AttachmentEntity attachment) {
        return attachmentRepository.save(attachment);
    }

    public void deleteAttachment(Long id) {
        attachmentRepository.deleteById(id);
    }
}