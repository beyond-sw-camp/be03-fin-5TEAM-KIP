package com.FINAL.KIP.document.dto.res;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DocumentVersionResDto {

	String writer;
	LocalDateTime createdTime;
	String isShow;

}