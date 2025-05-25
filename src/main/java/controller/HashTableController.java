package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import domain.ChainHashMap;

import java.util.Map;

public class HashTableController {

    @FXML private TextField keyField;
    @FXML private TextField valueField;
    @FXML private TextArea outputArea;

    private final ChainHashMap<String, String> map = new ChainHashMap<>();

    @FXML
    public void onInsert() {
        String key = keyField.getText();
        String value = valueField.getText();
        if (!key.isEmpty() && !value.isEmpty()) {
            map.put(key, value);
            outputArea.setText("Insertado: " + key + " -> " + value);
        }
    }

    @FXML
    public void onSearch() {
        String key = keyField.getText();
        if (!key.isEmpty()) {
            String value = map.get(key);
            outputArea.setText(value != null ? "Encontrado: " + key + " -> " + value : "Clave no encontrada.");
        }
    }

    @FXML
    public void onRemove() {
        String key = keyField.getText();
        if (!key.isEmpty()) {
            String removed = map.remove(key);
            outputArea.setText(removed != null ? "Eliminado: " + key : "Clave no encontrada.");
        }
    }

    @FXML
    public void onShow() {
        StringBuilder result = new StringBuilder("Contenido de la Hash Table:\n");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            result.append(entry.getKey()).append(" -> ").append(entry.getValue()).append("\n");
        }
        outputArea.setText(result.toString());
    }

    @FXML
    public void onSize() {
        outputArea.setText("Tamaño actual: " + map.size());
    }
}
