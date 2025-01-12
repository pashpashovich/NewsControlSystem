package ru.clevertec.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.api.ApiResponse;
import ru.clevertec.dto.CommentDto;
import ru.clevertec.dto.NewsCreateRequest;
import ru.clevertec.dto.NewsDto;
import ru.clevertec.dto.NewsWithCommentsDto;
import ru.clevertec.mapper.NewsDomainMapper;
import ru.clevertec.service.NewsService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
@Validated
public class NewsController {

    private final NewsService newsService;
    private final NewsDomainMapper newsDomainMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<NewsDto>>> getAllNews(@ParameterObject Pageable pageable) {
        Page<NewsDto> allNews = newsDomainMapper.toDtoPage(newsService.getAllNews(pageable));
        return ResponseEntity.ok(ApiResponse.<Page<NewsDto>>builder()
                .data(allNews)
                .message("Все новости получены")
                .status(true)
                .build());
    }

    @GetMapping("/{newsId}")
    public ResponseEntity<ApiResponse<NewsDto>> getNewsById(@PathVariable UUID newsId) {
        NewsDto newsDto = newsDomainMapper.toDto(newsService.getNewsById(newsId));
        return ResponseEntity.ok(ApiResponse.<NewsDto>builder()
                .data(newsDto)
                .message("Новость успешна получена")
                .status(true)
                .build());
    }

    @GetMapping("/{newsId}/comments")
    public ResponseEntity<ApiResponse<NewsWithCommentsDto>> getNewsWithComments(@PathVariable UUID newsId) {
        NewsWithCommentsDto newsWithComments = newsDomainMapper.toNewsWithCommentsDto(newsService.getNewsWithComments(newsId));
        return ResponseEntity.ok(ApiResponse.<NewsWithCommentsDto>builder()
                .data(newsWithComments)
                .status(true)
                .message("Новость с комментариями получена")
                .build());
    }

    @GetMapping("/{newsId}/comments/{commentsId}")
    public ResponseEntity<ApiResponse<CommentDto>> getExactComment(@PathVariable UUID newsId, @PathVariable UUID commentsId) {
        CommentDto comment = newsDomainMapper.toCommentDto(newsService.getExactComment(newsId, commentsId));
        return ResponseEntity.ok(ApiResponse.<CommentDto>builder()
                .data(comment)
                .status(true)
                .message("Комментарий к новости получен")
                .build());
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<NewsDto>>> searchNews(
            @RequestParam String query) {
        List<NewsDto> searchResults = newsDomainMapper.toDtoList(newsService.searchNews(query));
        return ResponseEntity.ok(ApiResponse.<List<NewsDto>>builder()
                .data(searchResults)
                .status(true)
                .message("Результаты поиска новостей")
                .build());
    }


    @PostMapping
    public ResponseEntity<NewsDto> createNews(@Valid @RequestBody NewsCreateRequest newsDto) {
        NewsDto createdNews = newsDomainMapper.toDto(newsService.createNews(newsDomainMapper.toNewsCreateRequestDomain(newsDto)));
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNews);
    }

    @PutMapping("/{newsId}")
    public ResponseEntity<NewsDto> updateNews(
            @PathVariable UUID newsId,
            @Valid @RequestBody NewsCreateRequest newsUpdateRequest) {
        NewsDto updatedNews = newsDomainMapper.toDto(newsService.updateNews(newsId, newsDomainMapper.toNewsCreateRequestDomain(newsUpdateRequest)));
        return ResponseEntity.ok(updatedNews);
    }

    @DeleteMapping("/{newsId}")
    public ResponseEntity<Void> deleteNews(@PathVariable UUID newsId) {
        newsService.deleteNews(newsId);
        return ResponseEntity.noContent().build();
    }
}

