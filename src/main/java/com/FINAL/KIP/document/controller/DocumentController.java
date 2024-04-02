package com.FINAL.KIP.document.controller;


import com.FINAL.KIP.document.dto.req.CreateDocumentReqDto;
import com.FINAL.KIP.document.dto.req.moveDocInGroupReqDto;
import com.FINAL.KIP.document.dto.req.updateDocGroupIdReqDto;
import com.FINAL.KIP.document.dto.req.updateDocTitleReqDto;
import com.FINAL.KIP.document.dto.res.DocumentResDto;
import com.FINAL.KIP.document.dto.res.LinkedDocumentResDto;
import com.FINAL.KIP.document.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("doc")
public class DocumentController {

    private final DocumentService documentService;

    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

//    Create
    @PostMapping
    public ResponseEntity<DocumentResDto> createDocument(@RequestBody CreateDocumentReqDto dto) {
        return ResponseEntity.ok(documentService.createDocument(dto));
    }

//    Read
    @GetMapping
    public ResponseEntity<List<DocumentResDto>> getPublicDocuments(){
        return ResponseEntity.ok(documentService.getPublicDocuments());
    }

    @GetMapping("{documentId}")
    public ResponseEntity<DocumentResDto> getDocument(@PathVariable Long documentId) {
        return ResponseEntity.ok(documentService.getIsAccessibleDoc(documentId));
    }

    @GetMapping("{groupId}/linked")
    public ResponseEntity<List<LinkedDocumentResDto>> getLinkedDocumentsByGroupId(@PathVariable Long groupId) {
        return ResponseEntity.ok(documentService.getLinkedDocumentsByGroupId(groupId));
    }


//  Update
    @PatchMapping("title")
    public ResponseEntity<DocumentResDto> updateDocumentTitle(@RequestBody updateDocTitleReqDto dto){
        return ResponseEntity.ok(documentService.updateDocumentTitle(dto));
    }

    @PatchMapping("move") // 그룹내 문서 이동
    public ResponseEntity<List<LinkedDocumentResDto>> moveDocumentInGroup(@RequestBody moveDocInGroupReqDto dto){
        return ResponseEntity.ok(documentService.moveDocumentInGroup(dto));
    }

    @PatchMapping("{DocumentId}/type") // 문서 섹션, 컨텐츠 변경
    public ResponseEntity<DocumentResDto> updateDocumentType(@PathVariable Long DocumentId){
        return ResponseEntity.ok(documentService.updateDocumentType(DocumentId));
    }

    @PatchMapping("{DocumentId}/public") // 그룹문서 => 전체공개
    public ResponseEntity<DocumentResDto> updateDocumentPublic(@PathVariable Long DocumentId){
        return ResponseEntity.ok(documentService.updateDocumentPublic(DocumentId));
    }

    @PatchMapping("group") // 전체공개 => 그룹문서
    public ResponseEntity<DocumentResDto> updatePublicDocumentGroupId(@RequestBody updateDocGroupIdReqDto dto){
        return ResponseEntity.ok(documentService.updatePublicDocumentGroupId(dto));
    }


//    Delete
    @DeleteMapping("{DocumentId}")
    public ResponseEntity<Void> deletedDocument(@PathVariable Long DocumentId) {
        documentService.deleteDocument(DocumentId);
        return ResponseEntity.noContent().build();
    }

}
