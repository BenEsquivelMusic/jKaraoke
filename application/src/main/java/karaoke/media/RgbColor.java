package karaoke.media;

import javafx.scene.paint.Color;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.Objects;

public final class RgbColor {

    private static final int NUM_BYTES = 16;
    private static final int NUM_BITS = 4;
    private static final int BIT_MASK = 0xFF;

    private final MemorySegment r;
    private final MemorySegment g;
    private final MemorySegment b;

    public RgbColor() {
        MemorySegment parentMemorySegment = Arena.ofAuto().allocate(ValueLayout.JAVA_BYTE, NUM_BYTES * 3);

        this.r = parentMemorySegment.asSlice(0, NUM_BYTES * ValueLayout.JAVA_BYTE.byteSize());
        this.g = parentMemorySegment.asSlice(NUM_BYTES * ValueLayout.JAVA_BYTE.byteSize(), NUM_BYTES * ValueLayout.JAVA_BYTE.byteSize());
        this.b = parentMemorySegment.asSlice((NUM_BYTES + NUM_BYTES) * ValueLayout.JAVA_BYTE.byteSize(), NUM_BYTES * ValueLayout.JAVA_BYTE.byteSize());
    }

    public void set(byte red, byte green, byte blue, int index) {
        Objects.checkIndex(index, NUM_BYTES);
        this.r.setAtIndex(ValueLayout.JAVA_BYTE, index, (byte) (red << NUM_BITS | red));
        this.g.setAtIndex(ValueLayout.JAVA_BYTE, index, (byte) (green << NUM_BITS | green));
        this.b.setAtIndex(ValueLayout.JAVA_BYTE, index, (byte) (blue << NUM_BITS | blue));
    }

    public int getArgb(int index) {
        int red = BIT_MASK & r.getAtIndex(ValueLayout.JAVA_BYTE, index);
        int green = BIT_MASK & g.getAtIndex(ValueLayout.JAVA_BYTE, index);
        int blue = BIT_MASK & b.getAtIndex(ValueLayout.JAVA_BYTE, index);
        return (0xFF << 24) | (red << 16) | (green << 8) | blue;
    }

    public Color createColor(int col) {
        return Color.rgb(
                BIT_MASK & r.getAtIndex(ValueLayout.JAVA_BYTE, col),
                BIT_MASK & g.getAtIndex(ValueLayout.JAVA_BYTE, col),
                BIT_MASK & b.getAtIndex(ValueLayout.JAVA_BYTE, col));
    }

}
