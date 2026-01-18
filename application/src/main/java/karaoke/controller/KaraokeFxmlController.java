package karaoke.controller;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import karaoke.*;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public final class KaraokeFxmlController implements Initializable {

    private static final String ARTIST = "artist";
    private static final String ALBUM_ARTIST = "album artist";
    private static final String TITLE = "title";
    private static final String UNKNOWN = "UNKNOWN";
    private static final String ADD_SONG_FXML_CONTROLLER = "/AddSongFxmlController.fxml";
    private static final String SINGER_LINEUP_FXML_CONTROLLER = "/SingerLineupFxmlController.fxml";
    private static final String KARAOKE_MEDIA_VIEW_FXML_CONTROLLER = "/MediaViewFxmlController.fxml";
    private static final String EQ_FXML_CONTROLLER = "/EqualizerFxmlController.fxml";
    private static final String EVENT_CONTROLLER = "/EventFxmlController.fxml";

    private static final Pattern SPACE_PATTERN = Pattern.compile(" ");

    private final ObservableList<IndexedSinger> indexedSingers;
    private final NumberFormat numberFormat;

    private SingerLineupFxmlController singerLineupFxmlController;
    private MediaViewFxmlController mediaViewFxmlController;
    private EqualizerFxmlController equalizerFxmlController;

    private IndexedSinger activeSinger;
    private MediaPlayer mediaPlayer;
    private InvalidationListener seekTrackListener;
    private InvalidationListener volumeControlListener;
    private String eventName;
    private EventManager eventManager;

    /* Menu Items */
    @FXML
    private MenuItem menuItemNew;

    @FXML
    private MenuItem menuItemOpen;

    @FXML
    private MenuItem menuItemClose;

    @FXML
    private MenuItem menuItemSave;

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

    @FXML
    private Button buttonChangeSinger;

    /* Labels */
    @FXML
    private Label labelSingerName;

    @FXML
    private Label labelQueue;

    @FXML
    private Label labelStatus;

    @FXML
    private Label labelTime;

    @FXML
    private Label labelSong;

    @FXML
    private Label labelTitle;

    @FXML
    private Label labelArtist;

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

    @FXML
    private BarChart<String, Double> barChartAudioSpectrum;

    @FXML
    private CategoryAxis categoryAxisPhase;

    @FXML
    private NumberAxis numberAxisBarMagnitude;

    @FXML
    private AnchorPane anchorPaneQueueButtons;

    public KaraokeFxmlController() {
        this.indexedSingers = FXCollections.observableArrayList();
        this.numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);
        numberFormat.setMinimumIntegerDigits(2);
    }

    public void closeMediaPlayer() {
        if (Objects.nonNull(mediaPlayer)) {
            if (mediaPlayerIsPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.dispose();
        }
        mediaViewFxmlController.closeMediaView();
        equalizerFxmlController.setMediaPlayer(null);
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
        this.singerLineupFxmlController = loadController(SINGER_LINEUP_FXML_CONTROLLER, ApplicationIcons.MONITOR_ICON, "Karaoke Singer Lineup", controller -> controller.setSingers(indexedSingers), false);
        this.mediaViewFxmlController = loadController(KARAOKE_MEDIA_VIEW_FXML_CONTROLLER, ApplicationIcons.MONITOR_ICON, "Karaoke Media View", null, false);
        this.equalizerFxmlController = loadController(EQ_FXML_CONTROLLER, ApplicationIcons.APPLICATION_ICON, "Equalizer", null, false);
        ObservableList<String> phaseCategories = FXCollections.observableList(new ArrayList<>(10));
        IntStream.rangeClosed(1, 10).forEach(phase -> phaseCategories.add(new AudioBand(phase).toString()));
        categoryAxisPhase.setCategories(phaseCategories);
        numberAxisBarMagnitude.setTickLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Number number) {
                return number.intValue() - 60 + " db";
            }

            @Override
            public Number fromString(String numberText) {
                String[] splitNumberText = SPACE_PATTERN.split(numberText, -1);
                if (splitNumberText.length != 2) {
                    return null;
                }
                return Double.valueOf(Integer.parseInt(splitNumberText[0]) + 60.0);
            }
        });
        disableMenuItemButtons(false);
        anchorPaneQueueButtons.setDisable(true);
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
        AddSongFxmlController addSongFxmlController = loadController(ADD_SONG_FXML_CONTROLLER, ApplicationIcons.APPLICATION_ICON, "Choose Singer and Song", null, true);
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
        barChartAudioSpectrum.getData().clear();
        actionEvent.consume();
    }

    public void handlePause(ActionEvent actionEvent) {
        if (mediaPlayerIsPlaying()) {
            mediaPlayer.pause();
        }
        actionEvent.consume();
    }

    public void handleChangeSong(ActionEvent actionEvent) {
        String singerName = activeSinger.getSingerName();
        AddSongFxmlController addSongFxmlController = loadController(
                ADD_SONG_FXML_CONTROLLER,
                ApplicationIcons.APPLICATION_ICON,
                "Choose Song",
                controller -> controller.setUnEditableSingerName(singerName),
                true);
        if (addSongFxmlController.isFormCompleted()) {
            closeMediaPlayer();
            this.activeSinger = addSongFxmlController.getSinger();
            setMediaPlayer(false);
        }
        actionEvent.consume();
    }

    public void handleChangeSinger(ActionEvent actionEvent) {
        String songFile = activeSinger.getSongFile();
        AddSongFxmlController addSingerFxmlController = loadController(
                ADD_SONG_FXML_CONTROLLER,
                ApplicationIcons.APPLICATION_ICON,
                "Choose Singer",
                controller -> controller.setUnEditableSong(songFile),
                true);
        if (addSingerFxmlController.isFormCompleted()) {
            this.activeSinger = addSingerFxmlController.getSinger();
            labelSingerName.setText(activeSinger.getSingerName());
        }
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
        setMediaPlayer(true);
        actionEvent.consume();
    }

    public void handleShowVideoScreen(ActionEvent actionEvent) {
        mediaViewFxmlController.showMediaView();
        actionEvent.consume();
    }

    public void handleShowSingerLineup(ActionEvent actionEvent) {
        singerLineupFxmlController.showSingerLineup();
        actionEvent.consume();
    }

    private void setMediaPlayer(boolean updateSingerIndex) {
        //Set up the media player
        this.mediaPlayer = new MediaPlayer(activeSinger.getMedia());
        mediaPlayer.totalDurationProperty().addListener((_, _, newValue) -> {
            double totalSecs = newValue.toSeconds();
            double hours = totalSecs / 3600;
            double minutes = (totalSecs % 3600) / 60;
            double seconds = totalSecs % 60;
            labelTime.setText(numberFormat.format(hours) + ':' + numberFormat.format(minutes) + ':' + numberFormat.format(seconds));
        });
        labelSong.setText(activeSinger.getSongFile());
        mediaPlayer.setAudioSpectrumNumBands(10);
        mediaViewFxmlController.updateMediaPlayerForMediaView(mediaPlayer);
        sliderTrack.setDisable(false);
        mediaPlayer.currentTimeProperty().addListener((_, _, newValue) -> {
            double currentTime = newValue.toMillis();
            if (currentTime == 0.0) {
                sliderTrack.setValue(0.0);
            } else {
                double totalTime = mediaPlayer.getTotalDuration().toMillis();
                sliderTrack.setValue(Math.min((currentTime * 100.0) / totalTime, 100.0));
            }
        });
        mediaPlayer.setAudioSpectrumListener((_, _, magnitudes, phases) -> {
            ObservableList<XYChart.Data<String, Double>> chartData = FXCollections.observableArrayList();
            double volume = mediaPlayer.getVolume();
            for (int bandIndex = 0; bandIndex < phases.length; bandIndex++) {
                float magnitude = magnitudes[bandIndex];
                float phase = phases[bandIndex];
                chartData.add(new XYChart.Data<>(
                        new AudioBand(bandIndex + 1).toString(),
                        Double.valueOf(Math.max(0.0, magnitude + 60.0) * volume),
                        Float.valueOf(phase)));
            }
            barChartAudioSpectrum.setData(FXCollections.observableArrayList(new XYChart.Series<>("Audio Spectrum", chartData)));
        });
        mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.stop());

        //Synchronize track slider
        if (Objects.nonNull(seekTrackListener)) {
            sliderTrack.valueProperty().removeListener(seekTrackListener);
        }
        this.seekTrackListener = _ -> {
            if (sliderTrack.isValueChanging()) {
                Duration totalTime = mediaPlayer.getTotalDuration();
                if (!Duration.UNKNOWN.equals(totalTime) && !Duration.INDEFINITE.equals(totalTime)) {
                    double seekValue = getSeekValue();
                    Duration seekTime = totalTime.multiply(seekValue);
                    mediaPlayer.seek(seekTime);
                    mediaViewFxmlController.seek(seekValue);
                }
            }
        };
        sliderTrack.valueProperty().addListener(seekTrackListener);

        //Synchronize volume slider
        if (Objects.nonNull(volumeControlListener)) {
            sliderVolume.valueProperty().removeListener(volumeControlListener);
        }
        this.volumeControlListener = ignored -> mediaPlayer.setVolume(getVolume());
        sliderVolume.valueProperty().addListener(volumeControlListener);

        //Synchronize active track labels
        mediaPlayer.statusProperty().addListener((_, _, newValue) -> labelStatus.setText(newValue.toString()));
        labelArtist.setText(mediaPlayer.getMedia().getMetadata().getOrDefault(ARTIST, mediaPlayer.getMedia().getMetadata().getOrDefault(ALBUM_ARTIST, UNKNOWN)).toString());
        labelTitle.setText(mediaPlayer.getMedia().getMetadata().getOrDefault(TITLE, UNKNOWN).toString());
        mediaPlayer.getMedia().getMetadata().addListener((MapChangeListener<String, Object>) change -> {
            if (change.wasAdded()) {
                String key = change.getKey();
                switch (key) {
                    case ARTIST, ALBUM_ARTIST:
                        labelArtist.setText(change.getValueAdded().toString());
                        break;
                    case TITLE:
                        labelTitle.setText(change.getValueAdded().toString());
                        break;
                    default:
                        break;
                }
            }
        });

        //Synchronize EQ Controller with new Media Player
        equalizerFxmlController.setMediaPlayer(mediaPlayer);

        //Finalize application controls and display
        if (updateSingerIndex) {
            updateSingerIndex();
        }
        labelSingerName.setText(activeSinger.getSingerName());
        labelSingerName.setVisible(true);
        buttonPlay.setDisable(false);
        buttonStop.setDisable(false);
        buttonPause.setDisable(false);
        buttonCompleteSinger.setDisable(false);
        buttonReQueueSinger.setDisable(false);
        buttonChangeSong.setDisable(false);
        buttonChangeSinger.setDisable(false);
    }

    private void disableMenuItemButtons(boolean disableNewAndOpenItems) {
        menuItemNew.setDisable(disableNewAndOpenItems);
        menuItemOpen.setDisable(disableNewAndOpenItems);

        boolean disableSaveAndCloseButtons = !disableNewAndOpenItems;
        menuItemSave.setDisable(disableSaveAndCloseButtons);
        menuItemClose.setDisable(disableSaveAndCloseButtons);
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
        buttonChangeSinger.setDisable(true);
        buttonNextSinger.setDisable(indexedSingers.isEmpty());
        labelTime.setText("");
        labelSong.setText("");
        labelStatus.setText("");
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
        slider.valueProperty().addListener((_, _, newValue) -> {
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
                                 String iconResource,
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
            stage.getIcons().add(new Image(Optional.ofNullable(Main.class.getResourceAsStream(iconResource)).orElseThrow()));
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

    public void handleNewEvent(ActionEvent actionEvent) {
        EventFxmlController eventController = loadController(
                EVENT_CONTROLLER,
                ApplicationIcons.APPLICATION_ICON,
                "Create a new event",
                controller -> controller.setIsCreateEvent(true),
                true);
        if (eventController.isFormCompleted()) {
            disableMenuItemButtons(true);
            anchorPaneQueueButtons.setDisable(false);
            this.eventName = eventController.getEventName();
            this.eventManager = eventController.getEventManager();
        }

        actionEvent.consume();
    }

    public void handleOpenEvent(ActionEvent actionEvent) {
        EventFxmlController eventController = loadController(
                EVENT_CONTROLLER,
                ApplicationIcons.APPLICATION_ICON,
                "Open an existing event",
                controller -> controller.setUnEditableEventName(""),
                true);
        if (eventController.isFormCompleted()) {
            disableMenuItemButtons(true);
            anchorPaneQueueButtons.setDisable(false);
            this.eventManager = eventController.getEventManager();
            Event event = eventManager.readEvent();
            this.eventName = event.name();
            for (Singer singer : event.singers()) {
                indexedSingers.add(new IndexedSinger(1, singer.singerName(), singer.songFile()));
            }
            updateSingerIndex();
        }

        actionEvent.consume();
    }

    public void handleCloseMenu(ActionEvent actionEvent) {
        disableMenuItemButtons(false);

        resetMediaView();
        indexedSingers.removeIf(_ -> true);
        updateSingerIndex();
        this.eventName = null;
        this.eventManager = null;
        anchorPaneQueueButtons.setDisable(true);
        actionEvent.consume();
    }

    public void handleSaveEvent(ActionEvent actionEvent) {
        int singerCount = indexedSingers.size();
        Singer[] singers = new Singer[singerCount];
        IntStream.range(0, singerCount).forEach(singerIndex -> singers[singerIndex] = indexedSingers.get(singerIndex).getSinger());
        eventManager.writeEvent(new Event(eventName, singers));
        actionEvent.consume();
    }

    public void handleExitApplication(ActionEvent actionEvent) {
        //TODO
        actionEvent.consume();
    }

    public void handleSettings(ActionEvent actionEvent) {
        //TODO
        actionEvent.consume();
    }

    public void handleShowEqWindow(ActionEvent actionEvent) {
        equalizerFxmlController.showEqualizer();
        actionEvent.consume();
    }
}
