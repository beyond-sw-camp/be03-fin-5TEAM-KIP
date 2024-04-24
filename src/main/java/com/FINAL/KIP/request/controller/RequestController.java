package com.FINAL.KIP.request.controller;

import com.FINAL.KIP.request.dto.request.RequestCreateReqDto;
import com.FINAL.KIP.request.dto.response.ReceivedRequestReadResDto;
import com.FINAL.KIP.request.dto.response.RequestAgreeResDto;
import com.FINAL.KIP.request.dto.response.RequestCreateResDto;
import com.FINAL.KIP.request.dto.response.RequestDeleteResDto;
import com.FINAL.KIP.request.dto.response.RequestReadResDto;
import com.FINAL.KIP.request.dto.response.RequestRefuseResDto;
import com.FINAL.KIP.request.service.RequestService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RequestController {

	private final RequestService requestService;

	@Autowired
	public RequestController(RequestService requestService) {
		this.requestService = requestService;
	}


	@PostMapping("/doc/request")
	public ResponseEntity<RequestCreateResDto> createRequest(
		@RequestBody RequestCreateReqDto requestCreateReqDto) {
		return requestService.createRequest(requestCreateReqDto);
	}

	@GetMapping("/request/refuse/{request_id}")
	public ResponseEntity<RequestRefuseResDto> refuseRequest(@PathVariable Long request_id) {
		return requestService.refuseRequest(request_id);
	}

	@GetMapping("/request/agree/{request_id}")
	public ResponseEntity<RequestAgreeResDto> agreeRequest(@PathVariable Long request_id) {
		return requestService.agreeRequest(request_id);
	}

	@DeleteMapping("/request/my/{request_id}")
	public ResponseEntity<RequestDeleteResDto> deleteMyRequst(@PathVariable Long request_id) {
		return requestService.deleteMyRequest(request_id);
	}
	@DeleteMapping("/request/receive/{request_id}")
	public ResponseEntity<RequestDeleteResDto> deleteReceiveRequst(@PathVariable Long request_id) {
		return requestService.deleteReceivedRequest(request_id);
	}

	@GetMapping("/request")
	public ResponseEntity<List<RequestReadResDto>> readRequest() {
		return requestService.readRequest();
	}
	@GetMapping("/request/receive")
	public ResponseEntity<List<ReceivedRequestReadResDto>> readGroupRequest() {
		return requestService.readReceivedRequest();
	}
}