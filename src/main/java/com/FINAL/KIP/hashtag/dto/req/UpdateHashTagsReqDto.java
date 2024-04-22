package com.FINAL.KIP.hashtag.dto.req;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UpdateHashTagsReqDto {

    private Long documentId;
    private List<String> hashTags;

}
