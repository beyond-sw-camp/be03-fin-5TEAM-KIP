package com.FINAL.KIP.document.dto.res;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AgreeDocResDto {
	private Long documentId;
	private String title;
	private String groupName;
}