package com.FINAL.KIP.hashtag.dto.req;

import com.FINAL.KIP.hashtag.domain.HashTag;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class addHashTagsToDocReqDto {

    private Long documentId;
    private List<HashTag> hashTags;

}
