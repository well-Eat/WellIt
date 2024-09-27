package com.wellit.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.filter.HiddenHttpMethodFilter;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories
public class WellItApplication {

	public static void main(String[] args) {
		SpringApplication.run(WellItApplication.class, args);
	}

	//@DeleteMapping, @PutMapping 사용하기 위해 Bean 추가
	@Bean
	public HiddenHttpMethodFilter hiddenHttpMethodFilter(){
		return new HiddenHttpMethodFilter();
	}


}
