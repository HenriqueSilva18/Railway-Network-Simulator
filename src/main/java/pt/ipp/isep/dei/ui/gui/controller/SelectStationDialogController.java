package pt.ipp.isep.dei.ui.gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pt.ipp.isep.dei.controller.template.ApplicationSession;
import pt.ipp.isep.dei.domain.template.Station;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SelectStationDialogController implements Initializable {
    @FXML
    private ListView<String> stationsListView;
    
    @FXML
    private Label stationNameLabel;
    @FXML
    private Label stationTypeLabel;
    @FXML
    private Label stationPositionLabel;
    @FXML
    private Label stationDetailsLabel;
    
    @FXML
    private Button selectButton;
    
    private List<Station> stations;
    private Station selectedStation;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Get all stations from the current map
        stations = ApplicationSession.getInstance().getCurrentMap().getStations();
        
        // Populate the list view
        ObservableList<String> items = FXCollections.observableArrayList();
        for (Station station : stations) {
            items.add(station.getNameID());
        }
        stationsListView.setItems(items);
        
        // Add selection listener
        stationsListView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    updateStationDetails(newValue);
                    selectButton.setDisable(false);
                } else {
                    clearStationDetails();
                    selectButton.setDisable(true);
                }
            }
        );
    }
    
    private void updateStationDetails(String stationName) {
        for (Station station : stations) {
            if (station.getNameID().equals(stationName)) {
                selectedStation = station;
                
                // Get station info using the station's getInfo() method
                Station.StationInfo info = station.getInfo();
                
                stationNameLabel.setText(info.getNameID());
                stationTypeLabel.setText("Type: " + info.getType());
                stationPositionLabel.setText(String.format("Position: (%d, %d)", info.getPosX(), info.getPosY()));
                stationDetailsLabel.setText(String.format("Building Slots: %d/%d", 
                    info.getUsedBuildingSlots(), info.getTotalBuildingSlots()));
                break;
            }
        }
    }
    
    private void clearStationDetails() {
        selectedStation = null;
        stationNameLabel.setText("");
        stationTypeLabel.setText("");
        stationPositionLabel.setText("");
        stationDetailsLabel.setText("");
    }
    
    @FXML
    private void handleSelect() {
        if (selectedStation != null) {
            ApplicationSession.getInstance().setCurrentStation(selectedStation);
            getStage().close();
        }
    }
    
    @FXML
    private void handleCancel() {
        selectedStation = null;
        getStage().close();
    }
    
    private Stage getStage() {
        return (Stage) selectButton.getScene().getWindow();
    }
} 