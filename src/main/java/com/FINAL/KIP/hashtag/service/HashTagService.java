package com.FINAL.KIP.hashtag.service;

import com.FINAL.KIP.common.aspect.JustAdmin;
import com.FINAL.KIP.common.aspect.UserAdmin;
import com.FINAL.KIP.document.dto.res.JustDocTitleResDto;
import com.FINAL.KIP.hashtag.domain.DocHashTag;
import com.FINAL.KIP.hashtag.domain.HashTag;
import com.FINAL.KIP.hashtag.dto.req.CreateHashTagReqDto;
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

    @Autowired
    public HashTagService(HashTagRepository hashTagRepo,
                          DocHashTagRepository docHashTagRepo) {
        this.hashTagRepo = hashTagRepo;
        this.docHashTagRepo = docHashTagRepo;
    }


    // Create
    @UserAdmin
    public List<HashTagResDto> createHashTags(List<CreateHashTagReqDto> dtos) {
        List<HashTag> hashTags = new ArrayList<>();
        for (CreateHashTagReqDto tagDto : dtos)
            hashTags.add(tagDto.makeHashTagFromTagReqDto());

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

    public List<JustDocTitleResDto> getDocumentsByHashTag(Long hashTagId) {
        return docHashTagRepo.findByHashTag(getHashTagByHashTagId(hashTagId)).stream()
                .map(DocHashTag::getDocument)
                .map(JustDocTitleResDto::new)
                .collect(Collectors.toList());
    }
}
