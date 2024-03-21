package com.FINAL.KIP.controller;

import com.FINAL.KIP.domain.AttachmentEntity;
import com.FINAL.KIP.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/attachments")
public class AttachmentController {

    private final AttachmentService attachmentService;

    @Autowired
    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @GetMapping("/{attachmentId}")
    public ResponseEntity<AttachmentEntity> getAttachmentById(@PathVariable("attachmentId") String attachmentId) {
        if (!attachmentId.startsWith("attachment-")) {
            return ResponseEntity.badRequest().build();
        }
        Long id;
        try {
            id = Long.parseLong(attachmentId.substring("attachment-".length()));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }

        Optional<AttachmentEntity> attachment = attachmentService.findAttachmentById(id);
        return attachment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public AttachmentEntity createOrUpdateAttachment(@RequestBody AttachmentEntity attachment) {
        return attachmentService.saveOrUpdateAttachment(attachment);
    }

    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<?> deleteAttachment(@PathVariable("attachmentId") String attachmentId) {
        if (!attachmentId.startsWith("attachment-")) {
            return ResponseEntity.badRequest().build();
        }
        Long id;
        try {
            id = Long.parseLong(attachmentId.substring("attachment-".length()));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }

        attachmentService.deleteAttachment(id);
        return ResponseEntity.ok().build();
    }
}