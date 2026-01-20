package karaoke.eq;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EqualizerSettingsTest {

    @Test
    void testConstructorWithValidBandCount() {
        // Arrange
        String name = "Custom";
        double[] bandValues = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0};
        boolean isPreset = false;

        // Act
        EqualizerSettings settings = new EqualizerSettings(name, bandValues, isPreset);

        // Assert
        assertNotNull(settings);
        assertEquals(name, settings.getName());
        assertArrayEquals(bandValues, settings.getBandValues());
        assertFalse(settings.isPreset());
    }

    @Test
    void testConstructorThrowsExceptionWithFewerThan10Elements() {
        // Arrange
        String name = "Invalid";
        double[] bandValues = {1.0, 2.0, 3.0}; // Only 3 elements
        boolean isPreset = false;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new EqualizerSettings(name, bandValues, isPreset)
        );
        assertTrue(exception.getMessage().contains("exactly 10 elements"));
    }

    @Test
    void testConstructorThrowsExceptionWithMoreThan10Elements() {
        // Arrange
        String name = "Invalid";
        double[] bandValues = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0}; // 12 elements
        boolean isPreset = false;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new EqualizerSettings(name, bandValues, isPreset)
        );
        assertTrue(exception.getMessage().contains("exactly 10 elements"));
    }

    @Test
    void testConstructorThrowsExceptionWithZeroElements() {
        // Arrange
        String name = "Invalid";
        double[] bandValues = {}; // Empty array
        boolean isPreset = false;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new EqualizerSettings(name, bandValues, isPreset)
        );
        assertTrue(exception.getMessage().contains("exactly 10 elements"));
    }

    @Test
    void testGetNameReturnsCorrectValue() {
        // Arrange
        EqualizerSettings settings = new EqualizerSettings(
            "Test Name",
            new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            true
        );

        // Act & Assert
        assertEquals("Test Name", settings.getName());
    }

    @Test
    void testGetBandValuesReturnsCopy() {
        // Arrange
        double[] originalValues = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        EqualizerSettings settings = new EqualizerSettings("Test", originalValues, false);

        // Act
        double[] returnedValues = settings.getBandValues();
        returnedValues[0] = 999; // Modify the returned array

        // Assert - Original should not be modified
        assertEquals(1.0, settings.getBandValues()[0]);
        assertArrayEquals(originalValues, settings.getBandValues());
    }

    @Test
    void testIsPresetReturnsCorrectValue() {
        // Arrange
        EqualizerSettings presetSettings = new EqualizerSettings(
            "Preset",
            new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            true
        );
        EqualizerSettings customSettings = new EqualizerSettings(
            "Custom",
            new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            false
        );

        // Assert
        assertTrue(presetSettings.isPreset());
        assertFalse(customSettings.isPreset());
    }

    @Test
    void testFlatFactoryMethod() {
        // Act
        EqualizerSettings flat = EqualizerSettings.flat();

        // Assert
        assertNotNull(flat);
        assertEquals("Flat", flat.getName());
        assertArrayEquals(new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, flat.getBandValues());
        assertTrue(flat.isPreset());
    }

    @Test
    void testRockFactoryMethod() {
        // Act
        EqualizerSettings rock = EqualizerSettings.rock();

        // Assert
        assertNotNull(rock);
        assertEquals("Rock", rock.getName());
        assertArrayEquals(new double[]{5, 4, 3, 1, -1, -1, 0, 2, 3, 4}, rock.getBandValues());
        assertTrue(rock.isPreset());
    }

    @Test
    void testPopFactoryMethod() {
        // Act
        EqualizerSettings pop = EqualizerSettings.pop();

        // Assert
        assertNotNull(pop);
        assertEquals("Pop", pop.getName());
        assertArrayEquals(new double[]{-1, 1, 3, 4, 3, 0, -1, -1, 1, 2}, pop.getBandValues());
        assertTrue(pop.isPreset());
    }

    @Test
    void testJazzFactoryMethod() {
        // Act
        EqualizerSettings jazz = EqualizerSettings.jazz();

        // Assert
        assertNotNull(jazz);
        assertEquals("Jazz", jazz.getName());
        assertArrayEquals(new double[]{3, 2, 1, 2, -1, -1, 0, 1, 2, 3}, jazz.getBandValues());
        assertTrue(jazz.isPreset());
    }

    @Test
    void testClassicalFactoryMethod() {
        // Act
        EqualizerSettings classical = EqualizerSettings.classical();

        // Assert
        assertNotNull(classical);
        assertEquals("Classical", classical.getName());
        assertArrayEquals(new double[]{4, 3, 2, 1, -1, -1, -1, 1, 2, 3}, classical.getBandValues());
        assertTrue(classical.isPreset());
    }

    @Test
    void testVocalFactoryMethod() {
        // Act
        EqualizerSettings vocal = EqualizerSettings.vocal();

        // Assert
        assertNotNull(vocal);
        assertEquals("Vocal", vocal.getName());
        assertArrayEquals(new double[]{-2, -1, 0, 2, 4, 4, 3, 1, 0, -1}, vocal.getBandValues());
        assertTrue(vocal.isPreset());
    }

    @Test
    void testBassFactoryMethod() {
        // Act
        EqualizerSettings bass = EqualizerSettings.bass();

        // Assert
        assertNotNull(bass);
        assertEquals("Bass Boost", bass.getName());
        assertArrayEquals(new double[]{6, 5, 4, 2, 0, 0, 0, 0, 0, 0}, bass.getBandValues());
        assertTrue(bass.isPreset());
    }

    @Test
    void testTrebleFactoryMethod() {
        // Act
        EqualizerSettings treble = EqualizerSettings.treble();

        // Assert
        assertNotNull(treble);
        assertEquals("Treble Boost", treble.getName());
        assertArrayEquals(new double[]{0, 0, 0, 0, 0, 1, 2, 4, 5, 6}, treble.getBandValues());
        assertTrue(treble.isPreset());
    }

    @Test
    void testToStringReturnsName() {
        // Arrange
        EqualizerSettings settings = new EqualizerSettings(
            "My Preset",
            new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            true
        );

        // Act & Assert
        assertEquals("My Preset", settings.toString());
    }

    @Test
    void testAllFactoryMethodsReturnValidObjects() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            EqualizerSettings.flat();
            EqualizerSettings.rock();
            EqualizerSettings.pop();
            EqualizerSettings.jazz();
            EqualizerSettings.classical();
            EqualizerSettings.vocal();
            EqualizerSettings.bass();
            EqualizerSettings.treble();
        });
    }

    @Test
    void testAllFactoryMethodsReturn10BandValues() {
        // Assert
        assertEquals(10, EqualizerSettings.flat().getBandValues().length);
        assertEquals(10, EqualizerSettings.rock().getBandValues().length);
        assertEquals(10, EqualizerSettings.pop().getBandValues().length);
        assertEquals(10, EqualizerSettings.jazz().getBandValues().length);
        assertEquals(10, EqualizerSettings.classical().getBandValues().length);
        assertEquals(10, EqualizerSettings.vocal().getBandValues().length);
        assertEquals(10, EqualizerSettings.bass().getBandValues().length);
        assertEquals(10, EqualizerSettings.treble().getBandValues().length);
    }
}
