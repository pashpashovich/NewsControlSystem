package ru.clevertec.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.clevertec.domain.News;
import ru.clevertec.mapper.NewsDomainMapper;
import ru.clevertec.port.NewsRepositoryPort;
import ru.clevertec.repository.NewsRepository;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NewsRepositoryAdapter implements NewsRepositoryPort {

    private final NewsRepository newsRepository;
    private final NewsDomainMapper newsMapper;

    @Override
    public Page<News> findAll(Pageable pageable) {
        return newsMapper.toDomainList(newsRepository.findAll(pageable));
    }

    @Override
    public Optional<News> findById(UUID id) {
        return newsRepository.findById(id)
                .map(newsMapper::toDomain);
    }

    @Override
    public News save(News news) {
        return newsMapper.toDomain(newsRepository.save(newsMapper.toEntity(news)));
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

