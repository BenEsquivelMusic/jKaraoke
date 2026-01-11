package karaoke.settings;

import java.util.Arrays;
import java.util.Objects;

public final class EqualizerSettings {

    public static final int BAND_COUNT = 10;
    
    private final String name;
    private final double[] bandValues;
    private final boolean isPreset;

    public EqualizerSettings(String name, double[] bandValues, boolean isPreset) {
        this.name = Objects.requireNonNull(name);
        if (bandValues.length != BAND_COUNT) {
            throw new IllegalArgumentException("Band values must have exactly " + BAND_COUNT + " elements");
        }
        this.bandValues = Arrays.copyOf(bandValues, BAND_COUNT);
        this.isPreset = isPreset;
    }

    public String getName() {
        return name;
    }

    public double[] getBandValues() {
        return Arrays.copyOf(bandValues, BAND_COUNT);
    }

    public double getBandValue(int bandIndex) {
        if (bandIndex < 0 || bandIndex >= BAND_COUNT) {
            throw new IndexOutOfBoundsException("Band index must be between 0 and " + (BAND_COUNT - 1));
        }
        return bandValues[bandIndex];
    }

    public boolean isPreset() {
        return isPreset;
    }

    public static EqualizerSettings flat() {
        return new EqualizerSettings("Flat", new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, true);
    }

    public static EqualizerSettings rock() {
        return new EqualizerSettings("Rock", new double[]{5, 4, 3, 1, -1, -1, 0, 2, 3, 4}, true);
    }

    public static EqualizerSettings pop() {
        return new EqualizerSettings("Pop", new double[]{-1, 1, 3, 4, 3, 0, -1, -1, 1, 2}, true);
    }

    public static EqualizerSettings jazz() {
        return new EqualizerSettings("Jazz", new double[]{3, 2, 1, 2, -1, -1, 0, 1, 2, 3}, true);
    }

    public static EqualizerSettings classical() {
        return new EqualizerSettings("Classical", new double[]{4, 3, 2, 1, -1, -1, -1, 1, 2, 3}, true);
    }

    public static EqualizerSettings vocal() {
        return new EqualizerSettings("Vocal", new double[]{-2, -1, 0, 2, 4, 4, 3, 1, 0, -1}, true);
    }

    public static EqualizerSettings bass() {
        return new EqualizerSettings("Bass Boost", new double[]{6, 5, 4, 2, 0, 0, 0, 0, 0, 0}, true);
    }

    public static EqualizerSettings treble() {
        return new EqualizerSettings("Treble Boost", new double[]{0, 0, 0, 0, 0, 1, 2, 4, 5, 6}, true);
    }

    @Override
    public String toString() {
        return name;
    }
}