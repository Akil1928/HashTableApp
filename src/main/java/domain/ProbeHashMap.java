package domain;

import java.util.*;

public class ProbeHashMap<K, V> extends AbstractHashMap<K, V> {
    private MapEntry<K,V>[] table;        // un arreglo fijo de entradas (inicialmente todas null)
    private MapEntry<K,V> DEFUNCT = new MapEntry<>(null, null);  // centinela

    public ProbeHashMap() {
        super();
    }

    public ProbeHashMap(int cap) {
        super(cap);
    }

    public ProbeHashMap(int cap, int p) {
        super(cap, p);
    }

    /** Crea una tabla vacía con longitud igual a la capacidad actual. */
    @SuppressWarnings("unchecked")
    protected void createTable() {
        table = (MapEntry<K,V>[]) new MapEntry[capacity];  // conversión segura
    }

    /** Devuelve true si la ubicación está vacía o es el centinela "defunct". */
    private boolean isAvailable(int j) {
        return (table[j] == null || table[j] == DEFUNCT);
    }

    /** Busca la posición ideal para una entrada con clave k y valor hash h.
     * @return índice de posición encontrada (si es j ≥ 0)
     * o -(a+1) donde a es el primer índice disponible (si se encuentra al final de búsqueda)
     */
    private int findSlot(int h, K k) {
        int avail = -1;                // ningún slot disponible (hasta ahora)
        int j = h;                     // índice mientras se escanea la tabla
        do {
            if (isAvailable(j)) {      // puede estar vacío o defunct
                if (avail == -1) avail = j;  // ¡este es el primer slot disponible!
                if (table[j] == null) break;  // si está vacío, la búsqueda falla inmediatamente
            } else if (table[j].getKey().equals(k))
                return j;              // coincidencia exitosa
            j = (j+1) % capacity;      // seguir buscando (cíclicamente)
        } while (j != h);              // parar si volvemos al inicio
        return -(avail + 1);           // la búsqueda ha fallado
    }

    /** Devuelve valor asociado con clave k en bucket con valor hash h, o else null. */
    protected V bucketGet(int h, K k) {
        int j = findSlot(h, k);
        if (j < 0) return null;        // no se encontró coincidencia
        return table[j].getValue();
    }

    /** Asocia clave k con valor v en bucket con valor hash h; devuelve valor antiguo. */
    protected V bucketPut(int h, K k, V v) {
        int j = findSlot(h, k);
        if (j >= 0)                    // esta clave tiene una entrada existente
            return table[j].setValue(v);
        table[-(j+1)] = new MapEntry<>(k, v);  // convertir al índice adecuado
        n++;
        return null;
    }

    /** Elimina entrada con clave k de bucket con valor hash h (si existe). */
    protected V bucketRemove(int h, K k) {
        int j = findSlot(h, k);
        if (j < 0) return null;        // nada que eliminar
        V answer = table[j].getValue();
        table[j] = DEFUNCT;           // marcar este slot como desactivado
        n--;
        return answer;
    }

    /** Devuelve una colección iterable de todas las entradas clave-valor del mapa. */
    public Iterable<Map.Entry<K,V>> entrySet() {
        ArrayList<Map.Entry<K,V>> buffer = new ArrayList<>();
        for (int h=0; h < capacity; h++)
            if (!isAvailable(h)) buffer.add(table[h]);
        return buffer;
    }
}