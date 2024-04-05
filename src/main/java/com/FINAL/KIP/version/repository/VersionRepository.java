package com.FINAL.KIP.version.repository;

import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.version.domain.Version;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VersionRepository extends JpaRepository<Version, Long> {
	Optional<Version> findByDocumentAndIsShow(Document document , String isShow);

}
