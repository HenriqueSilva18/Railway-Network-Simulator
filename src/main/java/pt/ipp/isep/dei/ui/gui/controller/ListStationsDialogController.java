package pt.ipp.isep.dei.ui.gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pt.ipp.isep.dei.controller.template.ApplicationSession;
import pt.ipp.isep.dei.controller.template.ListStationController;
import pt.ipp.isep.dei.domain.template.Cargo;
import pt.ipp.isep.dei.domain.template.Station;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ListStationsDialogController implements Initializable {
    @FXML
    private ListView<String> stationsListView;
    
    @FXML
    private Label stationNameLabel;
    @FXML
    private Label stationTypeLabel;
    @FXML
    private Label stationPositionLabel;
    @FXML
    private Label storageCapacityLabel;
    @FXML
    private Label buildingSlotsLabel;
    
    @FXML
    private ListView<String> buildingsListView;
    
    @FXML
    private TextArea buildingEventsLog;
    
    @FXML
    private ListView<String> servedCitiesListView;
    @FXML
    private ListView<String> servedIndustriesListView;
    @FXML
    private ListView<String> availableCargoListView;
    @FXML
    private ListView<String> requestedCargoListView;
    
    @FXML
    private Button upgradeButton;
    
    private final ListStationController controller;
    private List<Station> stations;
    private Map<String, List<String>> stationBuildingEvents; // Map of station ID to list of events
    private Map<String, String> previousBuildingStates; // Map to track previous building states
    
    public ListStationsDialogController() {
        this.controller = new ListStationController();
        this.stationBuildingEvents = new HashMap<>();
        this.previousBuildingStates = new HashMap<>();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Get stations from controller
        stations = controller.getStations();
        
        // Populate stations list with summary information
        ObservableList<String> stationItems = FXCollections.observableArrayList();
        for (Station station : stations) {
            StringBuilder stationSummary = new StringBuilder();
            stationSummary.append(station.getNameID())
                .append(" (Type: ").append(station.getStationType().getName()).append(")\n");
            
            // Add supply summary
            stationSummary.append("Supply: ");
            List<Cargo> availableCargo = station.getAvailableCargo();
            if (availableCargo.isEmpty()) {
                stationSummary.append("None");
            } else {
                stationSummary.append(summarizeCargo(availableCargo));
            }
            
            // Add demand summary
            stationSummary.append("\nDemand: ");
            List<Cargo> requestedCargo = station.getRequestedCargo();
            if (requestedCargo.isEmpty()) {
                stationSummary.append("None");
            } else {
                stationSummary.append(summarizeCargo(requestedCargo));
            }
            
            stationItems.add(stationSummary.toString());
        }
        stationsListView.setItems(stationItems);
        
        // Add selection listener
        stationsListView.getSelectionModel().selectedIndexProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue.intValue() >= 0 && newValue.intValue() < stations.size()) {
                    updateStationDetails(stations.get(newValue.intValue()));
                    upgradeButton.setDisable(false);
                } else {
                    clearStationDetails();
                    upgradeButton.setDisable(true);
                }
            }
        );
    }
    
    private String summarizeCargo(List<Cargo> cargoList) {
        StringBuilder summary = new StringBuilder();
        for (int i = 0; i < cargoList.size(); i++) {
            Cargo cargo = cargoList.get(i);
            summary.append(cargo.getName()).append(" (").append(cargo.getAmount()).append(")");
            
            if (i < cargoList.size() - 1) {
                summary.append(", ");
            }
        }
        return summary.toString();
    }
    
    private void updateStationDetails(Station station) {
        // Set the current station in ApplicationSession
        ApplicationSession.getInstance().setCurrentStation(station);
        
        // Update available cargo before displaying details
        station.updateAvailableCargo();
        
        // Update basic information
        stationNameLabel.setText(station.getNameID());
        stationTypeLabel.setText(station.getStationType().getName());
        stationPositionLabel.setText(String.format("(%d, %d)", 
            station.getPosition().getX(), 
            station.getPosition().getY()));
        storageCapacityLabel.setText(String.format("%d/%d units", 
            station.getCurrentStorage(),
            station.getStorageCapacity()));
        buildingSlotsLabel.setText(String.format("%d/%d", station.getBuildings().size(), station.getBuildingSlots()));
        
        // Update buildings list and events log
        ObservableList<String> buildingItems = FXCollections.observableArrayList();
        List<String> currentEvents = stationBuildingEvents.computeIfAbsent(station.getNameID(), k -> new ArrayList<>());
        
        station.getBuildings().forEach(building -> {
            buildingItems.add(String.format("%s (Type: %s) - %s",
                building.getNameID(),
                building.getType(),
                building.getEffect()));
                
            // Check for building evolution
            String buildingKey = station.getNameID() + "_" + building.getNameID();
            String previousState = previousBuildingStates.get(buildingKey);
            String currentState = building.getNameID() + "_" + building.getType();
            
            if (previousState != null && !previousState.equals(currentState)) {
                // Building has evolved
                String event = String.format("[%s] Building '%s' evolved to %s",
                    java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    previousState.split("_")[0], // Previous name
                    building.getNameID());
                currentEvents.add(event);
            } else if (previousState == null) {
                // New building
                String event = String.format("[%s] New building '%s' (%s) constructed",
                    java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    building.getNameID(),
                    building.getType());
                currentEvents.add(event);
            }
            
            // Update the previous state
            previousBuildingStates.put(buildingKey, currentState);
        });
        
        buildingsListView.setItems(buildingItems);
        buildingEventsLog.setText(String.join("\n", currentEvents));
        
        // Update served cities list
        ObservableList<String> cityItems = FXCollections.observableArrayList();
        Set<String> displayedCities = new HashSet<>();
        station.getServedCities().forEach(city -> {
            String cityName = city.getNameID();
            if (displayedCities.add(cityName)) {
                cityItems.add(cityName);
            }
        });
        servedCitiesListView.setItems(cityItems);

        // Update served industries list
        ObservableList<String> industryItems = FXCollections.observableArrayList();
        Set<String> displayedIndustries = new HashSet<>();
        station.getServedIndustries().forEach(industry -> {
            String industryName = industry.getNameID();
            if (displayedIndustries.add(industryName)) {
                industryItems.add(String.format("%s (%s)", industryName, industry.getType()));
            }
        });
        servedIndustriesListView.setItems(industryItems);
        
        // Update available cargo list
        ObservableList<String> availableItems = FXCollections.observableArrayList();
        station.getAvailableCargo().forEach(cargo ->
            availableItems.add(String.format("%s: %d units (Type: %s)",
                cargo.getName(), cargo.getAmount(), cargo.getType())));
        availableCargoListView.setItems(availableItems);
        
        // Update requested cargo list
        ObservableList<String> requestedItems = FXCollections.observableArrayList();
        station.getRequestedCargo().forEach(cargo ->
            requestedItems.add(String.format("%s: %d units (Type: %s)",
                cargo.getName(), cargo.getAmount(), cargo.getType())));
        requestedCargoListView.setItems(requestedItems);
    }
    
    private void clearStationDetails() {
        stationNameLabel.setText("");
        stationTypeLabel.setText("");
        stationPositionLabel.setText("");
        storageCapacityLabel.setText("");
        buildingSlotsLabel.setText("");
        buildingsListView.setItems(FXCollections.observableArrayList());
        buildingEventsLog.setText("");
        
        servedCitiesListView.setItems(FXCollections.observableArrayList());
        servedIndustriesListView.setItems(FXCollections.observableArrayList());
        availableCargoListView.setItems(FXCollections.observableArrayList());
        requestedCargoListView.setItems(FXCollections.observableArrayList());
    }
    
    @FXML
    private void handleUpgradeStation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UpgradeStationDialog.fxml"));
            Parent dialogRoot = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Upgrade Station");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(upgradeButton.getScene().getWindow());
            
            Scene scene = new Scene(dialogRoot);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);
            
            dialogStage.showAndWait();
            
            // After upgrade dialog closes, refresh the station details
            int selectedIndex = stationsListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                updateStationDetails(stations.get(selectedIndex));
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", 
                "Could not open Upgrade Station dialog: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleClose() {
        ((Stage) upgradeButton.getScene().getWindow()).close();
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 