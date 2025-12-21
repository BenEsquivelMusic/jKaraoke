package karaoke.media;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.*;
import javafx.scene.image.Image;

import java.awt.*;
import java.awt.image.*;


public final class CdgImageViewer implements ImageViewer {

    private static final int CDG_WIDTH = 300;
    private static final int CDG_HEIGHT = 216;
    private static final int NUM_PIXELS = CDG_WIDTH * CDG_HEIGHT;

    private final Canvas backgroundCanvas;
    private final ImageView imageView;
    private final RgbColor rgbColor;
    private final MediaImage image;

    public CdgImageViewer(Canvas backgroundCanvas, ImageView imageView) {
        this.backgroundCanvas = backgroundCanvas;
        this.imageView = imageView;
        this.rgbColor = new RgbColor();

        DataBuffer dataBuffer = new DataBufferByte(new byte[NUM_PIXELS], NUM_PIXELS, 0);
        int[] bitMasks = new int[]{(byte) 0xf};
        SampleModel sampleModel = new SinglePixelPackedSampleModel(DataBuffer.TYPE_BYTE, CDG_WIDTH, CDG_HEIGHT, bitMasks);
        WritableRaster raster = Raster.createWritableRaster(sampleModel, dataBuffer, null);
        this.image = new MediaImage(new BufferedImage(rgbColor.createColorModel(), raster, false, null));
        draw();
    }

    @Override
    public void setColor(byte r, byte g, byte b, int index) {
        rgbColor.set(r, g, b, index);
    }

    @Override
    public void setPixel(int x, int y, int color, boolean xor) {
        if (xor) {
            color ^= image.getElement(y * 300 + x);
        }
        image.setElement(y * 300 + x, color);
    }

    @Override
    public void applyColor() {
        image.setImage(new BufferedImage(rgbColor.createColorModel(), image.getImage().getRaster(), false, null));
        draw();
    }

    @Override
    public void clearBorder(int col) {
        Platform.runLater(() -> backgroundCanvas.getGraphicsContext2D().setFill(rgbColor.createColor(col)));
        int pos = 0;
        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 300; x++) {
                image.setElement(pos++, col);
            }
        }
        for (int y = 6; y < 210; y++) {
            for (int x = 0; x < 3; x++) {
                image.setElement(pos++, col);
            }
            pos += 294;
            for (int x = 297; x < 300; x++) {
                image.setElement(pos++, col);
            }
        }
        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 300; x++) {
                image.setElement(pos++, col);
            }
        }
    }

    @Override
    public void clearScreen(int col) {
        for (int y = 6; y < 210; y++) {
            for (int x = 3; x < 297; x++) {
                image.setElement(y * 300 + x, col);
            }
        }
    }

    @Override
    public void draw() {
        Image fxImage = getWritableImage(image.getImage());
        imageView.setImage(fxImage);
    }

    private WritableImage getWritableImage(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        switch (bufferedImage.getType()) {
            case 2:
            case 3:
                break;
            default:
                BufferedImage tempBufferedImage = new BufferedImage(width, height, 3);
                Graphics2D graphics = tempBufferedImage.createGraphics();
                graphics.drawImage(bufferedImage, 0, 0, null);
                graphics.dispose();
                bufferedImage = tempBufferedImage;
        }

        WritableImage writableImage = new WritableImage(width, height);

        PixelWriter pixelWriter = writableImage.getPixelWriter();
        DataBuffer pixelBuffer = bufferedImage.getRaster().getDataBuffer();
        int scanLine = getScanLine(bufferedImage);
        pixelWriter.setPixels(
                0,
                0,
                width,
                height,
                PixelFormat.getIntArgbInstance(),
                getPixels(pixelBuffer),
                pixelBuffer.getOffset(),
                scanLine);
        return writableImage;
    }

    private int getScanLine(BufferedImage bufferedImage) {
        int scanLine = 0;
        SampleModel sampleModel = bufferedImage.getRaster().getSampleModel();
        if (sampleModel instanceof SinglePixelPackedSampleModel singlePixelPackedSampleModel) {
            scanLine = singlePixelPackedSampleModel.getScanlineStride();
        }
        return scanLine;
    }

    private int[] getPixels(DataBuffer buffer) {
        DataBufferInt pixelBuffer = (DataBufferInt) buffer;
        return pixelBuffer.getData();
    }

}
