package com.FINAL.KIP.document.controller;


import com.FINAL.KIP.document.dto.req.CreateDocumentReqDto;
import com.FINAL.KIP.document.dto.req.moveDocInGroupReqDto;
import com.FINAL.KIP.document.dto.req.updateDocGroupReqDto;
import com.FINAL.KIP.document.dto.res.DocumentResDto;
import com.FINAL.KIP.document.dto.res.GetDocumentResDto;
import com.FINAL.KIP.document.dto.res.PublicDocResDto;
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
    public ResponseEntity<List<PublicDocResDto>> getPublicDocuments(){
        return ResponseEntity.ok(documentService.getPublicDocuments());
    }

    @GetMapping("{docId}/{userId}")
    public ResponseEntity<GetDocumentResDto> getDocument(@PathVariable Long docId,
                                                         @PathVariable Long userId) {
        return ResponseEntity.ok(documentService.GetIsAccessibleDoc(docId, userId));
    }

    @GetMapping("{groupId}/linked")
    public ResponseEntity<List<GetDocumentResDto>> getLinkedDocumentsByGroupId(@PathVariable Long groupId) {
        return ResponseEntity.ok(documentService.getLinkedDocumentsByGroupId(groupId));
    }


//  Update
    @PatchMapping("move")
    public ResponseEntity<List<GetDocumentResDto>> moveDocumentInGroup(@RequestBody moveDocInGroupReqDto dto){
        return ResponseEntity.ok(documentService.moveDocumentInGroup(dto));
    }

    @PatchMapping("{DocumentId}/type")
    public ResponseEntity<DocumentResDto> updateDocumentType(@PathVariable Long DocumentId){
        return ResponseEntity.ok(documentService.updateDocumentType(DocumentId));
    }

    @PatchMapping("{DocumentId}/public")
    public ResponseEntity<DocumentResDto> updateDocumentPublic(@PathVariable Long DocumentId){
        return ResponseEntity.ok(documentService.updateDocumentPublic(DocumentId));
    }

    @PatchMapping("group") // 전체공개에서 그룹으로 부여하는거 만드는 중 
    public ResponseEntity<DocumentResDto> updateDocumentGroup(@RequestBody updateDocGroupReqDto dto){
        return ResponseEntity.ok(documentService.updateDocumentGroup(dto));

    }


//    Delete
    @DeleteMapping("{DocumentId}")
    public ResponseEntity<Void> deletedDocument(@PathVariable Long DocumentId) {
        documentService.deleteDocument(DocumentId);
        return ResponseEntity.noContent().build();
    }

}
