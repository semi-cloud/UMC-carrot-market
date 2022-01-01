package com.umc.carrotmarket.src;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity  //SpringSecurity 사용을 위한 어노테이션, 기본적으로 CSRF 활성화
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * SpringSecurity 설정
     * REST API 서버는 stateless 하게 개발하기 때문에 사용자 정보를 Session 에 저장 안함
     * jwt 토큰을 Cookie 에 저장하지 않는다면, CSRF 에 어느정도는 안전.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();  // CSRF 비활성화,
    }
}
