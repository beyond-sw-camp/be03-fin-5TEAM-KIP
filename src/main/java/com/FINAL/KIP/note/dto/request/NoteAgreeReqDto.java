package com.FINAL.KIP.note.dto.request;

import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NoteAgreeReqDto {

	private User receiver;
	private Document document;
	private User sender;

}