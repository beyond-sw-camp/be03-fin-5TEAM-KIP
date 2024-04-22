package com.FINAL.KIP.hashtag.controller;

import com.FINAL.KIP.document.dto.res.JustDocTitleResDto;
import com.FINAL.KIP.hashtag.dto.req.HashTagReqDto;
import com.FINAL.KIP.hashtag.dto.req.UpdateHashTagsReqDto;
import com.FINAL.KIP.hashtag.dto.res.HashTagResDto;
import com.FINAL.KIP.hashtag.service.HashTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("hashtag")
public class HashTagController {

    public final HashTagService hashTagService;

    @Autowired
    public HashTagController(HashTagService hashTagService) {
        this.hashTagService = hashTagService;
    }

    //  Create
    @PostMapping
    public ResponseEntity<List<HashTagResDto>> createHashTags(@RequestBody List<String> dtos) {
        return ResponseEntity.ok(hashTagService.createHashTags(dtos));
    }

    //  Read
    @GetMapping
    public ResponseEntity<List<HashTagResDto>> getAllHashTags() {
        return ResponseEntity.ok(hashTagService.getAllHashTags());
    }

    @GetMapping("{HashTagId}/docs")
    public ResponseEntity<List<JustDocTitleResDto>> getDocumentsByHashTag(@PathVariable Long HashTagId){
        return ResponseEntity.ok(hashTagService.getDocumentsByHashTag(HashTagId));
    }

    // Update
    @PatchMapping
    public ResponseEntity<List<HashTagResDto>> updateHashTags(@RequestBody UpdateHashTagsReqDto dto){
        return ResponseEntity.ok(hashTagService.updateHashTags(dto));
    }

    // Delete
    @DeleteMapping("{HashTagId}/delete")
    public ResponseEntity<Void> deleteHashTag(@PathVariable Long HashTagId) {
        hashTagService.deleteHashTag(HashTagId);
        return ResponseEntity.noContent().build();
    }
}
