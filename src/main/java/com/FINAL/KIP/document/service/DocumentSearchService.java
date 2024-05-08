package com.FINAL.KIP.document.service;

import com.FINAL.KIP.common.aws.domain.EsDoc;
import com.FINAL.KIP.common.aws.repository.EsDocRepository;
import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.document.repository.DocumentRepository;
import com.FINAL.KIP.group.domain.Group;
import com.FINAL.KIP.group.repository.GroupUserRepository;
import com.FINAL.KIP.request.repository.RequestRepository;
import com.FINAL.KIP.user.domain.User;
import com.FINAL.KIP.user.repository.UserRepository;
import com.FINAL.KIP.version.repository.VersionRepository;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DocumentSearchService {

	private final EsDocRepository esDocRepository;
	private final DocumentRepository documentRepository;
	private final RestHighLevelClient opensearchClient;
	private final VersionRepository versionRepository;

	private final UserRepository userRepository;
	private final GroupUserRepository groupUserRepository;

	private final RequestRepository requestRepository;

	@Autowired
	public DocumentSearchService(EsDocRepository esDocRepository,
		DocumentRepository documentRepository,
		RestHighLevelClient opensearchClient, VersionRepository versionRepository,
		UserRepository userRepository, GroupUserRepository groupUserRepository, RequestRepository requestRepository) {
		this.esDocRepository = esDocRepository;
		this.documentRepository = documentRepository;
		this.opensearchClient = opensearchClient;
		this.versionRepository = versionRepository;
		this.userRepository = userRepository;
		this.groupUserRepository = groupUserRepository;
		this.requestRepository = requestRepository;
	}

	public String test() throws IOException {
		Request request = new Request("GET", "/document/_search");
		Response response = opensearchClient.getLowLevelClient().performRequest(request);
		return EntityUtils.toString(response.getEntity());
	}

//	@Scheduled(cron = "0 0 0/4 * * *")
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
			.groupName(Optional.ofNullable(document.getGroup())
				.map(Group::getGroupName)
				.orElse("전체공개"))
			.build();
	}

	private String getCurrentVersion(Document document) {
		return versionRepository.findByDocumentAndIsShow(document, "Y")
			.orElseThrow(() -> new IllegalArgumentException("해당 문서의 내용이 존재하지 않습니다.")).getContent();
	}

	public ResponseEntity<?> searchDocs(String keyword, int pageNumber) {
		Pageable pageable = PageRequest.of(pageNumber, 10);
		Page<EsDoc> byTitleOrContentUsingMultiMatch = esDocRepository.findByTitleOrContentUsingMultiMatch(
			keyword, pageable);
		return new ResponseEntity<>(byTitleOrContentUsingMultiMatch, HttpStatus.OK);
	}

	public ResponseEntity<?> viewDocs(String documentUUID) {
		Document documentByUuid = findDocumentByUuid(documentUUID);
		User user = getUserFromAuthentication();
		if(Optional.ofNullable(documentByUuid.getGroup()).isEmpty()) {
			return new ResponseEntity<>(Map.of("groupId", "",
				"result", "Public Document"), HttpStatus.OK);
		}
		if (groupUserRepository.findByGroupAndUser(documentByUuid.getGroup(), user).isPresent()) {
			return new ResponseEntity<>(Map.of("groupId", documentByUuid.getGroup().getId(),
				"result", "Group User"), HttpStatus.OK);
		}
		if (requestRepository.availableDocument(user, documentByUuid)) {
			return new ResponseEntity<>(Map.of("groupId", documentByUuid.getGroup().getId(),
				"result", "Available User"), HttpStatus.OK);
		}

		return new ResponseEntity<>(Map.of("groupId", documentByUuid.getGroup().getId(),
			"result", "Unavailable User"), HttpStatus.OK);
	}

	public User getUserFromAuthentication() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return userRepository.findByEmployeeId(authentication.getName())
			.orElseThrow(() -> new IllegalArgumentException("예상치 못한 에러가 발생했습니다."));
	}

	public Document findDocumentByUuid(String documentUUID) {
		UUID uuid = UUID.fromString(documentUUID);
		return documentRepository.findByUuid(uuid).orElseThrow(
			() -> new IllegalArgumentException("해당 문서가 존재하지 않습니다.")
		);
	}
}