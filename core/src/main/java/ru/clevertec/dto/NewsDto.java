package ru.clevertec.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class NewsDto {
    String title;
    String text;
    LocalDateTime createdAt;
}
