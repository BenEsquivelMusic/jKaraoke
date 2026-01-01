package karaoke;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import karaoke.controller.KaraokeFxmlController;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public final class Main extends Application {

    private Consumer<Void> closeConsumer;

    public Main() {
        /* Main application class  */
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL fxmlURL = getClass().getResource("/KaraokeFxmlController.fxml");
        Objects.requireNonNull(fxmlURL);
        FXMLLoader loader = new FXMLLoader(fxmlURL);
        Parent root = loader.load();
        KaraokeFxmlController controller = loader.getController();
        this.closeConsumer = _ -> controller.closeMediaPlayer();
        Scene scene = new Scene(root);
        primaryStage.getIcons().add(new Image(Optional.ofNullable(Main.class.getResourceAsStream(ApplicationIcons.APPLICATION_ICON)).orElseThrow()));
        primaryStage.setTitle("JKaraoke");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(_ -> {
            stop();
            Platform.exit();
        });
        primaryStage.show();
    }

    @Override
    public void stop() {
        if (Objects.nonNull(closeConsumer)) {
            closeConsumer.accept(null);
        }
    }

}