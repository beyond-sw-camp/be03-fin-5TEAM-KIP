package com.FINAL.KIP.common.firebase.service;

import com.FINAL.KIP.common.firebase.dto.FCMMessageDto;
import java.util.List;

public interface MessageService {

	void sendNewRequestMessage(List<FCMMessageDto> fcmMessageDtos);
	void sendRefuseRequestMessage(FCMMessageDto fcmMessageDto);
	void sendAgreeRequestMessage(FCMMessageDto fcmMessageDto);
}
