package com.FINAL.KIP.securities;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")
//                .allowedOrigins("http://localhost:3000/", "https://www.teamkip.info") //vue의 url
                .allowedOrigins("*") //vue의 url
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true); //보안처리 관련 credentials
    }
}
