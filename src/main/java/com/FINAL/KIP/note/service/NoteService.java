package com.FINAL.KIP.note.service;

import com.FINAL.KIP.common.firebase.dto.FCMMessageDto;
import com.FINAL.KIP.common.firebase.service.FCMService;
import com.FINAL.KIP.group.domain.GroupRole;
import com.FINAL.KIP.group.domain.GroupUser;
import com.FINAL.KIP.group.repository.GroupUserRepository;
import com.FINAL.KIP.note.domain.Note;
import com.FINAL.KIP.note.dto.request.NoteAgreeReqDto;
import com.FINAL.KIP.note.dto.request.NoteCreateReqDto;
import com.FINAL.KIP.note.dto.request.NoteRefuseReqDto;
import com.FINAL.KIP.note.dto.response.NoteGetReqDto;
import com.FINAL.KIP.note.repository.NoteRepository;
import com.FINAL.KIP.user.domain.User;
import com.FINAL.KIP.user.repository.UserRepository;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NoteService {

	private final UserRepository userRepository;
	private final GroupUserRepository groupUserRepository;
	private final FCMService fcmService;
	private final NoteRepository noteRepository;

	@Autowired
	public NoteService(UserRepository userRepository, GroupUserRepository groupUserRepository, FCMService fcmService,
		NoteRepository noteRepository) {
		this.userRepository = userRepository;
		this.groupUserRepository = groupUserRepository;
		this.fcmService = fcmService;
		this.noteRepository = noteRepository;
	}

	public void createNewRequestNote(NoteCreateReqDto noteCreateReqDto) {

		List<User> supers = groupUserRepository.findGroupUsersByGroupAndGroupRole(
			noteCreateReqDto.getGroup(),
			GroupRole.SUPER).stream().map(GroupUser::getUser).toList();

		List<Note> newRequests = new ArrayList<>();
		for (User user : supers) {
			Note note = Note.builder()
				.receiver(user)
				.writer(noteCreateReqDto.getSender())
				.document(noteCreateReqDto.getDocument())
				.message(
					noteCreateReqDto.getSender().getName() + "님이 " + noteCreateReqDto.getDocument()
						.getTitle() + " 문서의 접근 권한을 요청했습니다.").build();
			newRequests.add(note);
		}
		noteRepository.saveAll(newRequests);
		sendNewFCM(newRequests);
	}

	public void createRefuseRequestNote(NoteRefuseReqDto noteRefuseReqDto) {

		Note note = Note.builder()
			.document(noteRefuseReqDto.getDocument())
			.writer(noteRefuseReqDto.getSender())
			.receiver(noteRefuseReqDto.getReceiver())
			.message(noteRefuseReqDto.getSender().getName() + "님이 " + noteRefuseReqDto.getDocument()
				.getTitle() + " 문서의 접근 권한 요청을 거절하셨습니다.")
			.build();
		Note save = noteRepository.save(note);
		sendRefuseFCM(save);
	}

	public void createAgreeRequestNote(NoteAgreeReqDto noteAgreeReqDto) {

		Note note = Note.builder()
			.document(noteAgreeReqDto.getDocument())
			.writer(noteAgreeReqDto.getSender())
			.receiver(noteAgreeReqDto.getReceiver())
			.message(noteAgreeReqDto.getSender().getName() + "님이 " + noteAgreeReqDto.getDocument()
				.getTitle() + " 문서의 접근 권한 요청을 수락하셨습니다.")
			.build();
		Note save = noteRepository.save(note);
		sendAgreeFCM(save);
	}

	private void sendRefuseFCM(Note note) {
		FCMMessageDto fcmMessageDtos = FCMMessageDto.of(note.getWriter().getName(),
			note.getReceiver().getEmployeeId(),
			note.getMessage());
		fcmService.sendRefuseRequestMessage(fcmMessageDtos);
	}

	private void sendNewFCM(List<Note> newRequests) {
		List<FCMMessageDto> fcmMessageDtos = new ArrayList<>();
		for (Note note : newRequests) {
			fcmMessageDtos.add(FCMMessageDto.of(note.getWriter().getName(),
				note.getReceiver().getEmployeeId(),
				note.getMessage()));
		}
		fcmService.sendNewRequestMessage(fcmMessageDtos);
	}

	private void sendAgreeFCM(Note note) {
		FCMMessageDto fcmMessageDto = FCMMessageDto.of(note.getWriter().getName(),
			note.getReceiver().getEmployeeId(),
			note.getMessage());

		fcmService.sendAgreeRequestMessage(fcmMessageDto);
	}

	public ResponseEntity<List<NoteGetReqDto>> getAllNote() {
		User user = getUserFromAuthentication();

		List<NoteGetReqDto> collect = user.getNotes().stream().map(note -> {
			return new NoteGetReqDto(
				note.getMessage(),
				note.getId(),
				note.getIsRead(),
				note.getCreated_at().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
			);
		}).toList();
		return new ResponseEntity<>(collect, HttpStatus.OK);
	}

	public User getUserFromAuthentication() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return getUserByEmployeeId(authentication.getName());
	}

	private User getUserByEmployeeId(String name) {
		return userRepository.findByEmployeeId(name)
			.orElseThrow(() -> new IllegalArgumentException("예상치 못한 에러가 발생했습니다."));
	}

	public ResponseEntity<String> removeNote(Long noteId) {
		User user = getUserFromAuthentication();
		Note note = findNoteById(noteId);
		if (!note.getReceiver().equals(user)) {
			throw new IllegalArgumentException("해당 알림은 삭제할 수 없습니다.");
		}

		noteRepository.delete(note);
		return new ResponseEntity<>("remove is complete!", HttpStatus.OK);
	}

	public Note findNoteById(Long noteId) {
		return noteRepository.findById(noteId)
			.orElseThrow(() -> new IllegalArgumentException("해당 알림이 존재하지 않습니다."));
	}
	public ResponseEntity<Long> readNote(Long noteId) {
		User user = getUserFromAuthentication();
		Note note = findNoteById(noteId);
		if (!note.getReceiver().equals(user)) {
			throw new IllegalArgumentException("해당 알림은 읽을 수 없습니다.");
		}
		note.readNote();
		return new ResponseEntity<>(noteId, HttpStatus.OK);
	}
}