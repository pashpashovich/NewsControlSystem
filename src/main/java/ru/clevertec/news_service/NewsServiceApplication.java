package ru.clevertec.news_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "ru.clevertec"
})
@EntityScan(basePackages = "ru.clevertec.entity")
@EnableJpaRepositories(basePackages = "ru.clevertec.entity")
public class NewsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NewsServiceApplication.class, args);
    }
}
