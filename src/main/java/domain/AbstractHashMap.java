package domain;

import java.util.*;

public abstract class AbstractHashMap<K, V> {
    protected int n = 0;          // número de entradas en el diccionario
    protected int capacity;       // longitud de la tabla
    private int prime;            // factor primo
    private long scale, shift;    // factores de escala y desplazamiento

    public AbstractHashMap(int cap, int p) {
        prime = p;
        capacity = cap;
        Random rand = new Random();
        scale = rand.nextInt(prime-1) + 1;
        shift = rand.nextInt(prime);
        createTable();
    }

    public AbstractHashMap(int cap) {
        this(cap, 109345121);
    }  // valor primo predeterminado

    public AbstractHashMap() {
        this(17);
    }  // capacidad predeterminada

    // métodos públicos
    public int size() {
        return n;
    }

    public V get(K key) {
        return bucketGet(hashValue(key), key);
    }

    public V remove(K key) {
        return bucketRemove(hashValue(key), key);
    }

    public V put(K key, V value) {
        V answer = bucketPut(hashValue(key), key, value);
        if (n > capacity / 2)      // mantener factor de carga <= 0.5
            resize(2 * capacity - 1);  // (o encontrar un primo cercano)
        return answer;
    }

    // utilidades privadas
    protected int hashValue(K key) {
        return (int) ((Math.abs(key.hashCode() * scale + shift) % prime) % capacity);
    }

    private void resize(int newCap) {
        ArrayList<Map.Entry<K,V>> buffer = new ArrayList<>(n);
        for (Map.Entry<K,V> e : entrySet())
            buffer.add(e);
        capacity = newCap;
        createTable();    // basada en la capacidad actualizada
        n = 0;            // se recalculará durante la reinserción
        for (Map.Entry<K,V> e : buffer)
            put(e.getKey(), e.getValue());
    }

    // métodos abstractos protegidos para ser implementados por subclases
    protected abstract void createTable();
    protected abstract V bucketGet(int h, K k);
    protected abstract V bucketPut(int h, K k, V v);
    protected abstract V bucketRemove(int h, K k);
    public abstract Iterable<Map.Entry<K,V>> entrySet();
}