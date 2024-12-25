package ru.clevertec.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.clevertec.entity.NewsEntity;

import java.util.UUID;


@Repository
public interface NewsRepository extends JpaRepository<NewsEntity, UUID> {
    Page<NewsEntity> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}

