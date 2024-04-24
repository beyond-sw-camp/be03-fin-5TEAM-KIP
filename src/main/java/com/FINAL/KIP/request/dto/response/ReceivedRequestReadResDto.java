package com.FINAL.KIP.request.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReceivedRequestReadResDto {
	private Long requestId;
	private String requesterName;
	private String documentName;
	private String isOk;
	private int days;
	private String dueDate;
}