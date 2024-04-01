package com.FINAL.KIP.common.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CheckAuthorityAspect {

    @Before("@annotation(com.FINAL.KIP.common.aspect.UserAdmin)")
    public void checkAuthorityAmdinUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities().stream().noneMatch(
                grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN") || grantedAuthority.getAuthority().equals("USER"))
        ) {
            throw new SecurityException("유저와 관리자만 접근 가능한 함수입니다.");
        }
    }

    @Before("@annotation(com.FINAL.KIP.common.aspect.JustAdmin)")
    public void checkAuthorityAmdin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities().stream().noneMatch(
                grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"))
        ) {
            throw new SecurityException("관리자만 접근가능한 함수입니다.");
        }
    }
}
