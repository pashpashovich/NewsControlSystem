package ru.clevertec.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.clevertec.client.CommentClient;
import ru.clevertec.domain.CommentFromDto;
import ru.clevertec.mapper.NewsDomainMapper;
import ru.clevertec.port.CommentServicePort;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CommentServiceAdapter implements CommentServicePort {

    private final CommentClient commentClient;
    private final NewsDomainMapper newsDomainMapper;

    @Override
    public Page<CommentFromDto> getCommentsForNews(UUID newsId) {
        return newsDomainMapper.toCommentFromDtoPage(commentClient.getCommentsForNews(newsId).getBody().getData());
    }
}
