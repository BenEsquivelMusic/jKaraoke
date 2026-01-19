package karaoke.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import karaoke.eq.EqualizerSettings;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public final class EqualizerFxmlController implements Initializable {

    private static final String EQS_EXTENSION = ".eqs";

    @FXML
    private VBox equalizerPane;
    @FXML
    private ComboBox<EqualizerSettings> presetComboBox;
    @FXML
    private Slider slider60Hz;
    @FXML
    private Slider slider170Hz;
    @FXML
    private Slider slider310Hz;
    @FXML
    private Slider slider600Hz;
    @FXML
    private Slider slider1kHz;
    @FXML
    private Slider slider3kHz;
    @FXML
    private Slider slider6kHz;
    @FXML
    private Slider slider12kHz;
    @FXML
    private Slider slider14kHz;
    @FXML
    private Slider slider16kHz;
    @FXML
    private TextField customPresetName;
    @FXML
    private Button saveCustomButton;
    @FXML
    private Button deleteCustomButton;
    @FXML
    private CheckBox enableEqualizer;
    @FXML
    private Button loadSettingsButton;

    private List<Slider> bandSliders;
    private ObservableList<EqualizerSettings> presets;
    private List<EqualizerSettings> customPresets;
    private MediaPlayer mediaPlayer;
    private Consumer<List<EqualizerSettings>> saveCustomPresetsCallback;
    private boolean isUpdatingSliders = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bandSliders = Arrays.asList(slider60Hz, slider170Hz, slider310Hz, slider600Hz,
                slider1kHz, slider3kHz, slider6kHz, slider12kHz, slider14kHz, slider16kHz);

        customPresets = new ArrayList<>();
        presets = FXCollections.observableArrayList();
        loadDefaultPresets();

        presetComboBox.setItems(presets);
        presetComboBox.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
            if (newValue != null && !isUpdatingSliders) {
                applyPreset(newValue);
            }
        });

        for (Slider slider : bandSliders) {
            configureSlider(slider);
        }

        enableEqualizer.selectedProperty().addListener((_, _, enabled) -> {
            bandSliders.forEach(s -> s.setDisable(!enabled));
            presetComboBox.setDisable(!enabled);
            saveCustomButton.setDisable(!enabled);
            customPresetName.setDisable(!enabled);
            if (loadSettingsButton != null) {
                loadSettingsButton.setDisable(!enabled);
            }
            if (!enabled) {
                resetToFlat();
            }
        });

        presetComboBox.getSelectionModel().selectFirst();
    }

    private void loadDefaultPresets() {
        presets.addAll(Arrays.asList(
                EqualizerSettings.flat(),
                EqualizerSettings.rock(),
                EqualizerSettings.pop(),
                EqualizerSettings.jazz(),
                EqualizerSettings.classical(),
                EqualizerSettings.vocal(),
                EqualizerSettings.bass(),
                EqualizerSettings.treble()
        ));
    }

    public void loadCustomPresets(List<EqualizerSettings> customs) {
        if (customs != null) {
            customPresets.clear();
            customPresets.addAll(customs);
            presets.addAll(customs);
        }
    }

    public void setSaveCallback(Consumer<List<EqualizerSettings>> callback) {
        this.saveCustomPresetsCallback = callback;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
        applyEqualizerSettings();
    }

    private void configureSlider(Slider slider) {
        slider.setMin(-12);
        slider.setMax(12);
        slider.setValue(0);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(6);
        slider.valueProperty().addListener((_, _, _) -> {
            if (!isUpdatingSliders) {
                applyEqualizerSettings();
            }
        });
    }

    private void applyPreset(EqualizerSettings settings) {
        isUpdatingSliders = true;
        double[] values = settings.getBandValues();
        for (int i = 0; i < bandSliders.size(); i++) {
            bandSliders.get(i).setValue(values[i]);
        }
        isUpdatingSliders = false;
        applyEqualizerSettings();
        deleteCustomButton.setDisable(settings.isPreset());
    }

    private void applyEqualizerSettings() {
        if (mediaPlayer != null && enableEqualizer.isSelected()) {
            var bands = mediaPlayer.getAudioEqualizer().getBands();
            for (int i = 0; i < Math.min(bands.size(), bandSliders.size()); i++) {
                bands.get(i).setGain(bandSliders.get(i).getValue());
            }
        }
    }

    private void resetToFlat() {
        isUpdatingSliders = true;
        for (Slider slider : bandSliders) {
            slider.setValue(0);
        }
        isUpdatingSliders = false;
        if (mediaPlayer != null) {
            var bands = mediaPlayer.getAudioEqualizer().getBands();
            for (var band : bands) {
                band.setGain(0);
            }
        }
    }

    @FXML
    public void handleSaveCustomPreset() {
        String name = customPresetName.getText();
        if (name == null || name.isBlank()) {
            showAlert("Please enter a name for your custom preset.");
            return;
        }
        double[] values = new double[EqualizerSettings.BAND_COUNT];
        for (int i = 0; i < bandSliders.size(); i++) {
            values[i] = bandSliders.get(i).getValue();
        }
        EqualizerSettings custom = new EqualizerSettings(name.trim(), values, false);
        customPresets.add(custom);
        presets.add(custom);
        presetComboBox.getSelectionModel().select(custom);
        customPresetName.clear();
        if (saveCustomPresetsCallback != null) {
            saveCustomPresetsCallback.accept(new ArrayList<>(customPresets));
        }
        
        // Prompt user to save to file
        saveEqualizerSettingsToFile(custom);
    }

    private void saveEqualizerSettingsToFile(EqualizerSettings settings) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Equalizer Settings");
        fileChooser.setInitialFileName(settings.getName() + EQS_EXTENSION);
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Equalizer Settings Files", "*" + EQS_EXTENSION)
        );

        Stage stage = (Stage) equalizerPane.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            String filePath = file.getAbsolutePath();
            if (!filePath.endsWith(EQS_EXTENSION)) {
                file = new File(filePath + EQS_EXTENSION);
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                SerializableEqualizerSettings serializableSettings = new SerializableEqualizerSettings(
                        settings.getName(), settings.getBandValues()
                );
                oos.writeObject(serializableSettings);
                showInfo("Equalizer settings saved successfully to: " + file.getName());
            } catch (IOException e) {
                showAlert("Failed to save equalizer settings: " + e.getMessage());
            }
        }
    }

    @FXML
    public void handleLoadSettings() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Equalizer Settings");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Equalizer Settings Files", "*" + EQS_EXTENSION)
        );

        Stage stage = (Stage) equalizerPane.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                SerializableEqualizerSettings loadedSettings = (SerializableEqualizerSettings) ois.readObject();
                EqualizerSettings settings = new EqualizerSettings(
                        loadedSettings.name, loadedSettings.bandValues, false
                );

                // Check if preset with same name already exists
                boolean exists = presets.stream()
                        .anyMatch(p -> p.getName().equals(settings.getName()));

                if (!exists) {
                    customPresets.add(settings);
                    presets.add(settings);
                    if (saveCustomPresetsCallback != null) {
                        saveCustomPresetsCallback.accept(new ArrayList<>(customPresets));
                    }
                }

                // Select and apply the loaded preset
                presetComboBox.getSelectionModel().select(settings);
                applyPreset(settings);
                showInfo("Equalizer settings loaded successfully: " + settings.getName());
            } catch (IOException | ClassNotFoundException e) {
                showAlert("Failed to load equalizer settings: " + e.getMessage());
            }
        }
    }

    @FXML
    public void handleDeleteCustomPreset() {
        EqualizerSettings selected = presetComboBox.getSelectionModel().getSelectedItem();
        if (selected != null && !selected.isPreset()) {
            customPresets.remove(selected);
            presets.remove(selected);
            presetComboBox.getSelectionModel().selectFirst();
            if (saveCustomPresetsCallback != null) {
                saveCustomPresetsCallback.accept(new ArrayList<>(customPresets));
            }
        }
    }

    @FXML
    public void handleReset() {
        presetComboBox.getSelectionModel().selectFirst();
    }

    public void showEqualizer() {
        Stage stage = (Stage) equalizerPane.getScene().getWindow();
        stage.show();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static class SerializableEqualizerSettings implements Serializable {
        private static final long serialVersionUID = 1L;
        final String name;
        final double[] bandValues;

        SerializableEqualizerSettings(String name, double[] bandValues) {
            this.name = name;
            this.bandValues = bandValues;
        }
    }
}