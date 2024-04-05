package com.FINAL.KIP.version.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VersionDetailResDto {

	private String title;
	private String content;
	private String writer;
	private LocalDateTime localDateTime;

}