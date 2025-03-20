package com.bem;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public final class KaraokeFxmlController implements Initializable {

    private final ObservableList<IndexedSinger> indexedSingers;
    private SingerLineupFxmlController singerLineupFxmlController;
    private KaraokeMediaViewFxmlController karaokeMediaViewFxmlController;

    private IndexedSinger activeSinger;
    private MediaPlayer mediaPlayer;
    private InvalidationListener seekTrackListener;

    /* Buttons */
    @FXML
    private Button buttonPlay;

    @FXML
    private Button buttonStop;

    @FXML
    private Button buttonPause;

    @FXML
    private Button buttonNextSinger;

    @FXML
    private Button buttonCompleteSinger;

    @FXML
    private Button buttonReQueueSinger;

    @FXML
    private Button buttonChangeSong;

    /* Labels */
    @FXML
    private Label labelSingerName;

    @FXML
    private Label labelQueue;

    /* Sliders */
    @FXML
    private Slider sliderVolume;

    @FXML
    private Slider sliderTrack;

    /* Tables and Columns */
    @FXML
    private TableView<IndexedSinger> tableViewSingerQueue;

    @FXML
    private TableColumn<IndexedSinger, String> columnSinger;

    @FXML
    private TableColumn<IndexedSinger, String> columnSong;

    public KaraokeFxmlController() {
        this.indexedSingers = FXCollections.observableArrayList();
    }

    public void closeMediaPlayer() {
        if (Objects.nonNull(mediaPlayer)) {
            if (mediaPlayerIsPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.dispose();
        }
        karaokeMediaViewFxmlController.closeMediaView();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        columnSinger.setCellValueFactory(new PropertyValueFactory<>("SingerName"));
        columnSong.setCellValueFactory(new PropertyValueFactory<>("SongFile"));
        styleSlider(sliderVolume);
        styleSlider(sliderTrack);
        sliderVolume.setValue(0.0);
        sliderVolume.setValue(50.0);
        sliderTrack.setValue(0.0);
        tableViewSingerQueue.setItems(indexedSingers);
        this.singerLineupFxmlController = loadController("/fxml/SingerLineupFxmlController.fxml", "Karaoke Singer Lineup", controller -> controller.setSingers(indexedSingers), false);
        this.karaokeMediaViewFxmlController = loadController("/fxml/KaraokeMediaViewFxmlController.fxml", "Karaoke Media View", null, false);
    }

    public void handleMoveSingerForward(ActionEvent actionEvent) {
        TreeSet<Integer> selectedRows = getSelectedRows();
        if (!selectedRows.isEmpty()) {
            int numSingers = indexedSingers.size();
            selectedRows.reversed().forEach(row -> {
                int boxedRow = row.intValue();
                if (boxedRow < numSingers - 1) {
                    IndexedSinger singer = indexedSingers.remove(boxedRow);
                    indexedSingers.add(boxedRow + 1, singer);
                }
            });
            updateSingerIndex();
        }
        actionEvent.consume();
    }

    public void handleAddSinger(ActionEvent actionEvent) {
        AddSongFxmlController addSongFxmlController = loadController("/fxml/AddSongFxmlController.fxml", "Choose Singer and Song", null, true);
        if (addSongFxmlController.isFormCompleted()) {
            IndexedSinger singer = addSongFxmlController.getSinger();
            indexedSingers.add(singer);
            updateSingerIndex();
            if (buttonNextSinger.isDisable() && !indexedSingers.isEmpty() && Objects.isNull(activeSinger)) {
                buttonNextSinger.setDisable(false);
            }
        }
        actionEvent.consume();
    }

    public void handleDeleteSinger(ActionEvent actionEvent) {
        TreeSet<Integer> selectedRows = getSelectedRows();
        if (!selectedRows.isEmpty()) {
            selectedRows.reversed().forEach(row -> indexedSingers.remove(row.intValue()));
            updateSingerIndex();
            if (indexedSingers.isEmpty()) {
                buttonNextSinger.setDisable(true);
            }
        }
        actionEvent.consume();
    }

    public void handleMoveSingerBack(ActionEvent actionEvent) {
        TreeSet<Integer> selectedRows = getSelectedRows();
        if (!selectedRows.isEmpty()) {
            selectedRows.forEach(row -> {
                int boxedRow = row.intValue();
                if (boxedRow > 0) {
                    IndexedSinger singer = indexedSingers.remove(boxedRow);
                    indexedSingers.add(boxedRow - 1, singer);
                }
            });
            updateSingerIndex();
        }
        actionEvent.consume();
    }

    public void handlePlay(ActionEvent actionEvent) {
        if (!mediaPlayerIsPlaying()) {
            mediaPlayer.play();
        }
        actionEvent.consume();
    }

    public void handleStop(ActionEvent actionEvent) {
        if (mediaPlayerIsPlaying()) {
            mediaPlayer.stop();
        }
        actionEvent.consume();
    }

    public void handlePause(ActionEvent actionEvent) {
        if (mediaPlayerIsPlaying()) {
            mediaPlayer.pause();
        }
        actionEvent.consume();
    }

    public void handleChangeSong(ActionEvent actionEvent) {
        //TODO
        actionEvent.consume();
    }

    public void handleReQueueSinger(ActionEvent actionEvent) {
        indexedSingers.add(activeSinger.copy());
        resetMediaView();
        updateSingerIndex();
        actionEvent.consume();
    }

    public void handleCompleteSinger(ActionEvent actionEvent) {
        resetMediaView();
        actionEvent.consume();
    }

    public void handleNextSinger(ActionEvent actionEvent) {
        buttonNextSinger.setDisable(true);
        this.activeSinger = indexedSingers.removeFirst();
        this.mediaPlayer = new MediaPlayer(activeSinger.getMedia());
        karaokeMediaViewFxmlController.updateMediaPlayerForMediaView(mediaPlayer);
        sliderTrack.setDisable(false);
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            double currentTime = newValue.toMillis();
            if (currentTime == 0.0) {
                sliderTrack.setValue(0.0);
            } else {
                double totalTime = mediaPlayer.getTotalDuration().toMillis();
                sliderTrack.setValue(Math.min((currentTime * 100.0) / totalTime, 100.0));
            }
        });
        if (Objects.nonNull(seekTrackListener)) {
            sliderTrack.valueProperty().removeListener(seekTrackListener);
        }
        this.seekTrackListener = observable -> {
            if (sliderTrack.isValueChanging()) {
                double seekValue = getSeekValue();
                Duration totalTime = mediaPlayer.getTotalDuration();
                Duration seekTime = totalTime.multiply(seekValue);
                mediaPlayer.seek(seekTime);
                karaokeMediaViewFxmlController.seek(seekValue);
            }
        };
        sliderTrack.valueProperty().addListener(seekTrackListener);
        sliderVolume.valueProperty().addListener(ignored -> mediaPlayer.setVolume(getVolume()));
        mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.stop());
        updateSingerIndex();
        labelSingerName.setText(activeSinger.getSingerName());
        labelSingerName.setVisible(true);
        buttonPlay.setDisable(false);
        buttonStop.setDisable(false);
        buttonPause.setDisable(false);
        buttonCompleteSinger.setDisable(false);
        buttonReQueueSinger.setDisable(false);
        buttonChangeSong.setDisable(false);
        actionEvent.consume();
    }

    public void handleShowVideoScreen(ActionEvent actionEvent) {
        karaokeMediaViewFxmlController.showMediaView();
        actionEvent.consume();
    }

    public void handleShowSingerLineup(ActionEvent actionEvent) {
        singerLineupFxmlController.showSingerLineup();
        actionEvent.consume();
    }

    private void resetMediaView() {
        closeMediaPlayer();
        this.activeSinger = null;
        this.mediaPlayer = null;
        labelSingerName.setText("");
        labelSingerName.setVisible(false);
        sliderTrack.setValue(0.0);
        sliderTrack.setDisable(true);
        buttonPlay.setDisable(true);
        buttonStop.setDisable(true);
        buttonPause.setDisable(true);
        buttonCompleteSinger.setDisable(true);
        buttonReQueueSinger.setDisable(true);
        buttonChangeSong.setDisable(true);
        buttonNextSinger.setDisable(indexedSingers.isEmpty());
    }

    private TreeSet<Integer> getSelectedRows() {
        TreeSet<Integer> selectedRows = new TreeSet<>();
        tableViewSingerQueue.getSelectionModel()
                .getSelectedCells()
                .forEach(tablePosition -> selectedRows.add(Integer.valueOf(tablePosition.getRow())));
        return selectedRows;
    }

    private void updateSingerIndex() {
        IntStream.range(0, indexedSingers.size())
                .parallel()
                .forEach(singerIndex -> {
                    IndexedSinger singer = indexedSingers.get(singerIndex);
                    int singerPriority = singerIndex + 1;
                    if (singer.getIndex() != singerPriority) {
                        singer.setIndex(singerPriority);
                    }
                });
        labelQueue.setText("Queue: " + indexedSingers.size());
    }

    private void styleSlider(Slider slider) {
        slider.valueProperty().addListener((obs, oldValue, newValue) -> {
            double percentage = 100.0 * newValue.doubleValue() / slider.getMax();
            String style = String.format(
                    "-track-color: linear-gradient(to right, " +
                            "-fx-accent 0%%, " +
                            "-fx-accent %1$.1f%%, " +
                            "-default-track-color %1$.1f%%, " +
                            "-default-track-color 100%%);",
                    percentage);
            slider.setStyle(style);
        });
    }

    private <T> T loadController(String resource,
                                 String windowTitle,
                                 Consumer<T> customResourceLoadAction,
                                 boolean showAndWait) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
            Parent root = loader.load();
            T controller = loader.getController();
            if (Objects.nonNull(customResourceLoadAction)) {
                customResourceLoadAction.accept(controller);
            }
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle(windowTitle);
            stage.setResizable(true);
            if (showAndWait) {
                stage.showAndWait();
            }
            return controller;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private boolean mediaPlayerIsPlaying() {
        return MediaPlayer.Status.PLAYING.equals(mediaPlayer.getStatus());
    }

    private double getSeekValue() {
        return getSliderValueInDecimalForm(sliderTrack);
    }

    private double getVolume() {
        return getSliderValueInDecimalForm(sliderVolume);
    }

    private double getSliderValueInDecimalForm(Slider slider) {
        double max = slider.getMax();
        double value = slider.getValue();
        if (value >= max) {
            return 1.0;
        }
        if (value > 0.0) {
            return value / max;
        }
        return 0.0;
    }

}
