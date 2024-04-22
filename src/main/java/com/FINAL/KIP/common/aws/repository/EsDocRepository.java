package com.FINAL.KIP.common.aws.repository;

import com.FINAL.KIP.common.aws.domain.EsDoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EsDocRepository extends ElasticsearchRepository<EsDoc, String> {

	@Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title\", \"content\"]}}")
	Page<EsDoc> findByTitleOrContentUsingMultiMatch(String text, Pageable pageable);

}
