package karaoke.media;

public sealed interface ImageReader permits CdgImageReader {

    void initialize();

    void play();

    void pause();

    void stop();

    void seek(double percent);

    void reset();

}
