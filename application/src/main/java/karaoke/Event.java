package karaoke;

import java.io.Serializable;

public record Event(String name, Singer[] singers) implements Serializable {

}
