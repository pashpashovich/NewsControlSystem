package ru.clevertec.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsCreateRequest {
    @NotNull(message = "Заголовок новости не может быть пустым")
    @Size(min = 3, max = 255, message = "Заголовок новости должен содержать 3-255 символов")
    String title;
    @NotNull(message = "Текст новости не может быть пустым")
    @Size(min = 10, message = "Текст новости должен содержать минимум 10 символов")
    String text;
}
