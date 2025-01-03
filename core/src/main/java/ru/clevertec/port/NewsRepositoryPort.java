package ru.clevertec.port;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.clevertec.domain.News;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NewsRepositoryPort {
    Page<News> findAll(Pageable pageable);
    Optional<News> findById(UUID id);
    News save(News news);
    void deleteById(UUID id);
    List<News> searchByText(String query);
}
