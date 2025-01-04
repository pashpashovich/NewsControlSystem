package ru.clevertec.repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.clevertec.entity.NewsEntity;
import ru.clevertec.utils.NewsEntityFactory;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@Testcontainers
@DataJpaTest
@ActiveProfiles(profiles = "test")
class NewsRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("test")
                    .withUsername("news_user")
                    .withPassword("news_pass");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Autowired
    private NewsRepository newsRepository;

    @Test
    void shouldSaveAndFindNews() {
        // given
        NewsEntity news = NewsEntityFactory.createDefaultNews();

        // when
        newsRepository.save(news);
        List<NewsEntity> result = newsRepository.findAll();

        // then
        assertEquals(1, result.size());
        assertThat(result.get(0).getTitle()).isEqualTo("Default Title");
    }

    @Test
    void shouldDeleteNews() {
        // given
        NewsEntity news = NewsEntityFactory.createDefaultNews();

        NewsEntity savedNews = newsRepository.save(news);

        // when
        newsRepository.deleteById(savedNews.getId());

        // then
        boolean exists = newsRepository.existsById(savedNews.getId());
        assertThat(exists).isFalse();
    }

    @Test
    void shouldFindNewsContainingKeyword() {
        // given
        NewsEntity news1 = NewsEntityFactory.createNews("Breaking News", "Something happened", "author");

        NewsEntity news2 = NewsEntityFactory.createNews("Daily Update", "Another event", "author");

        newsRepository.save(news1);
        newsRepository.save(news2);

        // when
        List<NewsEntity> result = newsRepository.searchByText("breaking");

        // then
        assertEquals(1,result.size());
        assertThat(result.get(0).getTitle()).isEqualTo("Breaking News");
    }
}