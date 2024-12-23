package ru.clevertec.adapter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.clevertec.entity.News;
import ru.clevertec.port.NewsRepositoryPort;
import ru.clevertec.repository.NewsRepository;

import java.util.Optional;
import java.util.UUID;

@Component
public class NewsRepositoryAdapter implements NewsRepositoryPort {

    private final NewsRepository newsRepository;

    public NewsRepositoryAdapter(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @Override
    public Page<News> findAll(Pageable pageable) {
        return newsRepository.findAll(pageable);
    }

    @Override
    public Optional<News> findById(UUID id) {
        return newsRepository.findById(id);
    }

    @Override
    public News save(News news) {
        return newsRepository.save(news);
    }

    @Override
    public void deleteById(UUID id) {
        newsRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return newsRepository.existsById(id);
    }
}

