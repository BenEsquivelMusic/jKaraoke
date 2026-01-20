package karaoke.singer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SingerTest {

    @Test
    void testRecordCreationAndAccessors() {
        // Arrange
        String singerName = "John Doe";
        String songFile = "song.mp3";

        // Act
        Singer singer = new Singer(singerName, songFile);

        // Assert
        assertEquals(singerName, singer.singerName());
        assertEquals(songFile, singer.songFile());
    }

    @Test
    void testEqualityWithSameValues() {
        // Arrange
        Singer singer1 = new Singer("John Doe", "song.mp3");
        Singer singer2 = new Singer("John Doe", "song.mp3");

        // Assert
        assertEquals(singer1, singer2);
    }

    @Test
    void testEqualityWithDifferentName() {
        // Arrange
        Singer singer1 = new Singer("John Doe", "song.mp3");
        Singer singer2 = new Singer("Jane Smith", "song.mp3");

        // Assert
        assertNotEquals(singer1, singer2);
    }

    @Test
    void testEqualityWithDifferentSongFile() {
        // Arrange
        Singer singer1 = new Singer("John Doe", "song1.mp3");
        Singer singer2 = new Singer("John Doe", "song2.mp3");

        // Assert
        assertNotEquals(singer1, singer2);
    }

    @Test
    void testEqualityWithSameInstance() {
        // Arrange
        Singer singer = new Singer("John Doe", "song.mp3");

        // Assert
        assertEquals(singer, singer);
    }

    @Test
    void testHashCodeConsistency() {
        // Arrange
        Singer singer1 = new Singer("John Doe", "song.mp3");
        Singer singer2 = new Singer("John Doe", "song.mp3");

        // Assert - Equal objects must have equal hash codes
        assertEquals(singer1.hashCode(), singer2.hashCode());
    }

    @Test
    void testHashCodeDifferentForDifferentSingers() {
        // Arrange
        Singer singer1 = new Singer("John Doe", "song1.mp3");
        Singer singer2 = new Singer("John Doe", "song2.mp3");

        // Assert - Different objects typically have different hash codes (not guaranteed but expected)
        assertNotEquals(singer1.hashCode(), singer2.hashCode());
    }

    @Test
    void testWithNullValues() {
        // Arrange & Act
        Singer singer = new Singer(null, null);

        // Assert
        assertNull(singer.singerName());
        assertNull(singer.songFile());
    }

    @Test
    void testWithEmptyStrings() {
        // Arrange
        Singer singer = new Singer("", "");

        // Assert
        assertEquals("", singer.singerName());
        assertEquals("", singer.songFile());
    }
}
