module application {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.media;
    exports com.bem;
    opens com.bem to javafx.fxml;
}