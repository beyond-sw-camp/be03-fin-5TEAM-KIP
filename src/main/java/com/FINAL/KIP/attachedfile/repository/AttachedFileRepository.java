package com.FINAL.KIP.attachedfile.repository;

import com.FINAL.KIP.attachedfile.domain.AttachedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // 스프링에게 이 인터페이스가 데이터 접근 계층의 컴포넌트임을 나타냅니다.
public interface AttachedFileRepository extends JpaRepository<AttachedFile, Long> {
    // JpaRepository를 상속받아 기본적인 CRUD 연산 메서드를 사용할 수 있습니다.
    // 필요에 따라 추가적인 메서드를 정의할 수 있습니다.

    // 임시 파일 ID를 기반으로 파일 엔티티를 조회하는 새로운 메소드 추가
    Optional<AttachedFile> findByTempFileId(String tempFileId);
}