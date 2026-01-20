package karaoke;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import karaoke.controller.KaraokeFxmlController;
import karaoke.util.ApplicationIcons;
import karaoke.util.LoggingUtil;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Main extends Application {

    private static final Logger logger = LoggingUtil.getLogger(Main.class);

    public Main() {
        /* Main application class  */
    }

    static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info(() -> "Starting jKaraoke application...");
        try {
            URL fxmlURL = getClass().getResource("/KaraokeFxmlController.fxml");
            Objects.requireNonNull(fxmlURL);
            FXMLLoader loader = new FXMLLoader(fxmlURL);
            Parent root = loader.load();
            KaraokeFxmlController controller = loader.getController();
            Scene scene = new Scene(root);
            primaryStage.getIcons().add(new Image(Optional.ofNullable(Main.class.getResourceAsStream(ApplicationIcons.APPLICATION_ICON)).orElseThrow()));
            primaryStage.setTitle("JKaraoke");
            primaryStage.setScene(scene);
            primaryStage.setOnCloseRequest(_ -> {
                logger.info(() -> "Application closing.");
                controller.close();
            });
            primaryStage.show();
            logger.info(() -> "Application started successfully.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to start application", e);
            throw e;
        }
    }

}