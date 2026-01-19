package karaoke.media;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

/* See https://jbum.com/cdg_revealed.html for detailed specifications on CD+G format */
public final class CdgImageViewer implements ImageViewer {

    private static final int CDG_WIDTH = 300;
    private static final int CDG_HEIGHT = 216;
    private static final int NUM_PIXELS = CDG_WIDTH * CDG_HEIGHT;

    private final Canvas backgroundCanvas;
    private final ImageView imageView;
    private final RgbColor rgbColor;
    private final MemorySegment pixelIndices;
    private final WritableImage writableImage;

    // Transparent color index (-1 means no transparency)
    private int transparentColorIndex = -1;

    public CdgImageViewer(Canvas backgroundCanvas, ImageView imageView) {
        this.backgroundCanvas = backgroundCanvas;
        this.imageView = imageView;
        this.rgbColor = new RgbColor();
        this.pixelIndices = Arena.ofAuto().allocate(ValueLayout.JAVA_INT, NUM_PIXELS);
        this.writableImage = new WritableImage(CDG_WIDTH, CDG_HEIGHT);
        draw();
    }

    @Override
    public void setColor(byte r, byte g, byte b, int index) {
        rgbColor.set(r, g, b, index);
    }

    @Override
    public void setPixel(int x, int y, int color, boolean xor) {
        int index = y * CDG_WIDTH + x;
        if (xor) {
            color ^= pixelIndices.getAtIndex(ValueLayout.JAVA_INT, index);
        }
        pixelIndices.setAtIndex(ValueLayout.JAVA_INT, index, color);
    }

    @Override
    public void applyColor() {
        draw();
    }

    @Override
    public void clearBorder(int col) {
        Platform.runLater(() -> backgroundCanvas.getGraphicsContext2D().setFill(rgbColor.createColor(col)));
        int pos = 0;
        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < CDG_WIDTH; x++) {
                pixelIndices.setAtIndex(ValueLayout.JAVA_INT, pos++, col);
            }
        }
        for (int y = 6; y < 210; y++) {
            for (int x = 0; x < 3; x++) {
                pixelIndices.setAtIndex(ValueLayout.JAVA_INT, pos++, col);
            }
            pos += 294;
            for (int x = 297; x < CDG_WIDTH; x++) {
                pixelIndices.setAtIndex(ValueLayout.JAVA_INT, pos++, col);
            }
        }
        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < CDG_WIDTH; x++) {
                pixelIndices.setAtIndex(ValueLayout.JAVA_INT, pos++, col);
            }
        }
    }

    @Override
    public void clearScreen(int col) {
        for (int y = 6; y < 210; y++) {
            for (int x = 3; x < 297; x++) {
                pixelIndices.setAtIndex(ValueLayout.JAVA_INT, y * CDG_WIDTH + x, col);
            }
        }
    }

    @Override
    public void scroll(int hScroll, int vScroll, int color, boolean copy) {
        // Decode horizontal scroll command
        // bits 5-4: command (0=none, 1=right 6px, 2=left 6px)
        // Note: bits 2-0 contain offset (0-5 pixels) per CD+G spec, but fine-grained
        // offset positioning is not implemented in this viewer
        int hCmd = (hScroll >> 4) & 0x03;

        // Decode vertical scroll command
        // bits 5-4: command (0=none, 1=down 12px, 2=up 12px)
        // Note: bits 3-0 contain offset (0-11 pixels) per CD+G spec, but fine-grained
        // offset positioning is not implemented in this viewer
        int vCmd = (vScroll >> 4) & 0x03;

        // Calculate actual pixel shifts based on command
        int hShift = 0;
        if (hCmd == 1) {
            hShift = 6;  // Scroll right by 6 pixels
        } else if (hCmd == 2) {
            hShift = -6; // Scroll left by 6 pixels
        }

        int vShift = 0;
        if (vCmd == 1) {
            vShift = 12;  // Scroll down by 12 pixels
        } else if (vCmd == 2) {
            vShift = -12; // Scroll up by 12 pixels
        }

        // If no scroll command, just return (offset only affects display position)
        if (hShift == 0 && vShift == 0) {
            return;
        }

        // Create a temporary buffer for the scrolled pixels
        int[] tempBuffer = new int[NUM_PIXELS];

        for (int y = 0; y < CDG_HEIGHT; y++) {
            for (int x = 0; x < CDG_WIDTH; x++) {
                int srcX = x - hShift;
                int srcY = y - vShift;

                int pixelValue;
                if (copy) {
                    // Scroll Copy: wrap around
                    srcX = ((srcX % CDG_WIDTH) + CDG_WIDTH) % CDG_WIDTH;
                    srcY = ((srcY % CDG_HEIGHT) + CDG_HEIGHT) % CDG_HEIGHT;
                    pixelValue = pixelIndices.getAtIndex(ValueLayout.JAVA_INT, srcY * CDG_WIDTH + srcX);
                } else {
                    // Scroll Preset: fill with color if out of bounds
                    if (srcX < 0 || srcX >= CDG_WIDTH || srcY < 0 || srcY >= CDG_HEIGHT) {
                        pixelValue = color;
                    } else {
                        pixelValue = pixelIndices.getAtIndex(ValueLayout.JAVA_INT, srcY * CDG_WIDTH + srcX);
                    }
                }
                tempBuffer[y * CDG_WIDTH + x] = pixelValue;
            }
        }

        // Copy the temporary buffer back to pixelIndices
        for (int i = 0; i < NUM_PIXELS; i++) {
            pixelIndices.setAtIndex(ValueLayout.JAVA_INT, i, tempBuffer[i]);
        }
    }

    @Override
    public void setTransparentColor(int colorIndex) {
        this.transparentColorIndex = colorIndex;
    }

    @Override
    public void draw() {
        int[] argbPixels = new int[NUM_PIXELS];
        for (int i = 0; i < NUM_PIXELS; i++) {
            int colorIndex = pixelIndices.getAtIndex(ValueLayout.JAVA_INT, i);
            if (colorIndex == transparentColorIndex) {
                // Make this pixel fully transparent (alpha = 0)
                argbPixels[i] = rgbColor.getArgb(colorIndex) & 0x00FFFFFF;
            } else {
                argbPixels[i] = rgbColor.getArgb(colorIndex);
            }
        }
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        pixelWriter.setPixels(0, 0, CDG_WIDTH, CDG_HEIGHT, PixelFormat.getIntArgbInstance(), argbPixels, 0, CDG_WIDTH);
        imageView.setImage(writableImage);
    }

}