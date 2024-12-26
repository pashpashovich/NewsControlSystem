package ru.clevertec.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class LFUCache<K, V> implements Cache<K, V> {
    private static class Node<K, V> {
        K key;
        V value;
        int frequency;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.frequency = 1;
        }
    }

    private final Map<K, Node<K, V>> cache;
    private final PriorityQueue<Node<K, V>> frequencyQueue;
    private final int maxSize;

    public LFUCache(int maxSize) {
        this.maxSize = maxSize;
        this.cache = new HashMap<>();
        this.frequencyQueue = new PriorityQueue<>((a, b) -> a.frequency - b.frequency);
    }

    @Override
    public V get(K key) {
        Node<K, V> node = cache.get(key);
        if (node != null) {
            frequencyQueue.remove(node);
            node.frequency++;
            frequencyQueue.offer(node);
            return node.value;
        }
        return null;
    }

    @Override
    public void put(K key, V value) {
        if (cache.containsKey(key)) {
            Node<K, V> node = cache.get(key);
            frequencyQueue.remove(node);
            node.value = value;
            node.frequency++;
            frequencyQueue.offer(node);
        } else {
            if (cache.size() >= maxSize) {
                Node<K, V> leastFrequent = frequencyQueue.poll();
                if (leastFrequent != null) {
                    cache.remove(leastFrequent.key);
                }
            }
            Node<K, V> newNode = new Node<>(key, value);
            cache.put(key, newNode);
            frequencyQueue.offer(newNode);
        }
    }

    @Override
    public void remove(K key) {
        Node<K, V> node = cache.remove(key);
        if (node != null) {
            frequencyQueue.remove(node);
        }
    }

    @Override
    public boolean contains(K key) {
        return cache.containsKey(key);
    }

    @Override
    public int size() {
        return cache.size();
    }
}
