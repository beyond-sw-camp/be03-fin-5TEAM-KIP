package com.FINAL.KIP.bookmark.service;

import com.FINAL.KIP.bookmark.domain.Book;
import com.FINAL.KIP.bookmark.dto.BookResDto;
import com.FINAL.KIP.bookmark.repository.BookRepository;
import com.FINAL.KIP.document.domain.Document;
//import com.FINAL.KIP.document.repository.DocBookReportRepository;
import com.FINAL.KIP.document.repository.DocumentRepository;
import com.FINAL.KIP.user.domain.User;
import com.FINAL.KIP.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public BookService(DocumentRepository documentRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public BookResDto docBookMark(Long documentId){
        Document document = documentRepository.findById(documentId).orElseThrow(() -> new EntityNotFoundException("없는 문서입니다."));

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        String[] split = name.split(":");
        String employeeId = split[0];
        User user = userRepository.findByEmployeeId(employeeId).orElseThrow(() -> new EntityNotFoundException("user not found"));

        List<Book> bookList = bookRepository.findBooksByEmployeeIdAndDocumentId(employeeId, documentId);
        BookResDto bookResDto;

        if (bookList.isEmpty()) {
            Book book = Book.builder()
                    .document(document)
                    .user(user)
                    .employeeId(employeeId)
                    .build();
            bookRepository.save(book);
            bookResDto = BookResDto.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("success")
                    .result(HttpStatus.OK)
                    .build();
            document.addBookCount();

//            DocBookReport docBookReport = new DocBookReport(book.getDocument(), book.getEmployeeId());
//            docBookReportRepository.save(docBookReport);

        } else {
            Book book = bookList.get(0);
            bookRepository.delete(book);
            bookResDto = BookResDto.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("cancel")
                    .result(HttpStatus.OK)
                    .build();
            document.reduceLikeCount();

//            DocBookReport findDocBookReport = docBookReportRepository.findByDocumentIdAndEmployeeId(book.getDocument().getId(), book.getEmployeeId());
//            docBookReportRepository.delete(findDocBookReport);
        }
        return bookResDto;
    }

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
}
