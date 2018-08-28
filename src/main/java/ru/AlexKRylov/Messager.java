package ru.AlexKRylov;

import javafx.scene.control.Alert;

public class Messager {
    public static void setMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
