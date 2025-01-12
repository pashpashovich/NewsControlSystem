package ru.clevertec.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class NewsFromDto {
    private UUID id;
    private String title;
    private String text;
    private LocalDateTime createdAt;
}
