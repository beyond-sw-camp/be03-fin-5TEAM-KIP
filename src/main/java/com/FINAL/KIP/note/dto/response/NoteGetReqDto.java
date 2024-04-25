package com.FINAL.KIP.note.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class NoteGetReqDto {

	private String message;
	private Long noteId;
	private String isRead;
	private String createdTime;

}