package ru.clevertec.mapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import ru.clevertec.domain.News;
import ru.clevertec.entity.NewsEntity;

@Mapper(componentModel = "spring")
public interface NewsDomainMapper {

    default Page<News> toDomainList(Page<NewsEntity> newsEntities) {
        return newsEntities.map(this::toDomain);
    }

    News toDomain(NewsEntity newsEntity);

    NewsEntity toEntity(News news);

}
