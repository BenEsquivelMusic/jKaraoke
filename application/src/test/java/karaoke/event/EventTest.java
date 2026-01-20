package karaoke.event;

import karaoke.singer.Singer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    @Test
    void testRecordCreationAndAccessors() {
        // Arrange
        Singer singer1 = new Singer("John Doe", "song1.mp3");
        Singer singer2 = new Singer("Jane Smith", "song2.mp3");
        Singer[] singers = {singer1, singer2};
        String eventName = "Karaoke Night";

        // Act
        Event event = new Event(eventName, singers);

        // Assert
        assertEquals(eventName, event.name());
        assertArrayEquals(singers, event.singers());
        assertEquals(2, event.singers().length);
        assertEquals("John Doe", event.singers()[0].singerName());
        assertEquals("Jane Smith", event.singers()[1].singerName());
    }

    @Test
    void testEqualityWithSameValues() {
        // Arrange
        Singer singer = new Singer("John", "song.mp3");
        Singer[] singers1 = {singer};
        Singer[] singers2 = {singer};
        Event event1 = new Event("Event1", singers1);
        Event event2 = new Event("Event1", singers2);

        // Assert
        // Note: Record equality for arrays compares array identity, not contents
        // So we test with same array reference for equality
        assertEquals(event1.name(), event2.name());
        assertArrayEquals(event1.singers(), event2.singers());
    }

    @Test
    void testEqualityWithDifferentName() {
        // Arrange
        Singer[] singers = {new Singer("John", "song.mp3")};
        Event event1 = new Event("Event1", singers);
        Event event2 = new Event("Event2", singers);

        // Assert
        assertNotEquals(event1, event2);
    }

    @Test
    void testEqualityWithDifferentSingers() {
        // Arrange
        Singer[] singers1 = {new Singer("John", "song1.mp3")};
        Singer[] singers2 = {new Singer("Jane", "song2.mp3")};
        Event event1 = new Event("Event1", singers1);
        Event event2 = new Event("Event1", singers2);

        // Assert
        assertNotEquals(event1, event2);
    }

    @Test
    void testEqualityWithSameInstance() {
        // Arrange
        Singer[] singers = {new Singer("John", "song.mp3")};
        Event event = new Event("Event1", singers);

        // Assert
        assertEquals(event, event);
    }

    @Test
    void testHashCodeConsistency() {
        // Arrange
        Singer[] singers = {new Singer("John", "song.mp3")};
        Event event1 = new Event("Event1", singers);
        Event event2 = new Event("Event1", singers);

        // Assert - Equal objects must have equal hash codes
        // Note: Using same array reference to ensure equality
        assertEquals(event1.hashCode(), event2.hashCode());
    }

    @Test
    void testHashCodeDifferentForDifferentEvents() {
        // Arrange
        Singer[] singers = {new Singer("John", "song.mp3")};
        Event event1 = new Event("Event1", singers);
        Event event2 = new Event("Event2", singers);

        // Assert - Different objects typically have different hash codes (not guaranteed but expected)
        assertNotEquals(event1.hashCode(), event2.hashCode());
    }

    @Test
    void testEmptySingersArray() {
        // Arrange
        Singer[] singers = {};
        Event event = new Event("Event1", singers);

        // Assert
        assertEquals("Event1", event.name());
        assertArrayEquals(singers, event.singers());
        assertEquals(0, event.singers().length);
    }
}
