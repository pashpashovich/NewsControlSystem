package ru.clevertec.utils;

import ru.clevertec.domain.CommentFromDto;
import ru.clevertec.domain.News;
import ru.clevertec.domain.NewsCreateRequestDomain;
import ru.clevertec.domain.NewsFromDto;
import ru.clevertec.domain.NewsWithCommentsFromDto;

import java.util.List;
import java.util.UUID;

public class UtilCreator {
    public static News createSampleNews(UUID id, String title, String text, String username) {
        News news = new News();
        news.setId(id);
        news.setTitle(title);
        news.setText(text);
        news.setUsername(username);
        return news;
    }

    public static CommentFromDto createSampleComment(UUID id, String text) {
        return CommentFromDto.builder()
                .id(id)
                .text(text)
                .build();
    }

    public static NewsFromDto createSampleNewsDto(String title, String text) {
        return NewsFromDto.builder()
                .title(title)
                .text(text)
                .build();
    }

    public static NewsWithCommentsFromDto createSampleNewsWithCommentsDto(String title, String text, List<CommentFromDto> comments) {
        return NewsWithCommentsFromDto.builder()
                .title(title)
                .text(text)
                .comments(comments)
                .build();
    }

    public static NewsCreateRequestDomain createNewsCreateRequest(String title, String text) {
        NewsCreateRequestDomain newsCreateRequest = new NewsCreateRequestDomain();
        newsCreateRequest.setTitle(title);
        newsCreateRequest.setText(text);

        return newsCreateRequest;
    }
}
