package ru.clevertec.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.clevertec.cache.Cache;
import ru.clevertec.cache.LFUCache;
import ru.clevertec.cache.LRUCache;
import ru.clevertec.domain.News;

import java.util.UUID;

@Configuration
public class CacheConfig {

    @Value("${cache.maxSize:100}")
    private int maxSize;

    @Bean
    @Profile("lru")
    public Cache<UUID, News>  lruCache() {
        return new LRUCache<>(maxSize);
    }

    @Bean
    @Profile("lfu")
    public Cache<UUID, News>  lfuCache() {
        return new LFUCache<>(maxSize);
    }
}
