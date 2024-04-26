package com.FINAL.KIP.bookmark.dto;

import com.FINAL.KIP.bookmark.domain.Book;
import lombok.Data;

@Data
public class BookListResDto {
    private Long documentId;
    private String title;
    private String groupName;

    public BookListResDto(Book book){
        this.documentId = book.getDocument().getId();
        this.title = book.getDocument().getTitle();
        if(book.getDocument().getGroup() != null){
            this.groupName = book.getDocument().getGroup().getGroupName();
        }else{
            this.groupName = "전체그룹문서";
        }
    }
}
