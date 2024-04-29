package com.FINAL.KIP.user.dto.res;

import com.FINAL.KIP.bookmark.domain.Book;
import lombok.Data;

@Data
public class BookResDto {
    private Long documentId;
    private String title;
    private String groupName;

    public BookResDto(Book book){
        this.documentId = book.getDocument().getId();
        this.title = book.getDocument().getTitle();
        this.groupName = book.getDocument().getGroup().getGroupName();

    }
}
