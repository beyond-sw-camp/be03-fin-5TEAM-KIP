package com.FINAL.KIP.bookmark.repository;

import com.FINAL.KIP.bookmark.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findBooksByEmployeeIdAndDocumentId(String employeeId, Long documentId);
    @Query("SELECT b.document.id, b.title FROM Book b WHERE b.employeeId = :employeeId")
    List<Object[]> findDocumentIdAndTitleByEmployeeId(@Param("employeeId") String employeeId);

}
