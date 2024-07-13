package org.example.velogproject.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/profile_image/**")
            .addResourceLocations("file:" + uploadDir + "profile_image/");

        registry.addResourceHandler("/thumbnail_image/**")
            .addResourceLocations("file:" + uploadDir + "thumbnail_image/");

        registry.addResourceHandler("/post_image/**")
            .addResourceLocations("file:" + uploadDir + "post_image/");
    }
}
