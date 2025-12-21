package karaoke;

import java.util.Objects;

public final class AudioBand {

    private static final String PHASE_LABEL_PREFIX = "Band ";

    public final int band;

    public AudioBand(int band) {
        this.band = band;
    }

    @Override
    public String toString() {
        return PHASE_LABEL_PREFIX + band;
    }

    @Override
    public int hashCode() {
        return band;
    }

    @Override
    public boolean equals(Object other) {
        if (Objects.isNull(other)) {
            return false;
        }
        if (other instanceof AudioBand otherAudioBand) {
            return this.band == otherAudioBand.band;
        }
        return false;
    }

}
