package ru.clevertec.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.clevertec.cache.Cache;
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
    private final Cache<UUID, News> cache;


    public Page<NewsDto> getAllNews(Pageable pageable) {
        return newsMapper.toDtoPage(newsRepository.findAll(pageable));
    }

    public NewsDto createNews(NewsCreateRequest newsDto) {
        News news = newsMapper.toEntity(newsDto);
        news.setUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        News savedNews = newsRepository.save(news);
        cache.put(savedNews.getId(), savedNews);
        return newsMapper.toDto(savedNews);
    }

    public NewsDto getNewsById(UUID newsId) {
        News news = cache.contains(newsId) ? cache.get(newsId)
                : newsRepository.findById(newsId)
                .orElseThrow(() -> new NotFoundException(String.format("Новость с ID %s не найдена", newsId)));
        cache.put(newsId, news);
        return newsMapper.toDto(news);
    }

    public NewsWithCommentsDto getNewsWithComments(UUID newsId) {
        News news = cache.contains(newsId) ? cache.get(newsId)
                : newsRepository.findById(newsId)
                .orElseThrow(() -> new NotFoundException(String.format("Новость с ID %s не найдена", newsId)));
        cache.put(newsId, news);
        Page<CommentDto> comments = commentClient.getCommentsForNews(newsId);
        NewsWithCommentsDto newsWithComments = newsMapper.toDtoWithComments(news);
        newsWithComments.setComments(comments.getContent());
        return newsWithComments;
    }

    public CommentDto getExactComment(UUID newsId, UUID commentsId) {
        List<CommentDto> comments = commentClient.getCommentsForNews(newsId).getContent();
        return comments.stream()
                .filter(commentDto -> commentDto.getId().equals(commentsId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Комментария с таким ID не существует"));
    }

    public NewsDto updateNews(UUID newsId, NewsCreateRequest newsUpdateRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"));
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new NotFoundException(String.format("Новость с ID %s не найдена", newsId)));
        if (!news.getUsername().equals(username) && !isAdmin)
            throw new AccessDeniedException("Вы не имеете право на обновление этой новости");
        news.setTitle(newsUpdateRequest.getTitle());
        news.setText(newsUpdateRequest.getText());
        news = newsRepository.save(news);
        cache.put(newsId, news);
        return newsMapper.toDto(news);
    }


    public void deleteNews(UUID newsId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"));
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new NotFoundException(String.format("Новость с ID %s не найдена", newsId)));
        if (!news.getUsername().equals(username) && !isAdmin)
            throw new AccessDeniedException("Вы не имеете право на обновление этой новости");
        newsRepository.deleteById(newsId);
        cache.remove(newsId);
    }

    public List<NewsDto> searchNews(String query) {
        return newsMapper.toDtoList(newsRepository.searchByText(query));
    }
}