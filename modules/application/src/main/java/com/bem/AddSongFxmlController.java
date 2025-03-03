package com.bem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public final class AddSongFxmlController implements Initializable {

    @FXML
    private TextField txtSong;
    @FXML
    private TextField txtSingerName;
    @FXML
    private AnchorPane addSongAnchorPane;
    private boolean formCompleted;

    public AddSongFxmlController() {
        this.formCompleted = false;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public String getSong() {
        return txtSong.getText();
    }

    public String getSingerName() {
        return txtSingerName.getText();
    }

    public boolean isFormCompleted() {
        return formCompleted;
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

    private String getAbsolutePath(File selectedFile) {
        return selectedFile.toURI().toString();
    }

    public void handleCancelAction(ActionEvent actionEvent) {
        getStage().close();
        actionEvent.consume();
    }

    private Stage getStage() {
        return (Stage) addSongAnchorPane.getScene().getWindow();
    }

    public void handleOkAction(ActionEvent actionEvent) {
        if (txtSingerName.getText().isBlank()) {
            handleAlert("Singer name cannot be blank");
        } else if (txtSong.getText().isBlank()) {
            handleAlert("Song name cannot be blank");
        } else {
            this.formCompleted = true;
            getStage().close();
        }
        actionEvent.consume();
    }

    private void handleAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.showAndWait();
    }

}
