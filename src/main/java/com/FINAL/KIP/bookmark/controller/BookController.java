package com.FINAL.KIP.bookmark.controller;

import com.FINAL.KIP.bookmark.dto.BookResDto;
import com.FINAL.KIP.bookmark.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController {
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("doc/{DocumentId}/book")
    public ResponseEntity<BookResDto> docBookMark(@PathVariable Long DocumentId){
        BookResDto bookResDto = bookService.docBookMark(DocumentId);
        return new ResponseEntity<>(bookResDto, bookResDto.getHttpStatus());
    }

    @GetMapping("doc/{DocumentId}/book/count")
    public ResponseEntity<BookResDto> docBookCount(@PathVariable Long DocumentId){
        BookResDto bookResDto = bookService.docBookCount(DocumentId);
        return new ResponseEntity<>(bookResDto, bookResDto.getHttpStatus());
    }
}
