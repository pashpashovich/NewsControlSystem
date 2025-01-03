package ru.clevertec.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class News {
    private UUID id;
    private String title;
    private String text;
    private LocalDateTime createdAt=LocalDateTime.now();
    private String username;
}
