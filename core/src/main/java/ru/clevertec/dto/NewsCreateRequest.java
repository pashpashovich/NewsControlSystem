package ru.clevertec.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsCreateRequest {
    String title;
    String text;
}
