package com.example.MB_beauty_club_frontend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Autowired
    public WebMvcConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**") // Apply to all paths...
                .excludePathPatterns(   // ...but EXCLUDE the following:
                        // --- Static Resources ---
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/favicon.ico",
                        "/apple-touch-icon.png",

                        // --- Public Application Pages ---
                        "/", // Home page
                        "/auth/login",
                        "/auth/register",
                        "/login/google",
                        "/process-oauth2",
                        "/continue-action",
                        "/products",
                        "/services",
                        "/appointments/calendar/**", // Use wildcards for dynamic paths
                        "/appointments/select_worker/**"
                );
    }
}