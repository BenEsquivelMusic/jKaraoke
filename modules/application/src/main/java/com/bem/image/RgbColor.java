package com.bem.image;

import javafx.scene.paint.Color;

import java.awt.image.IndexColorModel;
import java.util.Objects;

public final class RgbColor {

    private static final int NUM_BYTES = 16;
    private static final int NUM_BITS = 4;
    private static final int BIT_MASK = 0xFF;

    private final byte[] r;
    private final byte[] g;
    private final byte[] b;

    public RgbColor() {
        this.r = new byte[NUM_BYTES];
        this.g = new byte[NUM_BYTES];
        this.b = new byte[NUM_BYTES];
    }

    public void set(byte red, byte green, byte blue, int index) {
        Objects.checkIndex(index, NUM_BYTES);
        this.r[index] = (byte) (red << NUM_BITS | red);
        this.g[index] = (byte) (green << NUM_BITS | green);
        this.b[index] = (byte) (blue << NUM_BITS | blue);
    }

    public IndexColorModel createColorModel() {
        return new IndexColorModel(NUM_BITS, NUM_BYTES, r.clone(), g.clone(), b.clone());
    }

    public Color createColor(int col) {
        return Color.rgb(BIT_MASK & r[col], BIT_MASK & g[col], BIT_MASK & b[col]);
    }

}
