package com.FINAL.KIP.note.dto.request;

import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.group.domain.Group;
import com.FINAL.KIP.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NoteCreateReqDto {

	private Document document;
	private Group group;
	private User sender;

}