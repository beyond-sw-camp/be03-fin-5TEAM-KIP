package com.FINAL.KIP.securities;

import com.FINAL.KIP.securities.refresh.UserRefreshToken;
import com.FINAL.KIP.securities.refresh.UserRefreshTokenRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@PropertySource("classpath:jwt.yml")
@Service
public class JwtTokenProvider { //토큰생성 과정

    private final String secretKey;
    private final Long expirationMinutes;
    private final Long refreshExpirationHours;
    private final String issuer;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final Long reissueLimit;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtTokenProvider(@Value("${secret-key}") String secretKey,
                            @Value("${expiration-minutes}") long expirationMinutes,
                            @Value("${refresh-expiration-hours}") long refreshExpirationHours,
                            @Value("${issuer}") String issuer, UserRefreshTokenRepository userRefreshTokenRepository) {
        this.secretKey = secretKey;
        this.expirationMinutes = expirationMinutes;
        this.refreshExpirationHours = refreshExpirationHours;
        this.issuer = issuer;
        this.userRefreshTokenRepository = userRefreshTokenRepository;
        reissueLimit = refreshExpirationHours * 60 / expirationMinutes;
    }

    public String createAccessToken(String userSpecification) {

        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .setSubject(userSpecification)
                .setIssuer(issuer)
                .setIssuedAt(Timestamp.valueOf(LocalDateTime.now()))
                .setExpiration(Date.from(Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES)))
                .compact();
    }

    public String createRefreshToken() {

        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .setIssuer(issuer)
                .setIssuedAt(Timestamp.valueOf(LocalDateTime.now()))
                .setExpiration(Date.from(Instant.now().plus(refreshExpirationHours, ChronoUnit.HOURS)))
                .compact();
    }

    @Transactional
    public String recreateAccessToken(String oldAccessToken) throws JsonProcessingException {
        String subject = decodeJwtPayloadSubject(oldAccessToken);
        userRefreshTokenRepository.findByUserIdAndReissueCountLessThan(
                        Long.parseLong(subject.split(":")[0]), reissueLimit)
                .ifPresentOrElse(
                        UserRefreshToken::increaseReissueCount,
                        () -> {
                            throw new ExpiredJwtException(null, null, "Refresh token expired.");
                        }
                );
        return createAccessToken(subject);
    }

    @Transactional
    public void validateRefreshToken(String refreshToken, String oldAccessToken) throws JsonProcessingException {
        validateAndParseToken(refreshToken);
        String memberId = decodeJwtPayloadSubject(oldAccessToken).split(":")[0];
        userRefreshTokenRepository.findByUserIdAndReissueCountLessThan(Long.parseLong(memberId), reissueLimit)
                .filter(memberRefreshToken -> memberRefreshToken.validateRefreshToken(refreshToken))
                .orElseThrow(() -> new ExpiredJwtException(null, null, "Refresh token expired."));
    }


    private String decodeJwtPayloadSubject(String oldAccessToken) throws JsonProcessingException {
        return objectMapper.readValue(
                new String(Base64.getDecoder().decode(oldAccessToken.split("\\.")[1]),
                        StandardCharsets.UTF_8),
                Map.class
        ).get("sub").toString();
    }

    private Jws<Claims> validateAndParseToken(String token) {	// validateTokenAndGetSubject에서 따로 분리
        return Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(token);
    }

    public String validateTokenAndGetSubject(String token) {
        return validateAndParseToken(token).getBody().getSubject();
    }



//    public String createToken(String employeeId, String role) {
////        claims : 클레임은 토큰 사용자에 대한 속성이나 추가 데이터 포함.
//        Claims claims = Jwts.claims().setSubject(employeeId); // claims에 email, role 세팅
//        log.info("expiration : " + expiration);
//        log.info("secretKey : " + secretKey);
//        claims.put("role", role);
//        Date now = new Date();
//
//        // signWith 진행시 secret값을 base64 or 바이트로 변환하여야 적용 가능.
//        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
//
//        String token = Jwts.builder()
//                .setClaims(claims)
//                .setIssuedAt(now)
//                .setExpiration(new Date(now.getTime() + expiration*60*1000L))   // 30분 설정 // 30분 60초 1000밀리초
//                .signWith(key, SignatureAlgorithm.HS256) //SHA256암호화 사용
//                .compact();
//        return token;
//    }
}
