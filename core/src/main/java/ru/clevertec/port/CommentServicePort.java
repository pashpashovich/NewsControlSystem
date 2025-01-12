package ru.clevertec.port;

import org.springframework.data.domain.Page;
import ru.clevertec.domain.CommentFromDto;

import java.util.UUID;

public interface CommentServicePort {
    Page<CommentFromDto> getCommentsForNews(UUID newsId);
}
