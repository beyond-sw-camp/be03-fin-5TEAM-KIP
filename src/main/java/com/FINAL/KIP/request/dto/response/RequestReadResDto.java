package com.FINAL.KIP.request.dto.response;

import com.FINAL.KIP.group.domain.Group;
import com.FINAL.KIP.request.domain.Request;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestReadResDto {

	private Long requestId;
	private String documentName;
	private String isOk;
	private int days;
	private String groupName;
	private String dueDate;

}