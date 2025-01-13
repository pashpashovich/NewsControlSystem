package ru.clevertec.controller;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.clevertec.domain.CommentFromDto;
import ru.clevertec.domain.NewsCreateRequestDomain;
import ru.clevertec.domain.NewsFromDto;
import ru.clevertec.domain.NewsWithCommentsFromDto;
import ru.clevertec.service.NewsService;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class NewsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NewsService newsService;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.3")
            .withDatabaseName("test")
            .withUsername("user")
            .withPassword("password");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    @WithMockUser()
    void shouldReturnAllNews() throws Exception {
        // given
        when(newsService.getAllNews(any())).thenReturn(Page.empty());
        //when
        //then
        mockMvc.perform(get("/news")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isEmpty())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Все новости получены"));
    }

    @Test
    @WithMockUser()
    void shouldReturnNewsById() throws Exception {
        // given
        UUID newsId = UUID.randomUUID();
        NewsFromDto mockNews = NewsFromDto.builder()
                .title("Title")
                .text("Text")
                .build();
        when(newsService.getNewsById(newsId)).thenReturn(mockNews);
        //when
        //then
        mockMvc.perform(get("/news/{newsId}", newsId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Title"))
                .andExpect(jsonPath("$.data.text").value("Text"))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Новость успешна получена"));
    }

    @Test
    @WithMockUser()
    void shouldReturnNewsWithComments() throws Exception {
        // given
        UUID newsId = UUID.randomUUID();
        NewsWithCommentsFromDto mockNewsWithComments = NewsWithCommentsFromDto.builder()
                .title("Title")
                .text("Text")
                .comments(List.of())
                .build();

        when(newsService.getNewsWithComments(newsId)).thenReturn(mockNewsWithComments);
        //when
        //then
        mockMvc.perform(get("/news/{newsId}/comments", newsId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Title"))
                .andExpect(jsonPath("$.data.text").value("Text"))
                .andExpect(jsonPath("$.data.comments").isEmpty())
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Новость с комментариями получена"));
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void shouldReturnExactComment() throws Exception {
        // given
        UUID newsId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        CommentFromDto mockComment = CommentFromDto.builder()
                .text("Comment Text")
                .build();

        when(newsService.getExactComment(newsId, commentId)).thenReturn(mockComment);
        //when
        //then
        mockMvc.perform(get("/news/{newsId}/comments/{commentId}", newsId, commentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.text").value("Comment Text"))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Комментарий к новости получен"));
    }

    @Test
    @WithMockUser()
    void shouldSearchNews() throws Exception {
        // given
        List<NewsFromDto> mockSearchResults = List.of(
                NewsFromDto.builder().title("Title 1").text("Text 1").build(),
                NewsFromDto.builder().title("Title 2").text("Text 2").build()
        );

        when(newsService.searchNews("query")).thenReturn(mockSearchResults);
        //when
        //then
        mockMvc.perform(get("/news/search")
                        .param("query", "query")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("Title 1"))
                .andExpect(jsonPath("$.data[1].title").value("Title 2"))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Результаты поиска новостей"));
    }


    @Test
    @WithMockUser(authorities = "JOURNALIST")
    void shouldCreateNews() throws Exception {
        // given
        NewsFromDto mockNews = NewsFromDto.builder()
                .title("Created Title")
                .text("Created Text")
                .build();
        when(newsService.createNews(any(NewsCreateRequestDomain.class))).thenReturn(mockNews);
        //when
        //then
        mockMvc.perform(post("/news")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Created Title",
                                  "text": "Created Text"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Created Title"))
                .andExpect(jsonPath("$.text").value("Created Text"));
    }

    @Test
    @WithMockUser(authorities = "JOURNALIST")
    void shouldUpdateNews() throws Exception {
        // given
        UUID newsId = UUID.randomUUID();
        NewsFromDto updatedNews = NewsFromDto.builder()
                .title("Updated Title")
                .text("Updated Text")
                .build();

        when(newsService.updateNews(eq(newsId), any(NewsCreateRequestDomain.class))).thenReturn(updatedNews);
        //when
        //then
        mockMvc.perform(put("/news/{newsId}", newsId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Updated Title",
                                  "text": "Updated Text"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.text").value("Updated Text"));
    }

    @Test
    @WithMockUser(authorities = "JOURNALIST")
    void shouldDeleteNews() throws Exception {
        // given
        UUID newsId = UUID.randomUUID();
        //when
        //then
        mockMvc.perform(delete("/news/{newsId}", newsId)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}