package com.FINAL.KIP.bookmark.repository;

import com.FINAL.KIP.bookmark.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findBooksByEmployeeIdAndDocumentId(String employeeId, Long documentId);
    List<Book> findDocumentIdByEmployeeId(String employeeId);

}
