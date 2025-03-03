package com.bem;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public final class KaraokeMediaViewFxmlController implements Initializable {

    @FXML
    private AnchorPane mediaViewPane;
    @FXML
    private MediaView mediaView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Objects.requireNonNull(mediaViewPane, "Media view pane was not initialized");
        Objects.requireNonNull(mediaView, "Media view was not initialized");
    }

    public void updateMediaPlayerForMediaView(MediaPlayer mediaPlayer) {
        mediaView.setMediaPlayer(mediaPlayer);
    }

    public void showMediaView() {
        Stage stage = (Stage) mediaViewPane.getScene().getWindow();
        stage.show();
    }

}
