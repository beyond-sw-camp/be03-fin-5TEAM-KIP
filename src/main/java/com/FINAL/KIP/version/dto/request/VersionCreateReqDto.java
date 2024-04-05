package com.FINAL.KIP.version.dto.request;

import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VersionCreateReqDto {

	private String content;
	private Document document;

}