module application {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.media;
    requires javafx.graphics;
    requires java.desktop;
    exports karaoke;
    opens karaoke to javafx.fxml;
    exports karaoke.media;
    opens karaoke.media to javafx.fxml;
    exports karaoke.controller;
    opens karaoke.controller to javafx.fxml;
}