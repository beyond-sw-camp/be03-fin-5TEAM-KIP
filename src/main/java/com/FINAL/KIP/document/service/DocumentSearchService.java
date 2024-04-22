package com.FINAL.KIP.document.service;

import com.FINAL.KIP.common.aws.domain.EsDoc;
import com.FINAL.KIP.common.aws.repository.EsDocRepository;
import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.document.repository.DocumentRepository;
import com.FINAL.KIP.version.repository.VersionRepository;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.http.util.EntityUtils;
import org.opensearch.client.Request;
import org.opensearch.client.Response;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class DocumentSearchService {

	private final EsDocRepository esDocRepository;
	private final DocumentRepository documentRepository;
	private final RestHighLevelClient opensearchClient;
	private final VersionRepository versionRepository;

	@Autowired
	public DocumentSearchService(EsDocRepository esDocRepository,
		DocumentRepository documentRepository,
		RestHighLevelClient opensearchClient, VersionRepository versionRepository) {
		this.esDocRepository = esDocRepository;
		this.documentRepository = documentRepository;
		this.opensearchClient = opensearchClient;
		this.versionRepository = versionRepository;
	}

	public String test() throws IOException {
		Request request = new Request("GET", "/document/_search");
		Response response = opensearchClient.getLowLevelClient().performRequest(request);
		return EntityUtils.toString(response.getEntity());
	}

	public void addAll() {
		List<Document> list = documentRepository.findAll();
		List<EsDoc> collect = list.stream().map(this::convertToEsDoc).collect(Collectors.toList());
		esDocRepository.saveAll(collect);
	}

	private EsDoc convertToEsDoc(Document document) {
		return EsDoc.builder()
			.id(String.valueOf(document.getId()))
			.title(document.getTitle())
			.content(getCurrentVersion(document))
			.uuid(document.getUuid().toString())
			.groupName(document.getGroup().getGroupName())
			.build();
	}

	private String getCurrentVersion(Document document) {
		return versionRepository.findByDocumentAndIsShow(document, "Y")
			.orElseThrow(() -> new IllegalArgumentException("해당 문서의 내용이 존재하지 않습니다.")).getContent();
	}

	public ResponseEntity<?> searchDocs(String keyword, int pageNumber) {
		Pageable pageable = PageRequest.of(pageNumber, 5);
		Page<EsDoc> byTitleOrContentUsingMultiMatch = esDocRepository.findByTitleOrContentUsingMultiMatch(
			keyword, pageable);
		return new ResponseEntity<>(byTitleOrContentUsingMultiMatch, HttpStatus.OK);
	}
}