package com.healthassist.ui;

import javafx.scene.Scene;
import javafx.scene.control.Alert;

public final class UiUtil {
    private UiUtil() {
    }

    public static void applyTheme(Scene scene) {
        String stylesheet = UiUtil.class.getResource("/com/healthassist/styles.css").toExternalForm();
        if (!scene.getStylesheets().contains(stylesheet)) {
            scene.getStylesheets().add(stylesheet);
        }
    }

    static void showInfo(String title, String message) {
        show(Alert.AlertType.INFORMATION, title, message);
    }

    static void showError(String title, Exception exception) {
        show(Alert.AlertType.ERROR, title, exception.getMessage());
    }

    static void showValidation(String message) {
        show(Alert.AlertType.WARNING, "Validation", message);
    }

    private static void show(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
