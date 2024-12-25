package ru.clevertec.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewsWithCommentsDto {
    private UUID id;
    private String title;
    private String text;
    private LocalDateTime createdAt;
    private List<CommentDto> comments;
}
