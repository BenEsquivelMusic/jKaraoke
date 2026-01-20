package karaoke.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaException;
import karaoke.singer.IndexedSinger;

import karaoke.util.AlertUtil;
import karaoke.util.FileChooserUtil;
import karaoke.util.LoggingUtil;
import karaoke.util.StageUtil;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class AddSongFxmlController implements Initializable {

    private static final Logger logger = LoggingUtil.getLogger(AddSongFxmlController.class);

    @FXML
    private Button buttonBrowseFile;

    @FXML
    private TextField txtSong;

    @FXML
    private TextField txtSingerName;

    @FXML
    private AnchorPane addSongAnchorPane;

    private IndexedSinger singer;

    public AddSongFxmlController() {
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //TODO
    }

    public IndexedSinger getSinger() {
        return singer;
    }

    public boolean isFormCompleted() {
        return Objects.nonNull(singer);
    }

    public void handleBrowseSong(ActionEvent actionEvent) {
        File selectedFile = FileChooserUtil.showFileChooser("Choose song file", StageUtil.getStage(addSongAnchorPane));
        if (Objects.nonNull(selectedFile)) {
            txtSong.setText(getAbsolutePath(selectedFile));
        }
        actionEvent.consume();
    }

    public void setUnEditableSingerName(String singerName) {
        txtSingerName.setText(singerName);
        txtSingerName.setDisable(true);
    }

    public void setUnEditableSong(String song) {
        txtSong.setText(song);
        txtSong.setDisable(true);
        buttonBrowseFile.setDisable(true);
    }

    private String getAbsolutePath(File selectedFile) {
        return selectedFile.toURI().toString();
    }

    public void handleCancelAction(ActionEvent actionEvent) {
        StageUtil.getStage(addSongAnchorPane).close();
        actionEvent.consume();
    }

    public void handleOkAction(ActionEvent actionEvent) {
        if (txtSingerName.getText().isBlank()) {
            logger.warning(() -> "Singer name cannot be blank");
            AlertUtil.showWarning("Singer name cannot be blank");
        } else if (txtSong.getText().isBlank()) {
            logger.warning(() -> "Song name cannot be blank");
            AlertUtil.showWarning("Song name cannot be blank");
        } else if (!setIndexedSinger()) {
            logger.warning(() -> "Invalid Media format: " + LoggingUtil.sanitizeForLogging(txtSong.getText()));
            AlertUtil.showWarning("Invalid Media format: " + txtSong.getText());
        } else {
            logger.info(() -> "Song added successfully: " + LoggingUtil.sanitizeForLogging(txtSong.getText()));
            StageUtil.getStage(addSongAnchorPane).close();
        }
        actionEvent.consume();
    }



    private boolean setIndexedSinger() {
        try {
            this.singer = new IndexedSinger(0, txtSingerName.getText(), txtSong.getText());
        } catch (IllegalArgumentException | UnsupportedOperationException | MediaException e) {
            logger.log(Level.SEVERE, "Error creating IndexedSinger", e);
            return false;
        }
        return true;
    }

}
