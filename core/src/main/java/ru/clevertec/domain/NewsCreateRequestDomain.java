package ru.clevertec.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsCreateRequestDomain {
    private String title;
    private String text;
}
