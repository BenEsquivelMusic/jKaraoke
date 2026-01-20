package karaoke.util;

import javafx.scene.Node;
import javafx.stage.Stage;

import java.util.Objects;

public final class StageUtil {

    private StageUtil() {
        /* Utility class */
    }

    /**
     * Gets the Stage from a JavaFX Node.
     *
     * @param node the node to get the stage from
     * @return the stage containing the node
     * @throws NullPointerException if node, its scene, or window is null
     * @throws ClassCastException if the window is not a Stage
     */
    public static Stage getStage(Node node) {
        Objects.requireNonNull(node, "Node cannot be null");
        Objects.requireNonNull(node.getScene(), "Node's scene cannot be null");
        var window = node.getScene().getWindow();
        Objects.requireNonNull(window, "Node's window cannot be null");
        return (Stage) window;
    }

}
