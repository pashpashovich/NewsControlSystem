package ru.clevertec.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.clevertec.domain.News;
import ru.clevertec.dto.CommentDto;
import ru.clevertec.dto.NewsCreateRequest;
import ru.clevertec.dto.NewsDto;
import ru.clevertec.dto.NewsWithCommentsDto;
import ru.clevertec.exception.NotFoundException;
import ru.clevertec.mapper.NewsMapper;
import ru.clevertec.port.CommentServicePort;
import ru.clevertec.port.NewsRepositoryPort;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepositoryPort newsRepository;
    private final NewsMapper newsMapper;
    private final CommentServicePort commentClient;

    public Page<NewsDto> getAllNews(Pageable pageable) {
        return newsMapper.toDtoList(newsRepository.findAll(pageable));
    }

    public NewsDto createNews(NewsCreateRequest newsDto) {
        News savedNews = newsRepository.save(newsMapper.toEntity(newsDto));
        return newsMapper.toDto(savedNews);
    }

    public NewsDto getNewsById(UUID newsId) {
        return newsMapper.toDto(newsRepository.findById(newsId)
                .orElseThrow(() -> new NotFoundException(String.format("Новость с ID %s не найдена", newsId))));
    }

    public NewsWithCommentsDto getNewsWithComments(UUID newsId) {
        NewsWithCommentsDto news = newsMapper.toDtoWithComments(newsRepository.findById(newsId)
                .orElseThrow(() -> new NotFoundException("Новость не найдена")));
        Page<CommentDto> comments = commentClient.getCommentsForNews(newsId);
        news.setComments(comments.getContent());
        return news;
    }

    public CommentDto getExactComment(UUID newsId, UUID commentsId) {
        List<CommentDto> comments = commentClient.getCommentsForNews(newsId).getContent();
        return comments.stream()
                .filter(commentDto -> commentDto.getId().equals(commentsId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Комментария с таким ID не существует"));
    }

    public NewsDto updateNews(UUID newsId, NewsCreateRequest newsUpdateRequest) {
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new NotFoundException(String.format("Новость с ID %s не найдена", newsId)));

        news.setTitle(newsUpdateRequest.getTitle());
        news.setText(newsUpdateRequest.getText());
        news = newsRepository.save(news);
        return newsMapper.toDto(news);
    }


    public void deleteNews(UUID newsId) {
        if (!newsRepository.existsById(newsId)) {
            throw new NotFoundException(String.format("Новость с ID %s не найдена", newsId));
        }
        newsRepository.deleteById(newsId);
    }
}