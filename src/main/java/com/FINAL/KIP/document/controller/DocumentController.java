package com.FINAL.KIP.document.controller;


import com.FINAL.KIP.document.dto.req.CreateDocumentReqDto;
import com.FINAL.KIP.document.dto.res.DocumentResDto;
import com.FINAL.KIP.document.dto.res.GetDocumentResDto;
import com.FINAL.KIP.document.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("{docId}/{userId}")
    public ResponseEntity<GetDocumentResDto> getDocument(@PathVariable Long docId,
                                                         @PathVariable Long userId) {
        return ResponseEntity.ok(documentService.GetIsAccessibleDoc(docId, userId));
    }
}
