package karaoke;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Represents an equalizer preset with a name and band values.
 * Each band value represents a gain adjustment in decibels (-12 to +12).
 */
public record EqualizerPreset(String name, double[] bandValues, boolean isCustom) implements Serializable {

    private static final long serialVersionUID = 1L;

    // Standard 10-band EQ frequency labels
    public static final String[] BAND_LABELS = {
            "32Hz", "64Hz", "125Hz", "250Hz", "500Hz",
            "1kHz", "2kHz", "4kHz", "8kHz", "16kHz"
    };

    public static final int NUM_BANDS = 10;
    public static final double MIN_GAIN = -12.0;
    public static final double MAX_GAIN = 12.0;
    public static final double DEFAULT_GAIN = 0.0;

    // Pre-defined presets
    public static final EqualizerPreset FLAT = new EqualizerPreset(
            "Flat",
            new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            false
    );

    public static final EqualizerPreset BASS_BOOST = new EqualizerPreset(
            "Bass Boost",
            new double[]{10, 8, 5, 2, 0, 0, 0, 0, 0, 0},
            false
    );

    public static final EqualizerPreset TREBLE_BOOST = new EqualizerPreset(
            "Treble Boost",
            new double[]{0, 0, 0, 0, 0, 2, 4, 6, 8, 10},
            false
    );

    public static final EqualizerPreset VOCAL_BOOST = new EqualizerPreset(
            "Vocal Boost",
            new double[]{-2, -1, 0, 3, 5, 5, 3, 0, -1, -2},
            false
    );

    public static final EqualizerPreset ROCK = new EqualizerPreset(
            "Rock",
            new double[]{5, 4, 2, 0, -1, 0, 2, 4, 5, 5},
            false
    );

    public static final EqualizerPreset POP = new EqualizerPreset(
            "Pop",
            new double[]{-2, 0, 2, 4, 5, 5, 3, 0, -1, -2},
            false
    );

    public static final EqualizerPreset JAZZ = new EqualizerPreset(
            "Jazz",
            new double[]{3, 2, 1, 2, -2, -2, 0, 2, 3, 4},
            false
    );

    public static final EqualizerPreset CLASSICAL = new EqualizerPreset(
            "Classical",
            new double[]{4, 3, 2, 1, -1, -1, 0, 2, 3, 4},
            false
    );

    public static final EqualizerPreset DANCE = new EqualizerPreset(
            "Dance",
            new double[]{6, 5, 2, 0, 0, -2, -1, 3, 5, 4},
            false
    );

    public static final EqualizerPreset KARAOKE = new EqualizerPreset(
            "Karaoke",
            new double[]{3, 2, 0, -4, -6, -6, -4, 0, 2, 3},
            false
    );

    public static EqualizerPreset[] getBuiltInPresets() {
        return new EqualizerPreset[]{
                FLAT, BASS_BOOST, TREBLE_BOOST, VOCAL_BOOST,
                ROCK, POP, JAZZ, CLASSICAL, DANCE, KARAOKE
        };
    }

    /**
     * Creates a copy of this preset with a new name (useful for creating custom presets).
     */
    public EqualizerPreset withName(String newName) {
        return new EqualizerPreset(newName, Arrays.copyOf(bandValues, bandValues.length), true);
    }

    /**
     * Creates a copy of this preset with updated band values.
     */
    public EqualizerPreset withBandValues(double[] newBandValues) {
        return new EqualizerPreset(name, Arrays.copyOf(newBandValues, newBandValues.length), isCustom);
    }

    /**
     * Gets a copy of the band values array to prevent modification.
     */
    public double[] getBandValuesCopy() {
        return Arrays.copyOf(bandValues, bandValues.length);
    }

    /**
     * Gets the gain value for a specific band.
     */
    public double getBandValue(int bandIndex) {
        if (bandIndex >= 0 && bandIndex < bandValues.length) {
            return bandValues[bandIndex];
        }
        return DEFAULT_GAIN;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EqualizerPreset that = (EqualizerPreset) o;
        return name.equals(that.name) && Arrays.equals(bandValues, that.bandValues);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + Arrays.hashCode(bandValues);
        return result;
    }
}
