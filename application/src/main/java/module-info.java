module application {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.media;
    requires javafx.graphics;
    requires java.desktop;
    requires java.logging;
    exports karaoke;
    opens karaoke to javafx.fxml;
    exports karaoke.media;
    opens karaoke.media to javafx.fxml;
    exports karaoke.controller;
    opens karaoke.controller to javafx.fxml;
    exports karaoke.event;
    opens karaoke.event to javafx.fxml;
    exports karaoke.util;
    opens karaoke.util to javafx.fxml;
    exports karaoke.singer;
    opens karaoke.singer to javafx.fxml;
    exports karaoke.eq;
    opens karaoke.eq to javafx.fxml;
}