package com.FINAL.KIP.document.dto.res;

import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.document.domain.KmsDocType;
import com.FINAL.KIP.hashtag.domain.DocHashTag;
import com.FINAL.KIP.hashtag.dto.res.HashTagResDto;
import lombok.Getter;

import java.util.List;

@Getter
public class DocumentResDto {

    private final Long documentId;
    private final String title;
    private final KmsDocType kmsDocType;
    private Long upLinkId;
    private String upDocTitle;
    private Long downLinkId;
    private String downDocTitle;
    private Long groupId;
    private String groupName;
    private List<HashTagResDto> HashTags;

    public DocumentResDto(Document document) {
        this.documentId = document.getId();
        this.title = document.getTitle();
        this.kmsDocType = document.getKmsDocType();

        if (document.getUpLink() != null) {
            this.upLinkId = document.getUpLink().getId();
            this.upDocTitle = document.getUpLink().getTitle();
        }

        if (document.getDownLink() != null) {
            this.downLinkId = document.getDownLink().getId();
            this.downDocTitle = document.getDownLink().getTitle();
        }
        if (document.getGroup() != null) {
            this.groupId = document.getGroup().getId();
            this.groupName = document.getGroup().getGroupName();
        }
        
        if (document.getDocHashTags() != null) {
            this.HashTags = document.getDocHashTags().stream()
                    .map(DocHashTag::getHashTag)
                    .map(HashTagResDto::new)
                    .toList();
        }
    }
}
