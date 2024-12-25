package ru.clevertec.port;

import org.springframework.data.domain.Page;
import ru.clevertec.dto.CommentDto;

import java.util.UUID;

public interface CommentServicePort {
    Page<CommentDto> getCommentsForNews(UUID newsId);
}
