module application {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.media;
    requires javafx.graphics;
    requires javafx.swing;
    requires java.desktop;
    exports com.bem;
    opens com.bem to javafx.fxml;
    exports com.bem.image;
    opens com.bem.image to javafx.fxml;
}