package karaoke.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import karaoke.event.EventManager;
import karaoke.util.AlertUtil;
import karaoke.util.FileChooserUtil;
import karaoke.util.LoggingUtil;
import karaoke.util.StageUtil;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public final class EventFxmlController implements Initializable {

    private static final Logger logger = LoggingUtil.getLogger(EventFxmlController.class);

    @FXML
    private Label labelSaveFile;

    @FXML
    private TextField txtEventFile;

    @FXML
    private TextField txtEventName;

    @FXML
    private AnchorPane addSongAnchorPane;

    private boolean isCreateEvent;
    private EventManager eventManager;

    public EventFxmlController() {
        /* FXML controller class */
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //TODO
    }

    public void setIsCreateEvent(boolean isCreateEvent) {
        this.isCreateEvent = isCreateEvent;
        labelSaveFile.setText("Folder");
    }
    public String getEventName() {
        return txtEventName.getText();
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public boolean isFormCompleted() {
        return Objects.nonNull(eventManager);
    }

    @FXML
    public void handleBrowseEventFile(ActionEvent actionEvent) {
        if (isCreateEvent) {
            File selectedFolder = FileChooserUtil.showDirectoryChooser("Browse to save data location", StageUtil.getStage(addSongAnchorPane));
            if (Objects.nonNull(selectedFolder)) {
                txtEventFile.setText(selectedFolder.getAbsolutePath());
                logger.info(() -> "Selected folder: " + LoggingUtil.sanitizeForLogging(selectedFolder.getAbsolutePath()));
            }
        } else {
            File selectedFile = FileChooserUtil.showFileChooser("Choose .kev file", StageUtil.getStage(addSongAnchorPane));
            if (Objects.nonNull(selectedFile)) {
                txtEventFile.setText(selectedFile.getAbsolutePath());
                logger.info(() -> "Selected file: " + LoggingUtil.sanitizeForLogging(selectedFile.getAbsolutePath()));
            }
        }

        actionEvent.consume();
    }

    public void setUnEditableEventName(String eventName) {
        txtEventName.setText(eventName);
        txtEventName.setDisable(true);
    }

    public void handleCancelAction(ActionEvent actionEvent) {
        StageUtil.getStage(addSongAnchorPane).close();
        actionEvent.consume();
    }

    public void handleOkAction(ActionEvent actionEvent) {
        if (!txtEventName.isDisabled() && txtEventName.getText().isBlank()) {
            logger.warning(() -> "Event name cannot be blank");
            AlertUtil.showWarning("Event name cannot be blank");
        } else if (txtEventFile.getText().isBlank()) {
            logger.warning(() -> "Event file cannot be blank");
            AlertUtil.showWarning("Event file cannot be blank");
        } else {
            setEventManager();
            logger.info(() -> "Event manager configured for: " + LoggingUtil.sanitizeForLogging(txtEventName.getText()));
            StageUtil.getStage(addSongAnchorPane).close();
        }
        actionEvent.consume();
    }



    private void setEventManager() {
        File eventFile = new File(txtEventFile.getText());
        if (eventFile.isFile()) {
            this.eventManager = new EventManager(eventFile);
        } else {
            this.eventManager = new EventManager(new File(eventFile, txtEventName.getText() + ".kev"));
        }

    }

}
