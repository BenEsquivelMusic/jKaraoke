package com.bem.image;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.image.*;

public final class CdgImageViewer implements ImageViewer {

    private static final int CDG_WIDTH = 300;
    private static final int CDG_HEIGHT = 216;

    private final Canvas canvas;
    private final ImageView imageView;
    private final DataBuffer dbuf;
    private final RgbColor rgbColor;
    private BufferedImage image;// = new BufferedImage(300, 216, BufferedImage.TYPE_BYTE_INDEXED);

    public CdgImageViewer(Canvas canvas, ImageView imageView) {
        this.canvas = canvas;
        this.imageView = imageView;

        byte[] pixels = new byte[CDG_WIDTH * CDG_HEIGHT];

        // Create a data buffer using the byte buffer of pixel data.
        // The pixel data is not copied; the data buffer uses the byte buffer array.
        this.dbuf = new DataBufferByte(pixels, pixels.length, 0);
        this.rgbColor = new RgbColor();

        // Prepare a sample model that specifies a storage 4-bits of
        // pixel datavd in an 8-bit data element
        int[] bitMasks = new int[]{(byte) 0xf};
        SampleModel sampleModel = new SinglePixelPackedSampleModel(DataBuffer.TYPE_BYTE, CDG_WIDTH, CDG_HEIGHT, bitMasks);

        // Create a raster using the sample model and data buffer
        WritableRaster raster = Raster.createWritableRaster(sampleModel, dbuf, null);

        // Combine the color model and raster into a buffered image
        this.image = new BufferedImage(rgbColor.createColorModel(), raster, false, null);

        draw();
    }

    @Override
    public void setColor(byte r, byte g, byte b, int index) {
        rgbColor.set(r, g, b, index);
    }

    @Override
    public void setPixel(int x, int y, int color, boolean xor) {
        if (xor) {
            color ^= image.getRaster().getDataBuffer().getElem(y * 300 + x);
        }
        image.getRaster().getDataBuffer().setElem(y * 300 + x, color);
    }

    @Override
    public void applyColor() {
        this.image = new BufferedImage(rgbColor.createColorModel(), image.getRaster(), false, null);
        draw();
    }

    @Override
    public void clearBorder(int col) {
        Platform.runLater(() -> canvas.getGraphicsContext2D().setFill(rgbColor.createColor(col)));
        int pos = 0;
        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 300; x++) {
                image.getRaster().getDataBuffer().setElem(pos++, col);
            }
        }
        for (int y = 6; y < 210; y++) {
            for (int x = 0; x < 3; x++) {
                image.getRaster().getDataBuffer().setElem(pos++, col);
            }
            pos += 294;
            for (int x = 297; x < 300; x++) {
                image.getRaster().getDataBuffer().setElem(pos++, col);
            }
        }
        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 300; x++) {
                image.getRaster().getDataBuffer().setElem(pos++, col);
            }
        }
    }

    @Override
    public void clearScreen(int col) {
        for (int y = 6; y < 210; y++) {
            for (int x = 3; x < 297; x++) {
                image.getRaster().getDataBuffer().setElem(y * 300 + x, col);
            }
        }
    }

    @Override
    public void draw() {
        Image fxImage = SwingFXUtils.toFXImage(image, null);
        imageView.setImage(fxImage);
    }

}
