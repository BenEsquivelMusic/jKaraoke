package karaoke.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import karaoke.util.StageUtil;

import java.net.URL;
import java.util.ResourceBundle;

public final class SettingsFxmlController implements Initializable {

    @FXML
    private VBox vBoxPane;

    @FXML
    private ColorPicker colorPickerSpectrum;

    @FXML
    private ToggleButton toggleAutoSave;

    @FXML
    private Button buttonApply;

    @FXML
    private Button buttonCancel;

    private boolean formCompleted = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set default values
        colorPickerSpectrum.setValue(Color.BLUE);
        toggleAutoSave.setSelected(false);
        updateToggleButtonText();

        // Add listener to update toggle button text when state changes
        toggleAutoSave.selectedProperty().addListener((_, _, _) -> updateToggleButtonText());
    }

    public void show() {
        StageUtil.getStage(vBoxPane).showAndWait();
    }

    @FXML
    public void handleApply() {
        formCompleted = true;
        closeWindow();
    }

    @FXML
    public void handleCancel() {
        formCompleted = false;
        closeWindow();
    }

    private void closeWindow() {
        StageUtil.getStage(buttonApply).close();
    }

    private void updateToggleButtonText() {
        toggleAutoSave.setText(toggleAutoSave.isSelected() ? "On" : "Off");
    }

    public boolean isFormCompleted() {
        return formCompleted;
    }

    public Color getSpectrumColor() {
        return colorPickerSpectrum.getValue();
    }

    public void setSpectrumColor(Color color) {
        if (color != null) {
            colorPickerSpectrum.setValue(color);
        }
    }

    public boolean isAutoSaveEnabled() {
        return toggleAutoSave.isSelected();
    }

    public void setAutoSaveEnabled(boolean enabled) {
        toggleAutoSave.setSelected(enabled);
        updateToggleButtonText();
    }
}
