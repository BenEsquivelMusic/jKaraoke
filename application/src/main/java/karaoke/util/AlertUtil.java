package karaoke.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public final class AlertUtil {

    private AlertUtil() {
        /* Utility class */
    }

    /**
     * Shows a warning alert dialog with the given message.
     *
     * @param message the message to display
     */
    public static void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText(message);
        alert.getButtonTypes().setAll(ButtonType.OK);
        alert.showAndWait();
    }

    /**
     * Shows an information alert dialog with the given message.
     *
     * @param message the message to display
     */
    public static void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(message);
        alert.showAndWait();
    }

}
