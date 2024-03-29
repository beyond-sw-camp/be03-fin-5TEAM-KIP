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
import com.FINAL.KIP.note.repository.NoteRepository;
import com.FINAL.KIP.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NoteService {

	private final GroupUserRepository groupUserRepository;
	private final FCMService fcmService;
	private final NoteRepository noteRepository;

	@Autowired
	public NoteService(GroupUserRepository groupUserRepository, FCMService fcmService,
		NoteRepository noteRepository) {
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
}