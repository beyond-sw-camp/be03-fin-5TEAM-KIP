package com.FINAL.KIP.attachedfile.repository;

import com.FINAL.KIP.attachedfile.domain.AttachedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 파일 메타데이터에 대한 CRUD 연산을 수행하는 JPA 리포지토리 인터페이스입니다.
 * Spring Data JPA가 제공하는 JpaRepository를 상속받아 구현됩니다.
 */
@Repository
public interface AttachedFileRepository extends JpaRepository<AttachedFile, Long> {
}