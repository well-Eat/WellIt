package com.wellit.project;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /imgs/shop/product/**로 접근하면 C:/uploads/ 경로에 있는 파일을 제공
        registry.addResourceHandler("/imgs/shop/product/**")
                .addResourceLocations("file:C:/uploads/");
        registry.addResourceHandler("/imgs/shop/review/**")
                .addResourceLocations("file:C:/uploads/");
    }
}
