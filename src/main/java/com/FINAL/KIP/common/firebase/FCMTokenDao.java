package com.FINAL.KIP.common.firebase;

import com.FINAL.KIP.user.dto.req.LoginReqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class FCMTokenDao {

	private final StringRedisTemplate stringRedisTemplate;

	public void saveToken(LoginReqDto loginReqDto) {
		stringRedisTemplate.opsForValue()
			.set(loginReqDto.getEmployeeId(), loginReqDto.getToken());
	}

	public String getToken(String employeeId) {
		return stringRedisTemplate.opsForValue().get(employeeId);
	}

	public void deleteToken(String employeeId) {
		stringRedisTemplate.delete(employeeId);
	}

	public boolean hasKey(String employeeId) {
		return stringRedisTemplate.hasKey(employeeId);
	}

}