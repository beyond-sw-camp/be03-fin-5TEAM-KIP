package com.FINAL.KIP.hashtag.service;

import com.FINAL.KIP.common.aspect.JustAdmin;
import com.FINAL.KIP.common.aspect.UserAdmin;
import com.FINAL.KIP.hashtag.domain.HashTag;
import com.FINAL.KIP.hashtag.dto.req.CreateHashTagReqDto;
import com.FINAL.KIP.hashtag.dto.res.HashTagResDto;
import com.FINAL.KIP.hashtag.repository.HashTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HashTagService {

    public final HashTagRepository hashTagRepo;

    @Autowired
    public HashTagService(HashTagRepository hashTagRepo) {
        this.hashTagRepo = hashTagRepo;
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
}
