package com.FINAL.KIP.common.firebase.dto;

import lombok.Getter;

@Getter
public class FCMMessageDto {

	private String username;
	private String message;
	private String employeeId;

	public static FCMMessageDto of(String username, String employeeId, String message){
		FCMMessageDto dto = new FCMMessageDto();
		dto.username = username;
		dto.message = message;
		dto.employeeId = employeeId;
		return dto;
	}
}