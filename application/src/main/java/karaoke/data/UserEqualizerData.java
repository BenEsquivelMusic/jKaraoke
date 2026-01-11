package karaoke.data;

import karaoke.settings.EqualizerSettings;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class UserEqualizerData {

    private static final String USER_DATA_DIR = System.getProperty("user.home") + File.separator + ".jkaraoke";
    private static final String EQ_FILE_NAME = "custom_eq_presets.dat";
    private static final Path EQ_FILE_PATH = Paths.get(USER_DATA_DIR, EQ_FILE_NAME);

    private UserEqualizerData() {
    }

    public static void saveCustomPresets(List<EqualizerSettings> presets) {
        try {
            Files.createDirectories(Paths.get(USER_DATA_DIR));
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(EQ_FILE_PATH))) {
                List<SerializablePreset> serializablePresets = new ArrayList<>();
                for (EqualizerSettings preset : presets) {
                    serializablePresets.add(new SerializablePreset(preset.getName(), preset.getBandValues()));
                }
                oos.writeObject(serializablePresets);
            }
        } catch (IOException e) {
            System.err.println("Failed to save custom EQ presets: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<EqualizerSettings> loadCustomPresets() {
        List<EqualizerSettings> result = new ArrayList<>();
        if (Files.exists(EQ_FILE_PATH)) {
            try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(EQ_FILE_PATH))) {
                List<SerializablePreset> serializablePresets = (List<SerializablePreset>) ois.readObject();
                for (SerializablePreset sp : serializablePresets) {
                    result.add(new EqualizerSettings(sp.name, sp.bandValues, false));
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Failed to load custom EQ presets: " + e.getMessage());
            }
        }
        return result;
    }

    private static class SerializablePreset implements Serializable {
        private static final long serialVersionUID = 1L;
        final String name;
        final double[] bandValues;

        SerializablePreset(String name, double[] bandValues) {
            this.name = name;
            this.bandValues = bandValues;
        }
    }
}
