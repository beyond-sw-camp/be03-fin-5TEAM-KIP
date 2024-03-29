package com.FINAL.KIP.securities.refresh;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Long> {

    Optional<UserRefreshToken> findByUserIdAndReissueCountLessThan(Long id, long count);
}
