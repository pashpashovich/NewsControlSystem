package ru.clevertec.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
import ru.clevertec.utils.UtilCreator;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NewsServiceTest {

    @InjectMocks
    private NewsService newsService;

    @Mock
    private NewsRepositoryPort newsRepository;

    @Mock
    private NewsMapper newsMapper;

    @Mock
    private CommentServicePort commentServicePort;

    @Mock
    private Cache cache;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("author");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @ParameterizedTest
    @MethodSource("provideSearchNewsTestCases")
    void shouldReturnExpectedResultsForSearchQuery(String query, List<News> repositoryResult, List<NewsDto> expectedDtos) {
        // given
        when(newsRepository.searchByText(query)).thenReturn(repositoryResult);
        when(newsMapper.toDtoList(repositoryResult)).thenReturn(expectedDtos);

        // when
        List<NewsDto> result = newsService.searchNews(query);

        // then
        assertThat(result).isEqualTo(expectedDtos);
        verify(newsRepository, times(1)).searchByText(query);
        verify(newsMapper, times(1)).toDtoList(repositoryResult);
    }

    static Stream<Arguments> provideSearchNewsTestCases() {
        return Stream.of(
                Arguments.of(
                        "example query",
                        List.of(
                                UtilCreator.createSampleNews(UUID.randomUUID(), "Match Title 1", "Content includes example query", "author1"),
                                UtilCreator.createSampleNews(UUID.randomUUID(), "Match Title 2", "Another content with example query", "author2"),
                                UtilCreator.createSampleNews(UUID.randomUUID(), "Match Title 3", "Another content with nothing", "author3")
                        ),
                        List.of(
                                UtilCreator.createSampleNewsDto("Match Title 1", "Content includes example query"),
                                UtilCreator.createSampleNewsDto("Match Title 2", "Another content with example query")
                        )
                ),
                Arguments.of(
                        "non-matching query",
                        List.of(
                                UtilCreator.createSampleNews(UUID.randomUUID(), "Match Title 1", "Content includes example query", "author1"),
                                UtilCreator.createSampleNews(UUID.randomUUID(), "Match Title 2", "Another content with example query", "author2")
                        ),
                        List.of()
                ),
                Arguments.of(
                        null,
                        List.of(),
                        List.of()
                )
        );
    }

    @Test
    void shouldReturnAllNews() {
        // given
        Pageable pageable = mock(Pageable.class);
        Page<News> newsPage = new PageImpl<>(List.of(new News()));
        when(newsRepository.findAll(pageable)).thenReturn(newsPage);
        when(newsMapper.toDtoPage(newsPage)).thenReturn(new PageImpl<>(List.of(new NewsDto())));

        // when
        Page<NewsDto> result = newsService.getAllNews(pageable);

        // then
        assertThat(result).isNotEmpty();
        verify(newsRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldCreateNews() {
        // given
        NewsCreateRequest request = UtilCreator.createNewsCreateRequest("Title", "Content");

        News news = UtilCreator.createSampleNews(UUID.randomUUID(), "Title", "Content", "author");

        when(newsMapper.toEntity(request)).thenReturn(news);
        when(newsRepository.save(news)).thenReturn(news);
        when(newsMapper.toDto(news)).thenReturn(UtilCreator.createSampleNewsDto(news.getTitle(), news.getText()));

        // when
        NewsDto result = newsService.createNews(request);

        // then
        assertThat(result.getTitle()).isEqualTo("Title");
        assertThat(result.getText()).isEqualTo("Content");
        verify(newsRepository, times(1)).save(news);
        verify(cache, times(1)).put(news.getId(), news);
    }

    @Test
    void shouldReturnNewsByIdWhenFoundInRepository() {
        // given
        UUID newsId = UUID.randomUUID();
        News news = UtilCreator.createSampleNews(newsId, "Test Title", "Test Content", "author");

        NewsDto expectedDto = UtilCreator.createSampleNewsDto("Test Title", "Test Content");

        when(cache.contains(newsId)).thenReturn(false);
        when(newsRepository.findById(newsId)).thenReturn(Optional.of(news));
        when(newsMapper.toDto(news)).thenReturn(expectedDto);

        // when
        NewsDto actualDto = newsService.getNewsById(newsId);

        // then
        assertThat(actualDto).isEqualTo(expectedDto);

        verify(newsRepository, times(1)).findById(newsId);

        verify(cache, times(1)).put(newsId, news);
    }

    @Test
    void shouldReturnNewsByIdWhenFoundInCache() {
        // given
        UUID newsId = UUID.randomUUID();
        News news = UtilCreator.createSampleNews(newsId, "Cached Title", "Cached Content", "author");

        NewsDto expectedDto = UtilCreator.createSampleNewsDto("Cached Title", "Cached Content");

        when(cache.contains(newsId)).thenReturn(true);
        when(cache.get(newsId)).thenReturn(news);
        when(newsMapper.toDto(news)).thenReturn(expectedDto);

        // when
        NewsDto actualDto = newsService.getNewsById(newsId);

        // then
        assertThat(actualDto).isEqualTo(expectedDto);

        verify(newsRepository, never()).findById(newsId);

        verify(cache, never()).put(any(), any());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenNewsNotFound() {
        // given
        UUID newsId = UUID.randomUUID();
        when(newsRepository.findById(newsId)).thenReturn(Optional.empty());

        // when / then
        assertThrows(NotFoundException.class, () -> newsService.getNewsById(newsId));
        verify(cache, never()).put(any(), any());
    }


    @Test
    void shouldReturnNewsWithCommentsWhenFoundInCache() {
        // given
        UUID newsId = UUID.randomUUID();
        News news = UtilCreator.createSampleNews(newsId, "Cached Title", "Cached Content", "author");

        NewsWithCommentsDto expectedDto = UtilCreator.createSampleNewsWithCommentsDto("Cached Title", "Content from cache", List.of());

        when(cache.contains(newsId)).thenReturn(true);
        when(cache.get(newsId)).thenReturn(news);
        when(newsMapper.toDtoWithComments(news)).thenReturn(expectedDto);
        when(commentServicePort.getCommentsForNews(newsId)).thenReturn(Page.empty());

        // when
        NewsWithCommentsDto result = newsService.getNewsWithComments(newsId);

        // then
        assertThat(result).isEqualTo(expectedDto);
        verify(newsRepository, never()).findById(newsId);
        verify(cache, never()).put(any(), any());
        verify(commentServicePort, times(1)).getCommentsForNews(newsId);
    }

    @Test
    void shouldReturnNewsWithCommentsWhenNotFoundInCache() {
        // given
        UUID newsId = UUID.randomUUID();
        News news = UtilCreator.createSampleNews(newsId, "Repo Title", "Content from repo", "author");

        NewsWithCommentsDto expectedDto = UtilCreator.createSampleNewsWithCommentsDto("Repo Title", "Content from repo", List.of());

        when(cache.contains(newsId)).thenReturn(false);
        when(newsRepository.findById(newsId)).thenReturn(Optional.of(news));
        when(newsMapper.toDtoWithComments(news)).thenReturn(expectedDto);
        when(commentServicePort.getCommentsForNews(newsId)).thenReturn(Page.empty());

        // when
        NewsWithCommentsDto result = newsService.getNewsWithComments(newsId);

        // then
        assertThat(result).isEqualTo(expectedDto);
        verify(cache, times(1)).put(newsId, news);
        verify(newsRepository, times(1)).findById(newsId);
        verify(commentServicePort, times(1)).getCommentsForNews(newsId);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenNewsNotInCacheOrRepo() {
        // given
        UUID newsId = UUID.randomUUID();

        when(cache.contains(newsId)).thenReturn(false);
        when(newsRepository.findById(newsId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> newsService.getNewsWithComments(newsId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format("Новость с ID %s не найдена", newsId));

        verify(cache, never()).put(any(), any());
        verify(commentServicePort, never()).getCommentsForNews(newsId);
    }

    @Test
    void shouldReturnExactCommentWhenFound() {
        // given
        UUID newsId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        CommentDto comment1 = UtilCreator.createSampleComment(UUID.randomUUID(), "First comment");
        CommentDto comment2 = UtilCreator.createSampleComment(commentId, "Exact comment");

        Page<CommentDto> commentsPage = new PageImpl<>(List.of(comment1, comment2));

        when(commentServicePort.getCommentsForNews(newsId)).thenReturn(commentsPage);

        // when
        CommentDto result = newsService.getExactComment(newsId, commentId);

        // then
        assertThat(result.getId()).isEqualTo(commentId);
        assertThat(result.getText()).isEqualTo("Exact comment");
        verify(commentServicePort, times(1)).getCommentsForNews(newsId);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenCommentNotFound() {
        // given
        UUID newsId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();

        CommentDto comment1 = UtilCreator.createSampleComment(UUID.randomUUID(), "First comment");
        CommentDto comment2 = UtilCreator.createSampleComment(UUID.randomUUID(), "Second comment");

        Page<CommentDto> commentsPage = new PageImpl<>(List.of(comment1, comment2));

        when(commentServicePort.getCommentsForNews(newsId)).thenReturn(commentsPage);

        // when / then
        assertThatThrownBy(() -> newsService.getExactComment(newsId, commentId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Комментария с таким ID не существует");

        verify(commentServicePort, times(1)).getCommentsForNews(newsId);
    }

    @Test
    void shouldUpdateNewsWhenUserIsAuthor() {
        // given
        UUID newsId = UUID.randomUUID();
        NewsCreateRequest updateRequest = UtilCreator.createNewsCreateRequest("Updated Title", "Updated Text");

        News existingNews = UtilCreator.createSampleNews(newsId, "Old Title", "Old Text", "author");

        when(newsRepository.findById(newsId)).thenReturn(Optional.of(existingNews));
        when(newsRepository.save(existingNews)).thenReturn(existingNews);
        when(newsMapper.toDto(existingNews)).thenReturn(
                UtilCreator.createSampleNewsDto(updateRequest.getTitle(), updateRequest.getText())
        );

        // when
        NewsDto result = newsService.updateNews(newsId, updateRequest);

        // then
        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getText()).isEqualTo("Updated Text");
        verify(newsRepository, times(1)).save(existingNews);
        verify(cache, times(1)).put(newsId, existingNews);
    }

    @Test
    void shouldUpdateNewsWhenUserIsAdmin() {
        // given
        UUID newsId = UUID.randomUUID();
        NewsCreateRequest updateRequest = UtilCreator.createNewsCreateRequest("Updated Title", "Updated Text");

        News existingNews = UtilCreator.createSampleNews(newsId, "Old Title", "Old Text", "anotherUser");

        when(newsRepository.findById(newsId)).thenReturn(Optional.of(existingNews));
        when(newsRepository.save(existingNews)).thenReturn(existingNews);
        when(newsMapper.toDto(existingNews)).thenReturn(
                UtilCreator.createSampleNewsDto(updateRequest.getTitle(), updateRequest.getText())
        );

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("admin");
        when(authentication.getAuthorities()).thenReturn((Collection) List.of(new SimpleGrantedAuthority("ADMIN")));
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // when
        NewsDto result = newsService.updateNews(newsId, updateRequest);

        // then
        assertThat(result.getTitle()).isEqualTo("Updated Title");
        assertThat(result.getText()).isEqualTo("Updated Text");
        verify(newsRepository, times(1)).save(existingNews);
        verify(cache, times(1)).put(newsId, existingNews);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenNewsDoesNotExist() {
        // given
        UUID newsId = UUID.randomUUID();
        NewsCreateRequest updateRequest = new NewsCreateRequest();

        when(newsRepository.findById(newsId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> newsService.updateNews(newsId, updateRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format("Новость с ID %s не найдена", newsId));

        verify(newsRepository, never()).save(any());
        verify(cache, never()).put(any(), any());
    }

    @Test
    void shouldThrowAccessDeniedExceptionWhenUserIsNotAuthorized() {
        // given
        UUID newsId = UUID.randomUUID();
        NewsCreateRequest updateRequest = UtilCreator.createNewsCreateRequest("Updated Title", "Updated Text");

        News existingNews = UtilCreator.createSampleNews(newsId, "Old Title", "Old Text", "anotherUser");

        when(newsRepository.findById(newsId)).thenReturn(Optional.of(existingNews));

        // when / then
        assertThatThrownBy(() -> newsService.updateNews(newsId, updateRequest))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Вы не имеете право на обновление этой новости");

        verify(newsRepository, never()).save(any());
        verify(cache, never()).put(any(), any());
    }

    @Test
    void shouldDeleteNewsWhenUserIsAuthor() {
        // given
        UUID newsId = UUID.randomUUID();
        News news = UtilCreator.createSampleNews(newsId, "Title", "Content", "author");

        when(newsRepository.findById(newsId)).thenReturn(Optional.of(news));

        // when
        newsService.deleteNews(newsId);

        // then
        verify(newsRepository, times(1)).deleteById(newsId);
        verify(cache, times(1)).remove(newsId);
    }

    @Test
    void shouldDeleteNewsWhenUserIsAdmin() {
        // given
        UUID newsId = UUID.randomUUID();
        News news = UtilCreator.createSampleNews(newsId, "Title", "Content", "anotherUser");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("admin");
        when(authentication.getAuthorities()).thenReturn((Collection) List.of(new SimpleGrantedAuthority("ADMIN")));

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(newsRepository.findById(newsId)).thenReturn(Optional.of(news));

        // when
        newsService.deleteNews(newsId);

        // then
        verify(newsRepository, times(1)).deleteById(newsId);
        verify(cache, times(1)).remove(newsId);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenNewsDoesNotExistForDeletion() {
        // given
        UUID newsId = UUID.randomUUID();
        when(newsRepository.findById(newsId)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> newsService.deleteNews(newsId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format("Новость с ID %s не найдена", newsId));

        verify(newsRepository, never()).deleteById(any());
        verify(cache, never()).remove(any());
    }

    @Test
    void shouldThrowAccessDeniedExceptionWhenUserIsNotAuthorizedForDeletion() {
        // given
        UUID newsId = UUID.randomUUID();
        News news = UtilCreator.createSampleNews(newsId, "Title", "Content", "anotherUser");

        when(newsRepository.findById(newsId)).thenReturn(Optional.of(news));

        // when / then
        assertThatThrownBy(() -> newsService.deleteNews(newsId))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Вы не имеете право на обновление этой новости");

        verify(newsRepository, never()).deleteById(any());
        verify(cache, never()).remove(any());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

}
