package ru.clevertec.utils;

import ru.clevertec.domain.News;
import ru.clevertec.dto.CommentDto;
import ru.clevertec.dto.NewsCreateRequest;
import ru.clevertec.dto.NewsDto;
import ru.clevertec.dto.NewsWithCommentsDto;

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

    public static CommentDto createSampleComment(UUID id, String text) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(id);
        commentDto.setText(text);
        return commentDto;
    }

    public static NewsDto createSampleNewsDto(String title, String text) {
        return NewsDto.builder()
                .title(title)
                .text(text)
                .build();
    }

    public static NewsWithCommentsDto createSampleNewsWithCommentsDto(String title, String text, List<CommentDto> comments) {
        return NewsWithCommentsDto.builder()
                .title(title)
                .text(text)
                .comments(comments)
                .build();
    }

    public static NewsCreateRequest createNewsCreateRequest(String title, String text) {
        NewsCreateRequest newsCreateRequest = new NewsCreateRequest();
        newsCreateRequest.setTitle(title);
        newsCreateRequest.setText(text);

        return newsCreateRequest;
    }
}
