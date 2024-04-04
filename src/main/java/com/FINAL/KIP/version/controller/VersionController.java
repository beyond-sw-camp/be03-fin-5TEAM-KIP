package com.FINAL.KIP.version.controller;

import com.FINAL.KIP.version.dto.request.VersionPostReqDto;
import com.FINAL.KIP.version.service.VersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VersionController {
	private final VersionService versionService;

	@Autowired
	public VersionController(VersionService versionService) {
		this.versionService = versionService;
	}

	@PostMapping("/version")
	public ResponseEntity<Object> postNewVersion(@RequestBody VersionPostReqDto versionPostReqDto) {
		return versionService.postNewVersion(versionPostReqDto);
	}

}