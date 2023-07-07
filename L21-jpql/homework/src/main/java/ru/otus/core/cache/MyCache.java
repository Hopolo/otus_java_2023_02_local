package ru.otus.core.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class MyCache<K, V> implements HwCache<K, V> {
//Надо реализовать эти методы

    private final List<HwListener<K, V>> listeners = new ArrayList<>();
    private final Map<K, V> cacheMap = new WeakHashMap<>();

    @Override
    public void put(
        K key,
        V value
    ) {
        listeners.forEach(listener -> listener.notify(key, value, "put"));
        cacheMap.put(key, value);
    }

    @Override
    public void remove(K key) {
        listeners.forEach(listener -> listener.notify(key, null, "remove"));
        cacheMap.remove(key);
    }

    @Override
    public V get(K key) {
        listeners.forEach(listener -> listener.notify(key, null, "get"));
        return cacheMap.get(key);
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        listeners.remove(listener);
    }
}
