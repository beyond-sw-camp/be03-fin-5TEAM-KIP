package com.FINAL.KIP.hashtag.dto.res;

import com.FINAL.KIP.hashtag.domain.HashTag;
import lombok.Getter;

@Getter
public class HashTagResDto {

    private final Long hashTagId;
    private final String tagName;
    private final int DocsCounts;

    public HashTagResDto (HashTag hashTag){
        this.hashTagId = hashTag.getId();
        this.tagName = hashTag.getTagName();
        DocsCounts = hashTag.getDocHashTags().size();
    }
}
