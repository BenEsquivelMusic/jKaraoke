package karaoke.util;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public final class FileChooserUtil {

    private FileChooserUtil() {
        /* Utility class */
    }

    /**
     * Shows a file chooser dialog with the given title.
     *
     * @param title the title of the dialog
     * @param stage the owner stage
     * @return the selected file, or null if no file was selected
     */
    public static File showFileChooser(String title, Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        return fileChooser.showOpenDialog(stage);
    }

    /**
     * Shows a file chooser dialog with the given title and extension filter.
     *
     * @param title the title of the dialog
     * @param stage the owner stage
     * @param extensionDescription the description of the file extension filter
     * @param extensions the file extensions (e.g., "*.txt")
     * @return the selected file, or null if no file was selected
     */
    public static File showFileChooserWithFilter(String title, Stage stage, String extensionDescription, String... extensions) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(extensionDescription, extensions)
        );
        return fileChooser.showOpenDialog(stage);
    }

    /**
     * Shows a file save dialog with the given title, initial file name, and extension filter.
     *
     * @param title the title of the dialog
     * @param stage the owner stage
     * @param initialFileName the initial file name
     * @param extensionDescription the description of the file extension filter
     * @param extensions the file extensions (e.g., "*.txt")
     * @return the selected file, or null if no file was selected
     */
    public static File showFileSaveDialog(String title, Stage stage, String initialFileName, String extensionDescription, String... extensions) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialFileName(initialFileName);
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(extensionDescription, extensions)
        );
        return fileChooser.showSaveDialog(stage);
    }

    /**
     * Shows a directory chooser dialog with the given title.
     *
     * @param title the title of the dialog
     * @param stage the owner stage
     * @return the selected directory, or null if no directory was selected
     */
    public static File showDirectoryChooser(String title, Stage stage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(title);
        return directoryChooser.showDialog(stage);
    }

}
