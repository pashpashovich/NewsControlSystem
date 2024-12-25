package ru.clevertec.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import ru.clevertec.domain.News;
import ru.clevertec.dto.NewsCreateRequest;
import ru.clevertec.dto.NewsDto;
import ru.clevertec.dto.NewsWithCommentsDto;

@Mapper(componentModel = "spring")
public interface NewsMapper {
    default Page<NewsDto> toDtoList(Page<News> news) {
        return news.map(this::toDto);
    }

    NewsDto toDto(News news);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    News toEntity(NewsCreateRequest newsDto);

    @Mapping(target = "comments", ignore = true)
    NewsWithCommentsDto toDtoWithComments(News news);

}
