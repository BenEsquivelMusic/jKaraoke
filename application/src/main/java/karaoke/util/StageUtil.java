package karaoke.util;

import javafx.scene.Node;
import javafx.stage.Stage;

public final class StageUtil {

    private StageUtil() {
        /* Utility class */
    }

    /**
     * Gets the Stage from a JavaFX Node.
     *
     * @param node the node to get the stage from
     * @return the stage containing the node
     */
    public static Stage getStage(Node node) {
        return (Stage) node.getScene().getWindow();
    }

}
