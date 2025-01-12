package ru.clevertec.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CommentFromDto {
    private UUID id;
    private String text;
    private String username;
    private LocalDateTime createdAt;
}
