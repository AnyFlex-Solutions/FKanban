package com.fkanban.fkanban.kanbans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.web.servlet.ViewResolver;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
public class KanbanConfiguration {
    // Конфигурация Thymeleaf для обработки HTML шаблонов
    @Bean
    @Description("Thymeleaf template resolver serving HTML 5")
    public ClassLoaderTemplateResolver templateResolver() {

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

        templateResolver.setPrefix("templates/"); // Установка префикса для шаблонов
        templateResolver.setCacheable(false); // Отключение кеширования
        templateResolver.setSuffix(".html"); // Установка суффикса для файлов
        templateResolver.setTemplateMode("HTML5"); // Режим шаблонов HTML5
        templateResolver.setCharacterEncoding("UTF-8"); // Кодировка для шаблонов

        return templateResolver;
    }

    // Создание объекта SpringTemplateEngine для интеграции с Spring
    @Bean
    @Description("Thymeleaf template engine with Spring integration")
    public SpringTemplateEngine templateEngine() {

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        templateEngine.addDialect(new SpringSecurityDialect()); // Добавление безопасности
        return templateEngine;
    }

    // Настройка Thymeleaf для обработки запросов
    @Bean
    @Description("Thymeleaf view resolver")
    public ViewResolver viewResolver() {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();

        viewResolver.setTemplateEngine(templateEngine()); // Используем созданный движок
        viewResolver.setCharacterEncoding("UTF-8"); // Установка кодировки

        return viewResolver;
    }

    // Диалект безопасности для Thymeleaf
    @Bean
    public SpringSecurityDialect springSecurityDialect(){
        return new SpringSecurityDialect();
    }
}
