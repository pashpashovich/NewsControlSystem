package ru.clevertec.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import ru.clevertec.domain.News;
import ru.clevertec.domain.NewsCreateRequestDomain;
import ru.clevertec.domain.NewsFromDto;
import ru.clevertec.domain.NewsWithCommentsFromDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NewsMapper {
    default Page<NewsFromDto> toDtoPage(Page<News> news) {
        return news.map(this::toDto);
    }

    List<NewsFromDto> toDtoList(List<News> news);

    NewsFromDto toDto(News news);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "username", ignore = true)
    News toEntity(NewsCreateRequestDomain newsDto);

    @Mapping(target = "comments", ignore = true)
    NewsWithCommentsFromDto toDtoWithComments(News news);
}
