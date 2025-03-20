package com.bem;

import com.bem.image.ImageReader;
import javafx.scene.image.ImageView;

import java.util.Objects;

public final class CdgMediaView extends ImageView {


    private ImageReader imageReader;

    public CdgMediaView() {
        super();
    }

    public void setImageReader(ImageReader imageReader) {
        stop();
        this.imageReader = imageReader;
    }

    public void seek(double percent) {
        if (Objects.nonNull(imageReader)) {
            imageReader.seek(percent);
        }
    }

    public void stop() {
        if (Objects.nonNull(imageReader)) {
            imageReader.stop();
            this.imageReader = null;
        }
    }


}
