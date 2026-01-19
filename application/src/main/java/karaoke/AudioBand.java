package karaoke;

import java.util.List;
import java.util.Objects;

public final class AudioBand {

    private static final List<String> LABELS = List.of("60Hz", "170Hz", "310Hz", "600Hz",
            "1kHz", "3kHz", "6kHz", "12kHz", "14kHz", "16kHz");

    public final int band;

    public AudioBand(int band) {
        this.band = band;
        validate();
    }

    @Override
    public String toString() {
        return LABELS.get(band - 1);
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

    private void validate() {
        if (band > LABELS.size()) {
            throw new IllegalArgumentException("Invalid band number provided: " + band);
        }
    }

}
