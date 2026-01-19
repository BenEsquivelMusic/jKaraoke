package karaoke.media;

public sealed interface ImageViewer permits CdgImageViewer {

    void setColor(byte r, byte g, byte b, int index);

    void setPixel(int x, int y, int color, boolean xor);

    void applyColor();

    void clearBorder(int col);

    void clearScreen(int col);

    void draw();

    void scroll(int hScroll, int vScroll, int color, boolean copy);
    
    void setTransparentColor(int colorIndex);

}
