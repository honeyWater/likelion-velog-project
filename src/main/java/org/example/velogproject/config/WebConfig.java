package org.example.velogproject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/profile_image/**")
            .addResourceLocations("file:///D:/사용자/msi/Desktop/멋쟁이_사자처럼/velog_프로젝트/image/profile_image/");

        registry.addResourceHandler("/thumbnail_image/**")
            .addResourceLocations("file:///D:/사용자/msi/Desktop/멋쟁이_사자처럼/velog_프로젝트/image/thumbnail_image/");
    }
}
