package ru.clevertec.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.clevertec.client.CommentClient;
import ru.clevertec.dto.CommentDto;
import ru.clevertec.port.CommentServicePort;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CommentServiceAdapter implements CommentServicePort {

    private final CommentClient commentClient;

    @Override
    public Page<CommentDto> getCommentsForNews(UUID newsId) {
        return commentClient.getCommentsForNews(newsId);
    }
}
