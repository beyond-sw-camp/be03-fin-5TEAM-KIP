package com.FINAL.KIP.common.firebase.service;

import com.FINAL.KIP.common.firebase.FCMTokenDao;
import com.FINAL.KIP.common.firebase.dto.FCMMessageDto;
import com.FINAL.KIP.user.dto.req.LoginReqDto;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FCMService implements MessageService {

	private final FCMTokenDao fcmTokenDao;

	@Autowired
	public FCMService(FCMTokenDao fcmTokenDao) {
		this.fcmTokenDao = fcmTokenDao;
	}

	@Override
	public void sendNewRequestMessage(List<FCMMessageDto> fcmMessageDtos) {
		for (FCMMessageDto fcmMessageDto : fcmMessageDtos) {
			if (!hasKey(fcmMessageDto.getEmployeeId())) {
				return;
			}

			String token = getToken(fcmMessageDto.getEmployeeId());
			Message message = Message.builder()
				.putData("title", "새로운 문서 접근 권한 요청")
				.putData("content", fcmMessageDto.getMessage())
				.setToken(token)
				.build();

			send(message);
		}
	}

	@Override
	public void sendRefuseRequestMessage(FCMMessageDto fcmMessageDto) {
		if (!hasKey(fcmMessageDto.getEmployeeId())) {
			return;
		}

		String token = getToken(fcmMessageDto.getEmployeeId());
		Message message = Message.builder()
			.putData("title", "문서 접근 요청 거절")
			.putData("content", fcmMessageDto.getMessage())
			.setToken(token)
			.build();

		send(message);
	}

	@Override
	public void sendAgreeRequestMessage(FCMMessageDto fcmMessageDto) {
		if (!hasKey(fcmMessageDto.getEmployeeId())) {
			return;
		}

		String token = getToken(fcmMessageDto.getEmployeeId());
		Message message = Message.builder()
			.putData("title", "문서 접근 권한 요청 승인")
			.putData("content", fcmMessageDto.getMessage())
			.setToken(token)
			.build();

		send(message);
	}

	public void send(Message message) {
		FirebaseMessaging.getInstance().sendAsync(message);
	}

	public void saveToken(LoginReqDto loginReqDto) {
		fcmTokenDao.saveToken(loginReqDto);
	}

	public void deleteToken(String employeeId) {
		fcmTokenDao.deleteToken(employeeId);
	}

	private boolean hasKey(String employeeId) {
		return fcmTokenDao.hasKey(employeeId);
	}

	private String getToken(String employeeId) {
		return fcmTokenDao.getToken(employeeId);
	}
}