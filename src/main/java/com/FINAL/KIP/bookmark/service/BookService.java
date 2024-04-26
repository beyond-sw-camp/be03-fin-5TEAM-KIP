package com.FINAL.KIP.bookmark.service;

import com.FINAL.KIP.bookmark.domain.Book;
import com.FINAL.KIP.bookmark.dto.BookListResDto;
import com.FINAL.KIP.bookmark.dto.BookResDto;
import com.FINAL.KIP.bookmark.repository.BookRepository;
import com.FINAL.KIP.common.aspect.UserAdmin;
import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.document.repository.DocumentRepository;
import com.FINAL.KIP.group.repository.GroupRepository;
import com.FINAL.KIP.user.domain.User;
import com.FINAL.KIP.user.repository.UserRepository;
import com.FINAL.KIP.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final DocumentRepository documentRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepo;
    private final BookRepository bookRepository;
    private final UserService userService;

    public BookService(DocumentRepository documentRepository, GroupRepository groupRepository, UserRepository userRepo, BookRepository bookRepository, UserService userService) {
        this.documentRepository = documentRepository;
        this.groupRepository = groupRepository;
        this.userRepo = userRepo;
        this.bookRepository = bookRepository;
        this.userService = userService;
    }

    @UserAdmin
    @Transactional
    public BookResDto docBookMark(Long documentId){
        Document document = documentRepository.findById(documentId).orElseThrow(() -> new EntityNotFoundException("없는 문서입니다."));

        User user = userService.getUserFromAuthentication();

        List<Book> bookList = bookRepository.findByUserAndDocument(user, document);
        BookResDto bookResDto;

        if (bookList.isEmpty()) {
            Book book = Book.builder()
                    .document(document)
                    .user(user)
                    .build();
            bookRepository.save(book);
            bookResDto = BookResDto.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("success")
                    .result(HttpStatus.OK)
                    .build();
            document.addBookCount();
        } else {
            Book book = bookList.get(0);
            bookRepository.delete(book);
            bookResDto = BookResDto.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("cancel")
                    .result(HttpStatus.OK)
                    .build();
            document.reduceLikeCount();
        }
        return bookResDto;
    }

    @UserAdmin
    public BookResDto docBookCount(Long documentId){
        Document document = documentRepository.findById(documentId).orElseThrow(() -> new EntityNotFoundException("없는 문서입니다."));
        BookResDto bookResDto;
        bookResDto = BookResDto.builder()
                .httpStatus(HttpStatus.OK)
                .message("BookMarkCount")
                .result(document.getBookCount())
                .build();
        return bookResDto;
    }

    @UserAdmin
    public List<BookListResDto> userBookList() {
        User user = userService.getUserFromAuthentication();

        List<Book> books = bookRepository.findByUser(user);

        // Stream API를 사용하여 각 Book 객체를 BookListResDto로 변환합니다.
        return books.stream()
                .map(BookListResDto::new)  // Book 객체를 BookListResDto 생성자에 전달
                .collect(Collectors.toList());
    }
}
