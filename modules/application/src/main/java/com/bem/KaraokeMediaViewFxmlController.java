package com.bem;

import com.bem.image.CdgImageReader;
import com.bem.image.CdgImageViewer;
import com.bem.image.ImageReader;
import com.bem.image.ImageViewer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public final class KaraokeMediaViewFxmlController implements Initializable {

    @FXML
    private Pane mediaViewPane;

    @FXML
    private Canvas mediaViewCanvas;

    @FXML
    private MediaView mediaView;

    @FXML
    private CdgMediaView cdgMediaView;

    private ImageViewer imageViewer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Objects.requireNonNull(mediaViewPane, "Media view pane was not initialized");
        Objects.requireNonNull(mediaViewCanvas, "Media view canvas was not initialized");
        Objects.requireNonNull(mediaView, "Media view was not initialized");
        Objects.requireNonNull(cdgMediaView, "CDG Media view was not initialized");
        this.imageViewer = new CdgImageViewer(mediaViewCanvas, cdgMediaView);
    }

    public void updateMediaPlayerForMediaView(MediaPlayer mediaPlayer) {
        getOptionalImageReader(mediaPlayer).ifPresentOrElse(imageReader -> {
                    mediaPlayer.setOnPlaying(imageReader::play);
                    mediaPlayer.setOnEndOfMedia(imageReader::pause);
                    mediaPlayer.setOnError(imageReader::stop);
                    mediaPlayer.setOnPaused(imageReader::pause);
                    mediaPlayer.setOnStalled(imageReader::pause);
                    mediaPlayer.setOnStopped(() -> {
                        imageReader.pause();
                        imageReader.reset();
                    });
                    mediaPlayer.setOnRepeat(imageReader::reset);
                    mediaView.setMediaPlayer(null);
                    mediaView.setVisible(false);
                    mediaViewCanvas.setVisible(true);
                    cdgMediaView.setImageReader(imageReader);
                    cdgMediaView.setVisible(true);
                    imageReader.initialize();
                },
                () -> {
                    mediaViewCanvas.setVisible(false);
                    cdgMediaView.setVisible(false);
                    cdgMediaView.stop();
                    mediaView.setMediaPlayer(mediaPlayer);
                    mediaView.setVisible(true);
                });
    }

    public void showMediaView() {
        Stage stage = (Stage) mediaViewPane.getScene().getWindow();
        stage.show();
    }

    public void closeMediaView() {
        cdgMediaView.stop();
    }

    public void seek(double percent) {
        cdgMediaView.seek(percent);
    }

    private Optional<ImageReader> getOptionalImageReader(MediaPlayer mediaPlayer) {
        return Optional.ofNullable(getImageReader(mediaPlayer));
    }

    private ImageReader getImageReader(MediaPlayer mediaPlayer) {
        try {
            URI mediaURI = URI.create(mediaPlayer.getMedia().getSource());
            File mediaFile = new File(mediaURI);
            if (mediaFile.isFile()) {
                return getImageReader(mediaFile);
            }
        } catch (IllegalArgumentException ignored) {
        }
        return null;
    }

    private ImageReader getImageReader(File mediaFile) {
        String mediaFileName = mediaFile.getName();
        String mediaFileNameWithoutExtension = getFileNameWithoutExtension(mediaFileName);
        File[] companionCdgFiles = mediaFile.getParentFile().listFiles(pathname -> {
            String foundFileName = pathname.getName();
            return pathname.isFile()
                    && mediaFileNameWithoutExtension.equals(getFileNameWithoutExtension(foundFileName))
                    && foundFileName.endsWith(".cdg");
        });
        if (Objects.nonNull(companionCdgFiles) && companionCdgFiles.length == 1) {
            return new CdgImageReader(imageViewer, companionCdgFiles[0]);
        }
        return null;
    }

    private String getFileNameWithoutExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex > 0) {
            return fileName.substring(0, dotIndex);
        } else {
            return fileName;
        }
    }

}
