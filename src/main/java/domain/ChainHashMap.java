package domain;

import java.util.*;

public class ChainHashMap<K, V> {
    private static final int CAPACITY = 17;
    private UnsortedTableMap<K, V>[] table;
    private int size = 0;

    @SuppressWarnings("unchecked")
    public ChainHashMap() {
        table = new UnsortedTableMap[CAPACITY];
    }

    private int hashValue(K key) {
        return Math.abs(key.hashCode() % CAPACITY);
    }

    public V get(K key) {
        int h = hashValue(key);
        UnsortedTableMap<K, V> bucket = table[h];
        return bucket == null ? null : bucket.get(key);
    }

    public V put(K key, V value) {
        int h = hashValue(key);
        if (table[h] == null)
            table[h] = new UnsortedTableMap<>();
        int oldSize = table[h].size();
        V oldValue = table[h].put(key, value);
        size += table[h].size() - oldSize;
        return oldValue;
    }

    public V remove(K key) {
        int h = hashValue(key);
        UnsortedTableMap<K, V> bucket = table[h];
        if (bucket == null) return null;
        int oldSize = bucket.size();
        V removed = bucket.remove(key);
        size -= (oldSize - bucket.size());
        return removed;
    }

    public Iterable<Map.Entry<K, V>> entrySet() {
        List<Map.Entry<K, V>> buffer = new ArrayList<>();
        for (UnsortedTableMap<K, V> bucket : table)
            if (bucket != null)
                for (Map.Entry<K, V> entry : bucket.entrySet())
                    buffer.add(entry);
        return buffer;
    }

    public int size() {
        return size;
    }
}
