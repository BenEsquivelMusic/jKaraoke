package karaoke.media;

public sealed interface ImageViewer permits CdgImageViewer {

    void setColor(byte r, byte g, byte b, int index);

    void setPixel(int x, int y, int color, boolean xor);

    void applyColor();

    void clearBorder(int col);

    void clearScreen(int col);

    void draw();

    /**
     * Scroll the image horizontally and/or vertically.
     * Based on CD+G specification from jbum.com/cdg_revealed.html
     *
     * @param hScroll horizontal scroll - bits 5-4: command (0=none, 1=right 6px, 2=left 6px), bits 2-0: offset (0-5)
     * @param vScroll vertical scroll - bits 5-4: command (0=none, 1=down 12px, 2=up 12px), bits 3-0: offset (0-11)
     * @param color fill color for scroll preset mode
     * @param copy if true, perform scroll copy (wrap around); if false, perform scroll preset (fill with color)
     */
    void scroll(int hScroll, int vScroll, int color, boolean copy);

    /**
     * Set a palette index as transparent.
     * Based on CD+G specification from jbum.com/cdg_revealed.html
     *
     * @param colorIndex the palette index (0-15) to treat as transparent
     */
    void setTransparentColor(int colorIndex);

}
