package com.wellit.project;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests((requests) -> requests //requests:authorizeHttpRequests
				.requestMatchers(new AntPathRequestMatcher("/**")).permitAll())	
			    .headers((headers) -> headers
			    		.addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
		// XFrameOptionsHeader 값으로 SAMEORIGIN을 설정하면 프레임에 포함된 웹 페이지가 동일한 사이트에 제공할 때만 사용이 허락된다.
			 // 폼 로그인 설정
	            .formLogin(form -> form
	            	.loginPage("/member/login")
	            	.successHandler(new CustomAuthenticationSuccessHandler()) // 커스텀 성공 핸들러 설정
	            )
	            
	            // 로그아웃 설정
	            .logout(logout -> logout
	                .logoutUrl("/member/logout")       // 로그아웃 요청 경로
	                .logoutSuccessUrl("/") // 로그아웃 후 리디렉션 URL
	                .invalidateHttpSession(true) // 세션 무효화
	                .deleteCookies("JSESSIONID") // 쿠키 삭제
	            );
		return http.build();
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();  
		//사용자 인증과 권한 부여 프로세스를 내부적으로 처리
	}
}