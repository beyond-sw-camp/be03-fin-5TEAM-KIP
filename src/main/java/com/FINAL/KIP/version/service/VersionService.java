package com.FINAL.KIP.version.service;

import com.FINAL.KIP.user.domain.User;
import com.FINAL.KIP.user.repository.UserRepository;
import com.FINAL.KIP.version.domain.Version;
import com.FINAL.KIP.version.dto.request.VersionCreateReqDto;
import com.FINAL.KIP.version.dto.request.VersionPostReqDto;
import com.FINAL.KIP.version.repository.VersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VersionService {

	private final UserRepository userRepository;
	private final VersionRepository versionRepository;

	@Autowired
	public VersionService(UserRepository userRepository,
		VersionRepository versionRepository) {
		this.userRepository = userRepository;
		this.versionRepository = versionRepository;
	}

	@Transactional
	public void createNewVersion(VersionCreateReqDto versionCreateReqDto) {
		String employeeId = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = findUserByEmployeeId(employeeId);

		Version newVersion = Version.builder()
			.content(versionCreateReqDto.getContent())
			.writer(user)
			.document(versionCreateReqDto.getDocument()).build();
		versionRepository.save(newVersion);
	}



	public User findUserByEmployeeId(String employeeId) {
		return userRepository.findByEmployeeId(employeeId).orElseThrow(
			() -> new IllegalArgumentException("예상치 못한 에러가 발생했습니다.")
		);
	}

	public Version findVersionById(Long id) {
		return versionRepository.findById(id).orElseThrow(
			() -> new IllegalArgumentException("해당 이력이 존재하지 않습니다.")
		);
	}

	@Transactional
	public ResponseEntity<Object> postNewVersion(VersionPostReqDto versionPostReqDto) {
		String employeeId = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = findUserByEmployeeId(employeeId);
		Version currentVersion = findVersionById(versionPostReqDto.getCurrentVersionId());


		Version newVersion = Version.builder()
			.document(currentVersion.getDocument())
			.writer(user)
			.content(versionPostReqDto.getContent()).build();

		versionRepository.save(newVersion);
		currentVersion.updateIsShow();

		return new ResponseEntity<>(null, HttpStatus.CREATED);
	}
}