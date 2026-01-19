package karaoke.event;

import karaoke.singer.Singer;

import java.io.Serializable;

public record Event(String name, Singer[] singers) implements Serializable {

}
