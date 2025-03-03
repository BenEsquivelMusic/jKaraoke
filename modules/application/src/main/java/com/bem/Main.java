package com.bem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.function.Consumer;

public final class Main extends Application {

    private Consumer<Void> closeConsumer;

    public Main() {
        /* Main application class  */
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/KaraokeFxmlController.fxml"));
        Parent root = loader.load();
        KaraokeFxmlController controller = loader.getController();
        this.closeConsumer = unused -> controller.closeMediaPlayer();
        Scene scene = new Scene(root);
        primaryStage.setTitle("JKaraoke");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        if (Objects.nonNull(closeConsumer)) {
            closeConsumer.accept(null);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}