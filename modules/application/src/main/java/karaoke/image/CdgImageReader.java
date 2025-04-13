package karaoke.image;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

public final class CdgImageReader extends TimerTask implements ImageReader {

    // frames displayed per second, CD+G has 300 packets per second -> 300/FPS = packets per frame
    private static final int FramesPerSecond = 20;
    private static final int PacketsPerFrame = 300 / FramesPerSecond;
    private static final int FrameInterval = 1000 / FramesPerSecond;

    private static final byte SC_MASK = 0x3F;
    private static final byte CDG_CMD = 0x09;
    private static final byte CDG_MEMORY_PRESET = 1;
    private static final byte CDG_BORDER_PRESET = 2;
    private static final byte CDG_TILE_BLOCK = 6;
    private static final byte CDG_SCROLL_PRESET = 20;
    private static final byte CDG_SCROLL_COPY = 24;
    private static final byte CDG_TRANSPARENT_COLOR = 28;
    private static final byte CDG_LOAD_COLOR_TABLE_LOW = 30;
    private static final byte CDG_LOAD_COLOR_TABLE_HIGH = 31;
    private static final byte CDG_TILE_BLOCK_XOR = 38;

    private final ImageViewer viewer;
    private final RandomAccessFile dis;
    private final long fileLength;
    private final Timer timer;
    private final ReentrantLock lock;

    private final byte[] packet;

    private boolean isPaused = true;

    public CdgImageReader(ImageViewer viewer, File cdgFile) {
        super();
        this.viewer = viewer;
        try {
            this.dis = new RandomAccessFile(cdgFile, "r");
            this.fileLength = dis.length();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        this.timer = new Timer(true);
        this.lock = new ReentrantLock();
        this.packet = new byte[24];
    }

    @Override
    public void initialize() {
        timer.scheduleAtFixedRate(this, 0, FrameInterval);
    }

    @Override
    public void play() {
        setPaused(false);
    }

    @Override
    public void pause() {
        setPaused(true);
    }

    @Override
    public void stop() {
        timer.cancel();
        try (Lock ignored = new Lock(lock)) {
            dis.close();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void seek(double percent) {
        try (Lock ignored = new Lock(lock)) {
            long seekPosition = getSeekPosition(percent);
            if (seekPosition > 0.0) {
                dis.seek(seekPosition);
            } else {
                dis.seek(0);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }


    @Override
    public void reset() {
        seek(0.0);
    }

    @Override
    public void run() {
        if (isPaused) {
            return;
        }
        int processedPackets = 0;
        int bytesRead = 0;
        while ((processedPackets < PacketsPerFrame) && ((bytesRead = readPacket()) == 24)) {
            processedPackets++;
            if ((packet[0] & SC_MASK) == CDG_CMD) {   // CD+G?
                switch (packet[1] & SC_MASK) {
                    case CDG_MEMORY_PRESET:
                        int color = packet[4] & 0x0F;
                        int repeat = packet[5] & 0x0F;
                        if (repeat == 0) {
                            viewer.clearScreen(color);
                        }
                        break;
                    case CDG_BORDER_PRESET:
                        int backgroundColor = packet[4] & 0x0F;
                        viewer.clearBorder(backgroundColor);
                        break;
                    case CDG_LOAD_COLOR_TABLE_LOW:
                        for (int i = 0; i < 8; i++) {
                            byte[] cols = decodeColor(packet[4 + i * 2], packet[5 + i * 2]);
                            viewer.setColor(cols[0], cols[1], cols[2], i);
                            viewer.applyColor();
                        }
                        break;
                    case CDG_LOAD_COLOR_TABLE_HIGH:
                        for (int i = 0; i < 8; i++) {
                            byte[] cols = decodeColor(packet[4 + i * 2], packet[5 + i * 2]);
                            viewer.setColor(cols[0], cols[1], cols[2], i + 8);
                            viewer.applyColor();
                        }
                        break;
                    case CDG_TILE_BLOCK:
                        paintTile(packet, false);
                        break;
                    case CDG_TILE_BLOCK_XOR:
                        paintTile(packet, true);
                        break;
                    default:
                        break;
                }
            }
        }
        viewer.draw();
        if (bytesRead == -1) {
            pause();
        }
    }

    private long getSeekPosition(double percent) {
        double position = percent * fileLength;
        position -= (position % 24.0);
        return (long) position;
    }

    private void setPaused(boolean pause) {
        if (isPaused != pause) {
            try (Lock ignored = new Lock(lock)) {
                this.isPaused = pause;
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private int readPacket() {
        try {
            return dis.read(packet);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private byte[] decodeColor(byte high, byte low) {
        return new byte[]{
                (byte) ((high & SC_MASK) >> 2),
                (byte) (((high & 0x3) << 2) | ((low >> 4) & 0x3)),
                (byte) (low & 0x0F)
        };
    }

    private void paintTile(byte[] packet, boolean xor) {
        int color0 = packet[4] & 0x0F;
        int color1 = packet[5] & 0x0F;
        int row = packet[6] & 0x1F;
        int column = packet[7] & 0x3F;

        if (row >= 17 || column >= 49) {
            return;
        }

        row *= 12;
        column *= 6;

        for (int y = 0; y < 12; y++) {
            int scanline = packet[y + 8] & 0x3F;
            for (int x = 5; x >= 0; x--) {
                int color = ((scanline & 1) == 0) ? color0 : color1;
                viewer.setPixel(column + x + 3, row + y + 6, color, xor);
                scanline = scanline >> 1;
            }
        }
    }

}
