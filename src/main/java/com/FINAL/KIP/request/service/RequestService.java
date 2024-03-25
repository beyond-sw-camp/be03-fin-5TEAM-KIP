package com.FINAL.KIP.request.service;

import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.document.repository.DocumentRepository;
import com.FINAL.KIP.request.domain.Request;
import com.FINAL.KIP.request.dto.request.RequestCreateReqDto;
import com.FINAL.KIP.request.dto.response.RequestCreateResDto;
import com.FINAL.KIP.request.repository.RequestRepository;
import com.FINAL.KIP.user.domain.User;
import com.FINAL.KIP.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RequestService {

	private final RequestRepository requestRepository;
	private final DocumentRepository documentRepository;
	private final UserRepository userRepository;

	@Autowired
	public RequestService(RequestRepository requestRepository,
		DocumentRepository documentRepository,
		UserRepository userRepository) {
		this.requestRepository = requestRepository;
		this.documentRepository = documentRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	public ResponseEntity<RequestCreateResDto> createRequest(RequestCreateReqDto requestCreateReqDto) {

		Document document = findDocumentByUuid(requestCreateReqDto.getDocId());
		User user = findUserById(requestCreateReqDto.getUserId());
		Request request = Request.builder()
			.requesterId(user)
			.dueDate(LocalDateTime.now().plusDays(requestCreateReqDto.getExpiration()))
			.documentId(document)
			.groupId(document.getGroup())
			.build();
		requestRepository.save(request);

		RequestCreateResDto requestCreateResDto = new RequestCreateResDto();
		requestCreateResDto.setDocTitle(document.getTitle());
		requestCreateResDto.setRequestTime(request.getDueDate());
		return new ResponseEntity<>(requestCreateResDto, HttpStatus.CREATED);
	}


	public User findUserById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
	}
	public Document findDocumentByUuid(String uuid) {
		UUID id = UUID.fromString(uuid);
		return documentRepository.findByUuid(id)
			.orElseThrow(() -> new IllegalArgumentException("해당되는 문서가 존재하지 않습니다"));
	}
}