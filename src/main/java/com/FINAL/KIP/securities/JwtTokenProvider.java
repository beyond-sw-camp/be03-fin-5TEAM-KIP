package com.FINAL.KIP.securities;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expiration*60*1000L))   // 30분 설정 // 30분 60초 1000밀리초
                .signWith(SignatureAlgorithm.HS256, "kipsecret") //SHA256암호화 사용
                .compact();
        return token;
    }
}
