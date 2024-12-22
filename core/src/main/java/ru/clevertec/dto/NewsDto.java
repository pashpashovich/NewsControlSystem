package ru.clevertec.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NewsDto {
    String title;
    String text;
    LocalDateTime createdAt;
}
