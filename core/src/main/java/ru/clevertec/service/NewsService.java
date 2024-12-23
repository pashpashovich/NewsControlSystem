package ru.clevertec.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.clevertec.dto.NewsCreateRequest;
import ru.clevertec.dto.NewsDto;
import ru.clevertec.entity.News;
import ru.clevertec.exception.NotFoundException;
import ru.clevertec.mapper.NewsMapper;
import ru.clevertec.port.NewsRepositoryPort;

import java.util.UUID;

@Service
public class NewsService {

    private final NewsRepositoryPort newsRepository;
    private final NewsMapper newsMapper;

    public NewsService(NewsRepositoryPort newsRepository, NewsMapper newsMapper) {
        this.newsRepository = newsRepository;
        this.newsMapper = newsMapper;
    }

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