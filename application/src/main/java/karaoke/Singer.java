package karaoke;

import java.io.Serializable;

public record Singer(String singerName, String songFile) implements Serializable {
}
