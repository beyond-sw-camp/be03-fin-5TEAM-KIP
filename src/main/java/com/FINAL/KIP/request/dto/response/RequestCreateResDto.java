package com.FINAL.KIP.request.dto.response;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestCreateResDto {

	String docTitle;
	LocalDateTime requestTime;

}