package com.FINAL.KIP.hashtag.service;

import com.FINAL.KIP.common.aspect.JustAdmin;
import com.FINAL.KIP.common.aspect.UserAdmin;
import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.document.dto.res.JustDocTitleResDto;
import com.FINAL.KIP.document.repository.DocumentRepository;
import com.FINAL.KIP.hashtag.domain.DocHashTag;
import com.FINAL.KIP.hashtag.domain.HashTag;
import com.FINAL.KIP.hashtag.dto.req.HashTagReqDto;
import com.FINAL.KIP.hashtag.dto.req.UpdateHashTagsReqDto;
import com.FINAL.KIP.hashtag.dto.res.HashTagResDto;
import com.FINAL.KIP.hashtag.repository.DocHashTagRepository;
import com.FINAL.KIP.hashtag.repository.HashTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class HashTagService {

    public final HashTagRepository hashTagRepo;
    public final DocHashTagRepository docHashTagRepo;
    public final DocumentRepository documentRepo;

    @Autowired
    public HashTagService(HashTagRepository hashTagRepo,
                          DocHashTagRepository docHashTagRepo,
                          DocumentRepository documentRepo) {
        this.hashTagRepo = hashTagRepo;
        this.docHashTagRepo = docHashTagRepo;
        this.documentRepo = documentRepo;
    }


    // Create
    @UserAdmin
    public List<HashTagResDto> createHashTags(List<String> dtos) {
        List<HashTag> hashTags = new ArrayList<>();
        for (String tagDto : dtos){
            HashTag newHashTag = HashTag.builder()
                .tagName(tagDto).build();
            hashTags.add(newHashTag);
        }

        List<HashTag> distinctTags = hashTags.stream()  // 중복제거
                .filter(tag -> hashTagRepo
                        .findByTagName(tag.getTagName())
                        .isEmpty())
                .toList();

        return hashTagRepo.saveAll(distinctTags).stream()
                .map(HashTagResDto::new)
                .collect(Collectors.toList());  // 중복제거 후 저장된 값만 반환
    }

    //  Read
    @UserAdmin
    public List<HashTagResDto> getAllHashTags() {
        return hashTagRepo.findAll().stream()
                .map(HashTagResDto::new)
                .collect(Collectors.toList());
    }

    @UserAdmin
    public List<JustDocTitleResDto> getDocumentsByHashTag(Long hashTagId) {
        return docHashTagRepo.findByHashTag(getHashTagByHashTagId(hashTagId)).stream()
                .map(DocHashTag::getDocument)
                .map(JustDocTitleResDto::new)
                .collect(Collectors.toList());
    }

    public List<HashTagResDto> updateHashTags(UpdateHashTagsReqDto dto) {
        Document targetDocument = documentRepo.findById(
                dto.getDocumentId()).orElseThrow(
                        ()-> new NoSuchElementException("찾는 문서가 없습니다."));
        List<DocHashTag> docHashTags = docHashTagRepo.findByDocument(targetDocument);
        docHashTagRepo.deleteAll(docHashTags); // 기존 문서에 연결된 해시정보 모두 삭제
        generateDocHashTags(dto.getHashTags(), targetDocument); // 해시 저장 및 문서에 연결
        return documentRepo.save(targetDocument).getDocHashTags().stream()
                .map(DocHashTag::getHashTag)
                .map(HashTagResDto::new)
                .toList();
    }

    //  Delete
    @JustAdmin
    public void deleteHashTag(Long hashTagId) {
        hashTagRepo.deleteById(hashTagId);
    }

    //  함수공통화
    public HashTag getHashTagByTagName(String tagName) {
        return hashTagRepo.findByTagName(tagName).orElseThrow(
                ()-> new NoSuchElementException(tagName + "해당 태그 이름 존재하지 않습니다."));
    }

    public HashTag getHashTagByHashTagId (Long hashTagId){
        return hashTagRepo.findById(hashTagId).orElseThrow(
                ()-> new NoSuchElementException("해당 태그 Id가 존재하지 않습니다. "));
    }

    public void generateDocHashTags(List<String> hashTags, Document document) {
        createHashTags(hashTags);  // 중복빼고 저장.
        List<DocHashTag> docHashTags = hashTags.stream()
                .map(req -> getHashTagByTagName(req))
                .map(hashTag -> new DocHashTag(document, hashTag))
                .toList();
        document.addAllDocHashTags(docHashTags);
    }

}
