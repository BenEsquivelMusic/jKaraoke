package com.bem;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public final class SingerLineupFxmlController implements Initializable {

    @FXML
    private AnchorPane singerViewPane;
    @FXML
    private TableView<IndexedSinger> singerLineupTableView;
    @FXML
    private TableColumn<IndexedSinger, Integer> columnLineupNumber;
    @FXML
    private TableColumn<IndexedSinger, String> columnSingerName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        columnLineupNumber.setCellValueFactory(new PropertyValueFactory<>("Index"));
        columnSingerName.setCellValueFactory(new PropertyValueFactory<>("SingerName"));
    }

    public void setSingers(ObservableList<IndexedSinger> singers) {
        singerLineupTableView.setItems(singers);
    }

    public void showSingerLineup() {
        Stage stage = (Stage) singerViewPane.getScene().getWindow();
        stage.show();
    }

}
