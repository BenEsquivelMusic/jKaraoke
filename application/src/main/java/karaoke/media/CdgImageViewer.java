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


public final class CdgImageViewer implements ImageViewer {

    private static final int CDG_WIDTH = 300;
    private static final int CDG_HEIGHT = 216;
    private static final int NUM_PIXELS = CDG_WIDTH * CDG_HEIGHT;

    private final Canvas backgroundCanvas;
    private final ImageView imageView;
    private final RgbColor rgbColor;
    private final MemorySegment pixelIndices;
    private final WritableImage writableImage;

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
    public void draw() {
        int[] argbPixels = new int[NUM_PIXELS];
        for (int i = 0; i < NUM_PIXELS; i++) {
            argbPixels[i] = rgbColor.getArgb(pixelIndices.getAtIndex(ValueLayout.JAVA_INT, i));
        }
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        pixelWriter.setPixels(0, 0, CDG_WIDTH, CDG_HEIGHT, PixelFormat.getIntArgbInstance(), argbPixels, 0, CDG_WIDTH);
        imageView.setImage(writableImage);
    }

}
