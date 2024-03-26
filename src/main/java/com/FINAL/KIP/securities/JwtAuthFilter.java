package com.FINAL.KIP.securities;

import com.FINAL.KIP.common.ErrorResponseDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthFilter extends GenericFilter {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try { // 예외 처리 //member create후 토큰이 null이면 if문 지나친다.
            String bearerToken = ((HttpServletRequest) request).getHeader("Authorization");

            if (bearerToken !=null) {
                if (!bearerToken.substring(0, 7).equals("Bearer ")) {
                    throw new AuthenticationServiceException("token의 형식이 맞지 않습니다.");
                }

//            그 토큰값이 secret값과 동일한지 검증해야함.
//            bearer토큰에서 토큰값만 추출
                String token = bearerToken.substring(7);
//            추출된 토큰(암호화된 email, role)을 검증 후 Authentication객체 생성
//            Body를 꺼내는 과정 자체가 검증이다. secret값이 맞지 않으면 에러가 난다. //예외처리 던저주어야한다,.
//                토큰 검증 및 claims 추출 // parser 자체가 검증이다.
                Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
//                Authentication 객체를 생성하기 위한 UserDetails 생성
                List<GrantedAuthority> authorities = new ArrayList<>();
//              ROLE_권한 이 패턴으로 스프링에서 기본적으로 권한체크
                authorities.add(new SimpleGrantedAuthority("ROLE_" + claims.get("role")));
                UserDetails userDetails = new User(claims.getSubject(), "", authorities);

                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
//            filterchain에서 그 다음 filtering으로 넘어가도록 하는 메서드
            chain.doFilter(request, response);
        }catch (Exception e){
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpServletResponse.setContentType("application/json");
            httpServletResponse.getWriter().write(ErrorResponseDto.makeMessage(HttpStatus.UNAUTHORIZED, e.getMessage()).toString());
        }
    }
}
