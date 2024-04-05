package com.FINAL.KIP.version.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VersionReplaceResDto {

	private Long beforeVersion;
	private Long currentVersion;

}