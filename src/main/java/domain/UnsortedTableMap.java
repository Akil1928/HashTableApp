package domain;

import java.util.*;

public class UnsortedTableMap<K, V> {
    private List<Map.Entry<K, V>> table = new ArrayList<>();

    public V get(K key) {
        for (Map.Entry<K, V> entry : table)
            if (entry.getKey().equals(key)) return entry.getValue();
        return null;
    }

    public V put(K key, V value) {
        for (Map.Entry<K, V> entry : table) {
            if (entry.getKey().equals(key)) {
                V old = entry.getValue();
                ((Map.Entry<K, V>) entry).setValue(value);
                return old;
            }
        }
        table.add(new AbstractMap.SimpleEntry<>(key, value));
        return null;
    }

    public V remove(K key) {
        Iterator<Map.Entry<K, V>> it = table.iterator();
        while (it.hasNext()) {
            Map.Entry<K, V> entry = it.next();
            if (entry.getKey().equals(key)) {
                it.remove();
                return entry.getValue();
            }
        }
        return null;
    }

    public Iterable<Map.Entry<K, V>> entrySet() {
        return table;
    }

    public int size() {
        return table.size();
    }
}
