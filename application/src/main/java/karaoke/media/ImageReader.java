package karaoke.media;

import java.io.Closeable;

public sealed interface ImageReader extends Closeable permits CdgImageReader {

    void initialize();

    void play();

    void pause();

    void close();

    void seek(double percent);

    void reset();

}
