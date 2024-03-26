package com.FINAL.KIP.securities;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.security.auth.kerberos.EncryptionKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider { //토큰생성 과정
    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private int expiration;

    public String createToken(String employeeId, String role) {
//        claims : 클레임은 토큰 사용자에 대한 속성이나 추가 데이터 포함.
        Claims claims = Jwts.claims().setSubject(employeeId); // claims에 email, role 세팅
        log.info("expiration : " + expiration);
        log.info("secretKey : " + secretKey);
        claims.put("role", role);
        Date now = new Date();

        // signWith 진행시 secret값을 base64 or 바이트로 변환하여야 적용 가능.
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expiration*60*1000L))   // 30분 설정 // 30분 60초 1000밀리초
                .signWith(key, SignatureAlgorithm.HS256) //SHA256암호화 사용
                .compact();
        return token;
    }
}
