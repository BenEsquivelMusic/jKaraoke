package karaoke.eq;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EqualizerPresetTest {

    @Test
    void testRecordCreationAndAccessors() {
        // Arrange
        String name = "Custom Preset";
        double[] bandValues = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0};
        boolean isCustom = true;

        // Act
        EqualizerPreset preset = new EqualizerPreset(name, bandValues, isCustom);

        // Assert
        assertEquals(name, preset.name());
        assertArrayEquals(bandValues, preset.bandValues());
        assertTrue(preset.isCustom());
    }

    @Test
    void testFlatPresetInitialization() {
        // Assert
        assertNotNull(EqualizerPreset.FLAT);
        assertEquals("Flat", EqualizerPreset.FLAT.name());
        assertArrayEquals(new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, EqualizerPreset.FLAT.bandValues());
        assertFalse(EqualizerPreset.FLAT.isCustom());
    }

    @Test
    void testBassBoostPresetInitialization() {
        // Assert
        assertNotNull(EqualizerPreset.BASS_BOOST);
        assertEquals("Bass Boost", EqualizerPreset.BASS_BOOST.name());
        assertArrayEquals(new double[]{10, 8, 5, 2, 0, 0, 0, 0, 0, 0}, EqualizerPreset.BASS_BOOST.bandValues());
        assertFalse(EqualizerPreset.BASS_BOOST.isCustom());
    }

    @Test
    void testTrebleBoostPresetInitialization() {
        // Assert
        assertNotNull(EqualizerPreset.TREBLE_BOOST);
        assertEquals("Treble Boost", EqualizerPreset.TREBLE_BOOST.name());
        assertArrayEquals(new double[]{0, 0, 0, 0, 0, 2, 4, 6, 8, 10}, EqualizerPreset.TREBLE_BOOST.bandValues());
        assertFalse(EqualizerPreset.TREBLE_BOOST.isCustom());
    }

    @Test
    void testVocalBoostPresetInitialization() {
        // Assert
        assertNotNull(EqualizerPreset.VOCAL_BOOST);
        assertEquals("Vocal Boost", EqualizerPreset.VOCAL_BOOST.name());
        assertArrayEquals(new double[]{-2, -1, 0, 3, 5, 5, 3, 0, -1, -2}, EqualizerPreset.VOCAL_BOOST.bandValues());
        assertFalse(EqualizerPreset.VOCAL_BOOST.isCustom());
    }

    @Test
    void testRockPresetInitialization() {
        // Assert
        assertNotNull(EqualizerPreset.ROCK);
        assertEquals("Rock", EqualizerPreset.ROCK.name());
        assertArrayEquals(new double[]{5, 4, 2, 0, -1, 0, 2, 4, 5, 5}, EqualizerPreset.ROCK.bandValues());
        assertFalse(EqualizerPreset.ROCK.isCustom());
    }

    @Test
    void testPopPresetInitialization() {
        // Assert
        assertNotNull(EqualizerPreset.POP);
        assertEquals("Pop", EqualizerPreset.POP.name());
        assertArrayEquals(new double[]{-2, 0, 2, 4, 5, 5, 3, 0, -1, -2}, EqualizerPreset.POP.bandValues());
        assertFalse(EqualizerPreset.POP.isCustom());
    }

    @Test
    void testJazzPresetInitialization() {
        // Assert
        assertNotNull(EqualizerPreset.JAZZ);
        assertEquals("Jazz", EqualizerPreset.JAZZ.name());
        assertArrayEquals(new double[]{3, 2, 1, 2, -2, -2, 0, 2, 3, 4}, EqualizerPreset.JAZZ.bandValues());
        assertFalse(EqualizerPreset.JAZZ.isCustom());
    }

    @Test
    void testClassicalPresetInitialization() {
        // Assert
        assertNotNull(EqualizerPreset.CLASSICAL);
        assertEquals("Classical", EqualizerPreset.CLASSICAL.name());
        assertArrayEquals(new double[]{4, 3, 2, 1, -1, -1, 0, 2, 3, 4}, EqualizerPreset.CLASSICAL.bandValues());
        assertFalse(EqualizerPreset.CLASSICAL.isCustom());
    }

    @Test
    void testDancePresetInitialization() {
        // Assert
        assertNotNull(EqualizerPreset.DANCE);
        assertEquals("Dance", EqualizerPreset.DANCE.name());
        assertArrayEquals(new double[]{6, 5, 2, 0, 0, -2, -1, 3, 5, 4}, EqualizerPreset.DANCE.bandValues());
        assertFalse(EqualizerPreset.DANCE.isCustom());
    }

    @Test
    void testKaraokePresetInitialization() {
        // Assert
        assertNotNull(EqualizerPreset.KARAOKE);
        assertEquals("Karaoke", EqualizerPreset.KARAOKE.name());
        assertArrayEquals(new double[]{3, 2, 0, -4, -6, -6, -4, 0, 2, 3}, EqualizerPreset.KARAOKE.bandValues());
        assertFalse(EqualizerPreset.KARAOKE.isCustom());
    }

    @Test
    void testEqualityWithSameValues() {
        // Arrange
        double[] bandValues1 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] bandValues2 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        EqualizerPreset preset1 = new EqualizerPreset("Custom", bandValues1, true);
        EqualizerPreset preset2 = new EqualizerPreset("Custom", bandValues2, true);

        // Assert
        assertEquals(preset1, preset2);
    }

    @Test
    void testEqualityWithDifferentName() {
        // Arrange
        double[] bandValues = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        EqualizerPreset preset1 = new EqualizerPreset("Custom1", bandValues, true);
        EqualizerPreset preset2 = new EqualizerPreset("Custom2", bandValues, true);

        // Assert
        assertNotEquals(preset1, preset2);
    }

    @Test
    void testEqualityWithDifferentBandValues() {
        // Arrange
        double[] bandValues1 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] bandValues2 = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
        EqualizerPreset preset1 = new EqualizerPreset("Custom", bandValues1, true);
        EqualizerPreset preset2 = new EqualizerPreset("Custom", bandValues2, true);

        // Assert
        assertNotEquals(preset1, preset2);
    }

    @Test
    void testEqualityWithSameInstance() {
        // Arrange
        EqualizerPreset preset = new EqualizerPreset("Custom", new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, true);

        // Assert
        assertEquals(preset, preset);
    }

    @Test
    void testHashCodeConsistency() {
        // Arrange
        double[] bandValues1 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] bandValues2 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        EqualizerPreset preset1 = new EqualizerPreset("Custom", bandValues1, true);
        EqualizerPreset preset2 = new EqualizerPreset("Custom", bandValues2, true);

        // Assert - Equal objects must have equal hash codes
        assertEquals(preset1.hashCode(), preset2.hashCode());
    }

    @Test
    void testHashCodeDifferentForDifferentPresets() {
        // Arrange
        double[] bandValues1 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] bandValues2 = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
        EqualizerPreset preset1 = new EqualizerPreset("Custom", bandValues1, true);
        EqualizerPreset preset2 = new EqualizerPreset("Custom", bandValues2, true);

        // Assert - Different objects typically have different hash codes (not guaranteed but expected)
        assertNotEquals(preset1.hashCode(), preset2.hashCode());
    }

    @Test
    void testBuiltInPresetsAreNotCustom() {
        // Assert
        assertFalse(EqualizerPreset.FLAT.isCustom());
        assertFalse(EqualizerPreset.BASS_BOOST.isCustom());
        assertFalse(EqualizerPreset.TREBLE_BOOST.isCustom());
        assertFalse(EqualizerPreset.VOCAL_BOOST.isCustom());
        assertFalse(EqualizerPreset.ROCK.isCustom());
        assertFalse(EqualizerPreset.POP.isCustom());
        assertFalse(EqualizerPreset.JAZZ.isCustom());
        assertFalse(EqualizerPreset.CLASSICAL.isCustom());
        assertFalse(EqualizerPreset.DANCE.isCustom());
        assertFalse(EqualizerPreset.KARAOKE.isCustom());
    }
}
