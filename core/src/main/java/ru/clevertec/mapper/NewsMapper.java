package ru.clevertec.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import ru.clevertec.domain.News;
import ru.clevertec.dto.NewsCreateRequest;
import ru.clevertec.dto.NewsDto;
import ru.clevertec.dto.NewsWithCommentsDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NewsMapper {
    default Page<NewsDto> toDtoPage(Page<News> news) {
        return news.map(this::toDto);
    }

    List<NewsDto> toDtoList(List<News> news);

    NewsDto toDto(News news);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "username", ignore = true)
    News toEntity(NewsCreateRequest newsDto);

    @Mapping(target = "comments", ignore = true)
    NewsWithCommentsDto toDtoWithComments(News news);

}
