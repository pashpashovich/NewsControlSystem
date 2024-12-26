package ru.clevertec.cache;

public interface Cache<K, V> {
    V get(K key);
    void put(K key, V value);
    void remove(K key);
    boolean contains(K key);
    int size();
}
