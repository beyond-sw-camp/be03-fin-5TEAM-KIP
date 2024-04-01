package com.FINAL.KIP.hashtag.dto.req;


import com.FINAL.KIP.hashtag.domain.HashTag;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateHashTagReqDto {
    private String tagName;

    public HashTag makeHashTagFromTagReqDto(){
        HashTag hashTag = new HashTag();
        hashTag.setTagName(this.tagName);
        return hashTag;
    }
}
