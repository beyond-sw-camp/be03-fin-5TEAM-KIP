package com.FINAL.KIP.hashtag.repository;

import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.hashtag.domain.DocHashTag;
import com.FINAL.KIP.hashtag.domain.DocHashTagId;
import com.FINAL.KIP.hashtag.domain.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocHashTagRepository extends JpaRepository<DocHashTag, DocHashTagId> {
    List<DocHashTag> findByDocument(Document document);
    List<DocHashTag> findByHashTag(HashTag hashTag);
}
