package com.FINAL.KIP.bookmark.dto;

import com.FINAL.KIP.bookmark.domain.Book;
import com.FINAL.KIP.hashtag.domain.DocHashTag;
import com.FINAL.KIP.hashtag.domain.HashTag;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class BookListResDto {
    private Long documentId;
    private String title;
    private String groupName;
    private List<Long> HashTagIds;

    public BookListResDto(Book book) {
        this.documentId = book.getDocument().getId();
        this.title = book.getDocument().getTitle();
        if (book.getDocument().getGroup() != null)
            this.groupName = book.getDocument().getGroup().getGroupName();
        else this.groupName = "전체공개문서";
        this.HashTagIds = book.getDocument().getDocHashTags().stream()
                .map(DocHashTag::getHashTag)
                .map(HashTag::getId)
                .collect(Collectors.toList());
    }
}
