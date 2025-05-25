package domain;

import java.util.*;

public class ChainHashMap<K, V> extends AbstractHashMap<K, V> {
    // un arreglo de capacidad fija de UnsortedTableMap que sirven como buckets
    private UnsortedTableMap<K,V>[] table;  // inicializado en createTable

    public ChainHashMap() {
        super();
    }

    public ChainHashMap(int cap) {
        super(cap);
    }

    public ChainHashMap(int cap, int p) {
        super(cap, p);
    }

    /** Crea una tabla vacía con longitud igual a la capacidad actual. */
    @SuppressWarnings("unchecked")
    protected void createTable() {
        table = (UnsortedTableMap<K,V>[]) new UnsortedTableMap[capacity];
    }

    /** Devuelve el valor asociado con la clave k en el bucket con valor hash h, o null. */
    protected V bucketGet(int h, K k) {
        UnsortedTableMap<K,V> bucket = table[h];
        if (bucket == null) return null;
        return bucket.get(k);
    }

    /** Asocia la clave k con el valor v en el bucket con valor hash h; devuelve valor antiguo. */
    protected V bucketPut(int h, K k, V v) {
        UnsortedTableMap<K,V> bucket = table[h];
        if (bucket == null)
            bucket = table[h] = new UnsortedTableMap<>();
        int oldSize = bucket.size();
        V answer = bucket.put(k,v);
        n += (bucket.size() - oldSize);  // el tamaño puede haber aumentado
        return answer;
    }

    /** Elimina la entrada con clave k del bucket con valor hash h (si existe). */
    protected V bucketRemove(int h, K k) {
        UnsortedTableMap<K,V> bucket = table[h];
        if (bucket == null) return null;
        int oldSize = bucket.size();
        V answer = bucket.remove(k);
        n -= (oldSize - bucket.size());  // el tamaño puede haber disminuido
        return answer;
    }

    /** Devuelve una colección iterable de todas las entradas clave-valor del mapa. */
    public Iterable<Map.Entry<K,V>> entrySet() {
        List<Map.Entry<K,V>> buffer = new ArrayList<>();
        for (int h=0; h < capacity; h++)
            if (table[h] != null)
                for (Map.Entry<K,V> entry : table[h].entrySet())
                    buffer.add(entry);
        return buffer;
    }
}