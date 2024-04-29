package com.FINAL.KIP.bookmark.repository;

import com.FINAL.KIP.bookmark.domain.Book;
import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
//    List<Book> findBooksByEmployeeIdAndDocumentId(String employeeId, Long documentId);
    List<Book> findByUserAndDocument(User user, Document document);
    List<Book> findByUser(User user);

}
