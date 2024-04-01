package com.FINAL.KIP.common.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})  // 함수에만 붙일 수 있는 설정.
@Retention(RetentionPolicy.RUNTIME) // 어노테이션 런타임시에 유지
public @interface JustAdmin {
}
