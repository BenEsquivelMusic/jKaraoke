package karaoke.image;

import java.awt.image.BufferedImage;
import java.util.concurrent.locks.ReentrantLock;

public final class MediaImage {

    private final ReentrantLock lock;
    private BufferedImage image;

    public MediaImage(BufferedImage image) {
        this.image = image;
        this.lock = new ReentrantLock();
    }

    public void setElement(int index, int color) {
        try (Lock ignored = new Lock(lock)) {
            image.getRaster().getDataBuffer().setElem(index, color);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    public int getElement(int index) {
        return image.getRaster().getDataBuffer().getElem(index);
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        try (Lock ignored = new Lock(lock)) {
            this.image = image;
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

}
