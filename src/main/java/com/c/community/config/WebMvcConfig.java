package com.c.community.config;

import com.c.community.controller.interceptor.AlphaInterceptor;
import com.c.community.controller.interceptor.LoginRequiredInterceptor;
import com.c.community.controller.interceptor.LoginTicketInterceptor;
import com.c.community.controller.interceptor.MessageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AlphaInterceptor alphaInterceptor;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Autowired
    private MessageInterceptor messageInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(alphaInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.jpeg", "/**/*.jpg", "/**/*.png")
                .addPathPatterns("/register", "/login");

        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.jpeg", "/**/*.jpg", "/**/*.png");

        registry.addInterceptor(loginRequiredInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.jpeg", "/**/*.jpg", "/**/*.png");

        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.jpeg", "/**/*.jpg", "/**/*.png");
    }
}
