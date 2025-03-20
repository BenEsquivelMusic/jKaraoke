package com.bem.image;

public interface ImageReader {

    void initialize();

    void play();

    void pause();

    void stop();

    void seek(double percent);

    void reset();

}
