package com.FINAL.KIP.controller;


import com.FINAL.KIP.entity.AttachmentEntity;
import com.FINAL.KIP.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attachments")
public class AttachmentController {
    private final AttachmentService attachmentService;

    @Autowired
    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @GetMapping
    public List<AttachmentEntity> getAllAttachments() {
        return attachmentService.findAllAttachments();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttachmentEntity> getAttachmentById(@PathVariable Long id) {
        return attachmentService.findAttachmentById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public AttachmentEntity createOrUpdateAttachment(@RequestBody AttachmentEntity attachment) {
        return attachmentService.saveOrUpdateAttachment(attachment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAttachment(@PathVariable Long id) {
        attachmentService.deleteAttachment(id);
        return ResponseEntity.ok().build();
    }
}