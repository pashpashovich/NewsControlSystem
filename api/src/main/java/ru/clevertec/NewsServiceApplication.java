package ru.clevertec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication(scanBasePackages = {
        "ru.clevertec"
})
@EntityScan(basePackages = "ru.clevertec.entity")
@EnableJpaRepositories(basePackages = "ru.clevertec.repository")
@EnableFeignClients(basePackages = "ru.clevertec.client")
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class NewsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NewsServiceApplication.class, args);
    }
}
