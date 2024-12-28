package ru.clevertec.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.clevertec.entity.NewsEntity;

import java.util.List;
import java.util.UUID;


@Repository
public interface NewsRepository extends JpaRepository<NewsEntity, UUID> {
    @Query(value = "SELECT * FROM news n WHERE LOWER(n.title) ILIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(n.text) ILIKE LOWER(CONCAT('%', :query, '%'))", nativeQuery = true)
    List<NewsEntity> searchByText(@Param("query") String query);
}

