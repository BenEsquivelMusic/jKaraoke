module application {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.media;
    requires javafx.graphics;
    requires javafx.swing;
    requires java.desktop;
    exports karaoke;
    opens karaoke to javafx.fxml;
    exports karaoke.image;
    opens karaoke.image to javafx.fxml;
}