package ru.clevertec.mapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import ru.clevertec.domain.CommentFromDto;
import ru.clevertec.domain.News;
import ru.clevertec.domain.NewsCreateRequestDomain;
import ru.clevertec.domain.NewsFromDto;
import ru.clevertec.domain.NewsWithCommentsFromDto;
import ru.clevertec.dto.CommentDto;
import ru.clevertec.dto.NewsCreateRequest;
import ru.clevertec.dto.NewsDto;
import ru.clevertec.dto.NewsWithCommentsDto;
import ru.clevertec.entity.NewsEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NewsDomainMapper {

    default Page<News> toDomainPage(Page<NewsEntity> newsEntities) {
        return newsEntities.map(this::toDomain);
    }

    List<News> toDomainList(List<NewsEntity> newsEntities);

    News toDomain(NewsEntity newsEntity);

    NewsEntity toEntity(News news);

    NewsDto toDto(NewsFromDto newsFromDto);

    default Page<NewsDto> toDtoPage(Page<NewsFromDto> newsFromDtos) {
        return newsFromDtos.map(this::toDto);
    }

    NewsWithCommentsDto toNewsWithCommentsDto(NewsWithCommentsFromDto newsWithCommentsFromDto);

    CommentDto toCommentDto(CommentFromDto commentFromDto);

    List<NewsDto> toDtoList(List<NewsFromDto> newsFromDtos);

    NewsCreateRequestDomain toNewsCreateRequestDomain(NewsCreateRequest newsCreateRequest);

    CommentFromDto toCommentFromDto(CommentDto commentDto);

    default Page<CommentFromDto> toCommentFromDtoPage(Page<CommentDto> commentDtos) {
        return commentDtos.map(this::toCommentFromDto);
    }
}
