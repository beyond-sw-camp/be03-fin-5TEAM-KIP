package com.FINAL.KIP.document.repository;

import com.FINAL.KIP.document.domain.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

	Optional<Document> findByUuid(UUID uuid);
}
