package ru.clevertec.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import ru.clevertec.cache.Cache;
import ru.clevertec.cache.LFUCache;
import ru.clevertec.cache.LRUCache;
import ru.clevertec.cache.RedisCache;
import ru.clevertec.domain.News;

import java.util.UUID;

@Configuration
public class CacheConfig {

    @Value("${cache.maxSize:100}")
    private int maxSize;

    @Bean
    @Profile("lru")
    public Cache<UUID, News> lruCache() {
        return new LRUCache<>(maxSize);
    }

    @Bean
    @Profile("lfu")
    public Cache<UUID, News> lfuCache() {
        return new LFUCache<>(maxSize);
    }

    @Bean
    @Profile("redis")
    public RedisTemplate<String, News> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, News> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        return redisTemplate;
    }


    @Bean
    @Profile("redis")
    public Cache<UUID, News> redisCache(RedisTemplate<String, News> redisTemplate) {
        return new RedisCache<>(redisTemplate);
    }
}
