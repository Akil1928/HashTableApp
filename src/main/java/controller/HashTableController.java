
package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import domain.AbstractHashMap;
import domain.ChainHashMap;
import domain.ProbeHashMap;
import domain.UnsortedTableMap;

import java.lang.reflect.Field;
import java.util.Map;

public class HashTableController {

    @FXML private TextField keyField;
    @FXML private TextField valueField;
    @FXML private TextArea outputArea;
    @FXML private ComboBox<String> hashTypeComboBox;

    private AbstractHashMap<String, String> map;

    @FXML
    public void initialize() {
        // Configurar el ComboBox con los tipos de hash
        hashTypeComboBox.getItems().addAll("Encadenamiento", "Direccionamiento Abierto");
        hashTypeComboBox.setValue("Encadenamiento");

        // Configurar el mapa inicial
        updateMapType();

        // Agregar un listener para cambiar el tipo de mapa
        hashTypeComboBox.setOnAction(e -> updateMapType());
    }

    private void updateMapType() {
        String selected = hashTypeComboBox.getValue();
        if ("Encadenamiento".equals(selected)) {
            map = new ChainHashMap<>();
        } else {
            map = new ProbeHashMap<>();
        }
        outputArea.setText("Usando implementación: " + selected);
    }

    @FXML
    public void onInsert() {
        String key = keyField.getText();
        String value = valueField.getText();
        if (!key.isEmpty() && !value.isEmpty()) {
            map.put(key, value);
            outputArea.setText("Insertado: " + key + " -> " + value);
            outputArea.appendText("\nValor hash: " + getHashValue(key));
        } else {
            outputArea.setText("Error: La clave y el valor no pueden estar vacíos.");
        }
    }

    @FXML
    public void onSearch() {
        String key = keyField.getText();
        if (!key.isEmpty()) {
            String value = map.get(key);
            if (value != null) {
                outputArea.setText("Encontrado: " + key + " -> " + value);
                outputArea.appendText("\nValor hash: " + getHashValue(key));
            } else {
                outputArea.setText("Clave no encontrada.");
            }
        } else {
            outputArea.setText("Error: Ingresa una clave para buscar.");
        }
    }

    @FXML
    public void onRemove() {
        String key = keyField.getText();
        if (!key.isEmpty()) {
            int hashValue = getHashValue(key);
            String removed = map.remove(key);
            if (removed != null) {
                outputArea.setText("Eliminado: " + key);
                outputArea.appendText("\nValor hash: " + hashValue);
            } else {
                outputArea.setText("Clave no encontrada.");
            }
        } else {
            outputArea.setText("Error: Ingresa una clave para eliminar.");
        }
    }

    @FXML
    public void onShow() {
        StringBuilder result = new StringBuilder();

        // Obtener capacidad
        int capacity = getCapacity();
        result.append("ESTRUCTURA DE LA TABLA HASH\n");
        result.append("==========================\n");
        result.append("Tipo: ").append(hashTypeComboBox.getValue()).append("\n");
        result.append("Capacidad: ").append(capacity).append("\n");
        result.append("Tamaño actual: ").append(map.size()).append("\n\n");

        if ("Encadenamiento".equals(hashTypeComboBox.getValue())) {
            // Mostrar tabla de encadenamiento
            result.append(showChainHashMap(capacity));
        } else {
            // Mostrar tabla de direccionamiento abierto
            result.append(showProbeHashMap(capacity));
        }

        outputArea.setText(result.toString());
    }

    private String showChainHashMap(int capacity) {
        StringBuilder result = new StringBuilder();

        try {
            // Acceder al campo table mediante reflexión
            Field tableField = ChainHashMap.class.getDeclaredField("table");
            tableField.setAccessible(true);
            UnsortedTableMap<String, String>[] table =
                    (UnsortedTableMap<String, String>[]) tableField.get(map);

            // Mostrar cada bucket y su contenido
            for (int i = 0; i < capacity; i++) {
                result.append("Bucket ").append(i).append(": ");

                if (table[i] == null) {
                    result.append("vacío\n");
                } else {
                    // Mostrar elementos en el bucket
                    int bucketSize = table[i].size();
                    result.append("(").append(bucketSize).append(" elementos)\n");

                    for (Map.Entry<String, String> entry : table[i].entrySet()) {
                        result.append("   → ").append(entry.getKey())
                                .append(" : ").append(entry.getValue())
                                .append(" (hash: ").append(Math.abs(entry.getKey().hashCode() % capacity))
                                .append(")\n");
                    }
                }
            }
        } catch (Exception e) {
            result.append("Error al acceder a la estructura interna: ").append(e.getMessage());
        }

        return result.toString();
    }

    private String showProbeHashMap(int capacity) {
        StringBuilder result = new StringBuilder();

        try {
            // Obtener todas las entradas
            Map.Entry<String, String>[] entries = new Map.Entry[capacity];
            int index = 0;
            for (Map.Entry<String, String> entry : map.entrySet()) {
                entries[index++] = entry;
            }

            // Mostrar cada posición de la tabla
            for (int i = 0; i < capacity; i++) {
                result.append("Posición ").append(i).append(": ");

                boolean occupied = false;
                for (Map.Entry<String, String> entry : entries) {
                    if (entry != null && Math.abs(entry.getKey().hashCode() % capacity) == i) {
                        result.append(entry.getKey())
                                .append(" : ").append(entry.getValue())
                                .append(" (hash original: ").append(Math.abs(entry.getKey().hashCode() % capacity))
                                .append(")\n");
                        occupied = true;
                        break;
                    }
                }

                if (!occupied) {
                    result.append("vacío\n");
                }
            }



        } catch (Exception e) {
            result.append("Error al acceder a la estructura interna: ").append(e.getMessage());
        }

        return result.toString();
    }

    @FXML
    public void onSize() {
        outputArea.setText("Tamaño actual: " + map.size());
    }

    // Métodos auxiliares para obtener información de la tabla hash

    private int getHashValue(String key) {
        try {
            // Usar reflexión para acceder al método hashValue
            java.lang.reflect.Method hashValueMethod =
                    AbstractHashMap.class.getDeclaredMethod("hashValue", Object.class);
            hashValueMethod.setAccessible(true);
            return (int) hashValueMethod.invoke(map, key);
        } catch (Exception e) {
            // En caso de error, usar una aproximación
            return Math.abs(key.hashCode() % getCapacity());
        }
    }

    private int getCapacity() {
        try {
            // Usar reflexión para acceder al campo capacity
            Field capacityField = AbstractHashMap.class.getDeclaredField("capacity");
            capacityField.setAccessible(true);
            return (int) capacityField.get(map);
        } catch (Exception e) {
            // En caso de error, devolver un valor predeterminado
            return 17;
        }
    }
}