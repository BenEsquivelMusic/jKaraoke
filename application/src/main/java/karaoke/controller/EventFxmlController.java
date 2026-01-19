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
import karaoke.EventManager;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public final class EventFxmlController implements Initializable {

    private static final Logger logger = Logger.getLogger(EventFxmlController.class.getName());

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
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Browse to save data location");
            File selectedFolder = directoryChooser.showDialog(getStage());
            if (Objects.nonNull(selectedFolder)) {
                txtEventFile.setText(selectedFolder.getAbsolutePath());
                logger.info(() -> "Selected folder: " + selectedFolder.getAbsolutePath());
            }
        } else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose .kev file");
            File selectedFile = fileChooser.showOpenDialog(getStage());
            if (Objects.nonNull(selectedFile)) {
                txtEventFile.setText(selectedFile.getAbsolutePath());
                logger.info(() -> "Selected file: " + selectedFile.getAbsolutePath());
            }
        }

        actionEvent.consume();
    }

    public void setUnEditableEventName(String eventName) {
        txtEventName.setText(eventName);
        txtEventName.setDisable(true);
    }

    public void handleCancelAction(ActionEvent actionEvent) {
        getStage().close();
        actionEvent.consume();
    }

    public void handleOkAction(ActionEvent actionEvent) {
        if (!txtEventName.isDisabled() && txtEventName.getText().isBlank()) {
            logger.warning(() -> "Event name cannot be blank");
            handleAlert("Event name cannot be blank");
        } else if (txtEventFile.getText().isBlank()) {
            logger.warning(() -> "Event file cannot be blank");
            handleAlert("Event file cannot be blank");
        } else {
            setEventManager();
            logger.info(() -> "Event manager configured for: " + txtEventName.getText());
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

    private void setEventManager() {
        File eventFile = new File(txtEventFile.getText());
        if (eventFile.isFile()) {
            this.eventManager = new EventManager(eventFile);
        } else {
            this.eventManager = new EventManager(new File(eventFile, txtEventName.getText() + ".kev"));
        }

    }

}
