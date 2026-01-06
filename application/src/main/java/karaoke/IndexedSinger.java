package karaoke;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.media.Media;

public final class IndexedSinger {

    private final SimpleIntegerProperty index;
    private final SimpleStringProperty singerName;
    private final SimpleStringProperty songFile;

    private Media media;

    public IndexedSinger(int index, String singerName, String songFile) {
        this.index = new SimpleIntegerProperty(index);
        this.singerName = new SimpleStringProperty(singerName);
        this.songFile = new SimpleStringProperty(songFile);
        initMedia();
    }

    public Singer getSinger() {
        return new Singer(singerName.getValue(), songFile.getValue());
    }

    public IndexedSinger copy() {
        return new IndexedSinger(getIndex(), getSingerName(), getSongFile());
    }

    public int getIndex() {
        return index.get();
    }

    public void setIndex(int index) {
        this.index.set(index);
    }

    public String getSingerName() {
        return singerName.get();
    }

    public void setSingerName(String singerName) {
        this.singerName.set(singerName);
    }

    public String getSongFile() {
        return songFile.get();
    }

    public void setSongFile(String songFile) {
        this.songFile.set(songFile);
        initMedia();
    }

    public Media getMedia() {
        return media;
    }

    private void initMedia() {
        this.media = new Media(songFile.get());
    }

}
