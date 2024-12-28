package ru.clevertec.mapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import ru.clevertec.domain.News;
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

}
