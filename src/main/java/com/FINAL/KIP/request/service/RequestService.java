package com.FINAL.KIP.request.service;

import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.document.repository.DocumentRepository;
import com.FINAL.KIP.group.domain.Group;
import com.FINAL.KIP.group.domain.GroupRole;
import com.FINAL.KIP.group.domain.GroupUser;
import com.FINAL.KIP.group.domain.GroupUserId;
import com.FINAL.KIP.group.repository.GroupUserRepository;
import com.FINAL.KIP.note.dto.request.NoteAgreeReqDto;
import com.FINAL.KIP.note.dto.request.NoteCreateReqDto;
import com.FINAL.KIP.note.dto.request.NoteRefuseReqDto;
import com.FINAL.KIP.note.service.NoteService;
import com.FINAL.KIP.request.domain.Request;
import com.FINAL.KIP.request.dto.request.RequestCreateReqDto;
import com.FINAL.KIP.request.dto.response.RequestAgreeResDto;
import com.FINAL.KIP.request.dto.response.RequestCreateResDto;
import com.FINAL.KIP.request.dto.response.RequestRefuseResDto;
import com.FINAL.KIP.request.repository.RequestRepository;
import com.FINAL.KIP.user.domain.User;
import com.FINAL.KIP.user.domain.UserDocAuthorityId;
import com.FINAL.KIP.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RequestService {
	private final GroupUserRepository groupUserRepository;

	private final RequestRepository requestRepository;
	private final DocumentRepository documentRepository;
	private final UserRepository userRepository;
	private final NoteService noteService;

	@Autowired
	public RequestService(GroupUserRepository groupUserRepository,
		RequestRepository requestRepository,
		DocumentRepository documentRepository, UserRepository userRepository,
		NoteService noteService) {
		this.groupUserRepository = groupUserRepository;
		this.requestRepository = requestRepository;
		this.documentRepository = documentRepository;
		this.userRepository = userRepository;
		this.noteService = noteService;
	}

	@Transactional
	public ResponseEntity<RequestCreateResDto> createRequest(
		RequestCreateReqDto requestCreateReqDto) throws IllegalArgumentException {

		Document document = findDocumentByUuid(requestCreateReqDto.getDocId());
		String userEmployeeId = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = findByEmployeeId(userEmployeeId);

		if (requestRepository.existsRequestByRequesterAndDocumentAndIsOk(user, document, "P")) {
			throw new IllegalArgumentException("이미 해당 문서에 접근 권한요청을 했습니다.");
		}
		if (duplicateAuthority(user, document)) {
			throw new IllegalArgumentException("이미 해당 문서의 접근권한을 가지고 있습니다");
		}
		Request request = Request.builder()
			.requester(user)
			.document(document)
			.group(document.getGroup())
			.days(requestCreateReqDto.getDays())
			.build();
		requestRepository.save(request);
		NoteCreateReqDto noteCreateReqDto = new NoteCreateReqDto(document, document.getGroup(), user);
		noteService.createNewRequestNote(noteCreateReqDto);

		RequestCreateResDto requestCreateResDto = new RequestCreateResDto();
		requestCreateResDto.setDocTitle(document.getTitle());
		requestCreateResDto.setDays(request.getDays());
		return new ResponseEntity<>(requestCreateResDto, HttpStatus.CREATED);
	}


	private User findUserById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
	}
	private Document findDocumentByUuid(String uuid) {
		UUID id = UUID.fromString(uuid);
		return documentRepository.findByUuid(id)
			.orElseThrow(() -> new IllegalArgumentException("해당되는 문서가 존재하지 않습니다"));
	}

	private boolean duplicateAuthority(User user, Document document) {
		Group group = document.getGroup();

		boolean authorityCheck = false;

		GroupUser groupUser = findGroupUser(group, user);

		if(groupUser != null) {
			authorityCheck = true;
		}

		return authorityCheck;
	}


	private GroupUser findGroupUser(Group group, User user) {
		return groupUserRepository.findByGroupAndUser(group, user).orElse(null);
	}


	@Transactional
	public ResponseEntity<RequestRefuseResDto> refuseRequest(Long requestId) {
		Request req = findById(requestId);
		if (!req.getIsOk().equals("P")) {
			throw new IllegalArgumentException("대기 상태의 요청만 수락 또는 거절이 가능합니다.");
		}
		String employeeId = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = findByEmployeeId(employeeId);

		Group group = req.getGroup();

		GroupUser groupUser = findGroupUser(group, user);
		if(groupUser == null) {
			throw new IllegalArgumentException("해당 그룹 요청에 접근 권한이 존재하지 않습니다.");
		}
		if (groupUser.getGroupRole() != GroupRole.SUPER) {
			throw new IllegalArgumentException("그룹의 관리자만 요청을 관리할 수 있습니다.");
		}

		req.refuseRequest();
		noteService.createRefuseRequestNote(
			new NoteRefuseReqDto(req.getRequester(), req.getDocument(), user));

		RequestRefuseResDto requestRefuseResDto = new RequestRefuseResDto();
		requestRefuseResDto.setTitle(req.getDocument().getTitle());
		requestRefuseResDto.setName(req.getRequester().getName());
		return ResponseEntity.ok(requestRefuseResDto);
	}

	private Request findById(Long requestId) {
		return requestRepository.findById(requestId)
			.orElseThrow(() -> new IllegalArgumentException("해당 요청이 존재하지 않습니다."));
	}

	private User findByEmployeeId(String employeeId) {
		return userRepository.findByEmployeeId(employeeId)
			.orElseThrow(() -> new IllegalArgumentException("사번이 잘못되었습니다."));
	}

	@Transactional
	public ResponseEntity<RequestAgreeResDto> agreeRequest(Long requestId) {
		Request req = findById(requestId);

		if (!req.getIsOk().equals("P")) {
			throw new IllegalArgumentException("대기 상태의 요청만 수락 또는 거절이 가능합니다.");
		}

		String employeeId = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = findByEmployeeId(employeeId);
		Group group = req.getGroup();

		GroupUser groupUser = findGroupUser(group, user);
		if(groupUser == null) {
			throw new IllegalArgumentException("해당 그룹 요청에 접근 권한이 존재하지 않습니다.");
		}
		if (groupUser.getGroupRole() != GroupRole.SUPER) {
			throw new IllegalArgumentException("그룹의 관리자만 요청을 관리할 수 있습니다.");
		}

		req.agreeRequest();
		noteService.createAgreeRequestNote(
			new NoteAgreeReqDto(req.getRequester(), req.getDocument(), user));

		RequestAgreeResDto requestAgreeResDto = new RequestAgreeResDto();
		requestAgreeResDto.setName(req.getRequester().getName());
		requestAgreeResDto.setDueDate(req.getDueDate());
		requestAgreeResDto.setTitle(req.getDocument().getTitle());

		return ResponseEntity.ok(requestAgreeResDto);
	}
}