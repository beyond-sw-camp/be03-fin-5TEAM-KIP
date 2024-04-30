package com.FINAL.KIP.version.service;

import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.document.repository.DocumentRepository;
import com.FINAL.KIP.user.domain.User;
import com.FINAL.KIP.user.repository.UserRepository;
import com.FINAL.KIP.version.domain.Version;
import com.FINAL.KIP.version.dto.request.VersionCreateReqDto;
import com.FINAL.KIP.version.dto.request.VersionPostReqDto;
import com.FINAL.KIP.version.repository.VersionRepository;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VersionService {

	private final UserRepository userRepository;
	private final VersionRepository versionRepository;

	private final DocumentRepository documentRepository;

	@Autowired
	public VersionService(UserRepository userRepository,
		VersionRepository versionRepository, DocumentRepository documentRepository) {
		this.userRepository = userRepository;
		this.versionRepository = versionRepository;
		this.documentRepository = documentRepository;
	}

	@Transactional
	public void createNewVersion(VersionCreateReqDto versionCreateReqDto) {
		String employeeId = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = findUserByEmployeeId(employeeId);

		Version newVersion = Version.builder()
			.content(versionCreateReqDto.getContent())
			.writer(user)
			.document(versionCreateReqDto.getDocument()).build();
		versionRepository.save(newVersion);
	}



	public User findUserByEmployeeId(String employeeId) {
		return userRepository.findByEmployeeId(employeeId).orElseThrow(
			() -> new IllegalArgumentException("예상치 못한 에러가 발생했습니다.")
		);
	}

	public Document findDocumentById(Long documentId) {
		return documentRepository.findById(documentId).orElseThrow(
			() -> new IllegalArgumentException("해당 ID를 가진 문서가 존재하지 않습니다.")
		);
	}

	public Version findShowVersionByDocument(Document document) {
		return versionRepository.findByDocumentAndIsShow(document, "Y").orElseThrow(
			() -> new IllegalArgumentException("예상치 못한 에러가 발생하였습니다.")
		);
	}

	@Transactional
	public ResponseEntity<Object> postNewVersion(VersionPostReqDto versionPostReqDto) {
		String employeeId = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = findUserByEmployeeId(employeeId);
		Document document = findDocumentById(versionPostReqDto.getDocumentId());

		findShowVersionByDocument(document).updateIsShow();
		Version newVersion = Version.builder()
			.document(document)
			.writer(user)
			.content(versionPostReqDto.getContent()).build();

		versionRepository.save(newVersion);

		return new ResponseEntity<>(null, HttpStatus.CREATED);
	}

	@Transactional(readOnly = true)
	public ResponseEntity<Map<String, Object>> getVersionDetail(Long versionId) {
		Version versionById = findVersionById(versionId);

		return ResponseEntity.ok(Map.of("content", versionById.getContent()));
	}

	public Version findVersionById(Long versionId) {
		return versionRepository.findById(versionId).orElseThrow(
			() -> new IllegalArgumentException("해당 버전이 존재하지 않습니다.")
		);
	}
}