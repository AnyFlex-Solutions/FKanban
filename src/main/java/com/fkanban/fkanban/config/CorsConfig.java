package com.fkanban.fkanban.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Конфигурация CORS
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Разрешаем все пути
                .allowedOrigins("http://localhost:8090") // Разрешаем доступ с определенного адреса
                .allowedMethods("*") // Разрешаем все методы HTTP
                .allowedHeaders("*") // Разрешаем любые заголовки
                .allowCredentials(true); // Разрешаем передачу учетных данных
    }
}
