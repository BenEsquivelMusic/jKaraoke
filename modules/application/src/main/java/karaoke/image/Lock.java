package karaoke.image;

import java.util.concurrent.locks.ReentrantLock;

public final class Lock implements AutoCloseable {

    private final ReentrantLock lock;

    public Lock(ReentrantLock lock) throws InterruptedException {
        this.lock = lock;
        lock();
    }

    @Override
    public void close() {
        lock.unlock();
    }

    private void lock() throws InterruptedException {
        lock.lockInterruptibly();
    }

}
