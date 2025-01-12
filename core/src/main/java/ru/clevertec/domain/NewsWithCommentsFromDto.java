package ru.clevertec.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class NewsWithCommentsFromDto {
    private UUID id;

    private String title;

    private String text;

    private LocalDateTime createdAt;

    private List<CommentFromDto> comments = new ArrayList<>();
}
