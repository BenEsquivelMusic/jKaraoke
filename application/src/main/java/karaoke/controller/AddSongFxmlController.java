package karaoke.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaException;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import karaoke.IndexedSinger;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class AddSongFxmlController implements Initializable {

    private static final Logger logger = Logger.getLogger(AddSongFxmlController.class.getName());

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
        /* FXML controller class */
    }

    private static String sanitizeForLogging(String input) {
        if (input == null) {
            return "";
        }
        return input.replace('\n', '_').replace('\r', '_').replace('\t', ' ');
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
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose song file");
        File selectedFile = fileChooser.showOpenDialog(getStage());
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
        getStage().close();
        actionEvent.consume();
    }

    public void handleOkAction(ActionEvent actionEvent) {
        if (txtSingerName.getText().isBlank()) {
            logger.warning(() -> "Singer name cannot be blank");
            handleAlert("Singer name cannot be blank");
        } else if (txtSong.getText().isBlank()) {
            logger.warning(() -> "Song name cannot be blank");
            handleAlert("Song name cannot be blank");
        } else if (!setIndexedSinger()) {
            logger.warning(() -> "Invalid Media format: " + sanitizeForLogging(txtSong.getText()));
            handleAlert("Invalid Media format: " + txtSong.getText());
        } else {
            logger.info(() -> "Song added successfully: " + sanitizeForLogging(txtSong.getText()));
            getStage().close();
        }
        actionEvent.consume();
    }

    private Stage getStage() {
        return (Stage) addSongAnchorPane.getScene().getWindow();
    }

    private void handleAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.showAndWait();
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
