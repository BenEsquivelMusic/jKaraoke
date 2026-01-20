package karaoke.event;

import karaoke.util.Lock;

import java.io.*;
import java.nio.file.Files;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public final class EventManager {

    private static final Logger logger = Logger.getLogger(EventManager.class.getName());

    private final ReentrantLock lock;

    /* karaoke event (.kev) file */
    private final File eventFile;

    public EventManager(File eventFile) {
        this.eventFile = eventFile;
        this.lock = new ReentrantLock();
    }

    public void writeEvent(Event event) {
        try (Lock _ = new Lock(lock);
             ObjectOutputStream oos = new ObjectOutputStream(
                     new BufferedOutputStream(Files.newOutputStream(eventFile.toPath())))) {
            oos.writeObject(event);
            oos.flush();
        } catch (IOException | InterruptedException e) {
            throw new IllegalArgumentException(e);
        }
        logger.info("Event saved");
    }

    public Event readEvent() {
        try (Lock _ = new Lock(lock);
             ObjectInputStream ois = new ObjectInputStream(
                     new BufferedInputStream(Files.newInputStream(eventFile.toPath())))) {
            return (Event) ois.readObject();
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
