package ru.clevertec.cache;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RedisCache<K, V> implements Cache<K, V> {

    private final RedisTemplate<String, V> redisTemplate;
    private static final long TTL = 60L;

    public RedisCache(RedisTemplate<String, V> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void put(K key, V value) {
        redisTemplate.opsForValue().set(key.toString(), value, TTL, TimeUnit.SECONDS);
    }

    @Override
    public V get(K key) {
        return redisTemplate.opsForValue().get(key.toString());
    }

    @Override
    public void remove(K key) {
        redisTemplate.delete(key.toString());
    }

    @Override
    public boolean contains(K key) {
        return redisTemplate.hasKey(key.toString());
    }

    @Override
    public int size() {
        return redisTemplate.keys("cache:*").size();
    }
}
