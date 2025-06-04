package pt.ipp.isep.dei.ui.gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pt.ipp.isep.dei.controller.template.ApplicationSession;
import pt.ipp.isep.dei.controller.template.BuildRailwayLineController;
import pt.ipp.isep.dei.domain.template.Station;
import pt.ipp.isep.dei.domain.template.RailwayLine;
import pt.ipp.isep.dei.domain.template.Player;
import pt.ipp.isep.dei.domain.template.Position;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class BuildRailwayLineDialogController implements Initializable {
    @FXML
    private ListView<String> firstStationListView;
    @FXML
    private ListView<String> secondStationListView;
    
    @FXML
    private Label firstStationDetailsLabel;
    @FXML
    private Label secondStationDetailsLabel;
    @FXML
    private Label railwayLineDetailsLabel;
    @FXML
    private Label lengthLabel;
    @FXML
    private Label costLabel;
    @FXML
    private Label budgetLabel;
    
    @FXML
    private Button buildButton;
    
    private final BuildRailwayLineController controller;
    private List<Station> stations;
    private Station selectedFirstStation;
    private Station selectedSecondStation;
    
    public BuildRailwayLineDialogController() {
        this.controller = new BuildRailwayLineController();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Get available stations
        stations = controller.getAvailableStations();
        
        if (stations.size() < 2) {
            showAlert(Alert.AlertType.WARNING, "Not Enough Stations", 
                "You need at least 2 stations to build a railway line.");
            buildButton.setDisable(true);
            return;
        }
        
        // Populate first station list
        ObservableList<String> firstStationItems = FXCollections.observableArrayList();
        for (Station station : stations) {
            firstStationItems.add(station.getNameID());
        }
        firstStationListView.setItems(firstStationItems);
        
        // Add selection listeners
        firstStationListView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    handleFirstStationSelection(newValue);
                }
            }
        );
        
        secondStationListView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    handleSecondStationSelection(newValue);
                }
            }
        );
    }
    
    private void handleFirstStationSelection(String stationName) {
        // Find selected station
        selectedFirstStation = stations.stream()
            .filter(s -> s.getNameID().equals(stationName))
            .findFirst()
            .orElse(null);
        
        if (selectedFirstStation != null) {
            // Update first station details
            firstStationDetailsLabel.setText(String.format("Selected: %s", selectedFirstStation.getNameID()));
            
            // Update second station list to exclude the selected station
            List<Station> remainingStations = stations.stream()
                .filter(s -> !s.equals(selectedFirstStation))
                .collect(Collectors.toList());
            
            ObservableList<String> secondStationItems = FXCollections.observableArrayList();
            for (Station station : remainingStations) {
                secondStationItems.add(station.getNameID());
            }
            secondStationListView.setItems(secondStationItems);
            
            // Clear second station selection
            secondStationListView.getSelectionModel().clearSelection();
            secondStationDetailsLabel.setText("");
            selectedSecondStation = null;
            
            updateRailwayLineDetails();
        }
    }
    
    private void handleSecondStationSelection(String stationName) {
        // Find selected station
        selectedSecondStation = stations.stream()
            .filter(s -> s.getNameID().equals(stationName))
            .findFirst()
            .orElse(null);
        
        if (selectedSecondStation != null) {
            // Update second station details
            secondStationDetailsLabel.setText(String.format("Selected: %s", selectedSecondStation.getNameID()));
            
            updateRailwayLineDetails();
        }
    }
    
    private void updateRailwayLineDetails() {
        // Update railway line details between selected stations
        if (selectedFirstStation != null && selectedSecondStation != null) {
            StringBuilder details = new StringBuilder();
            details.append("Railway Line Details:\n");
            details.append(String.format("From: %s\n", selectedFirstStation.getNameID()));
            details.append(String.format("To: %s", selectedSecondStation.getNameID()));
            
            railwayLineDetailsLabel.setText(details.toString());
            
            // Find path and calculate cost
            List<Position> path = controller.findDirectPath(selectedFirstStation, selectedSecondStation);
            if (path != null && !path.isEmpty()) {
                double estimatedLength = calculateLength(path);
                double estimatedCost = controller.calculatePathCost(path);
                
                lengthLabel.setText(String.format("%.2f units", estimatedLength));
                costLabel.setText(String.format("$%.2f", estimatedCost));
                
                // Update budget display
                Player currentPlayer = ApplicationSession.getInstance().getCurrentPlayer();
                if (currentPlayer != null) {
                    budgetLabel.setText(String.format("$%.2f", currentPlayer.getCurrentBudget()));
                    
                    // Enable build button only if player can afford it
                    buildButton.setDisable(currentPlayer.getCurrentBudget() < estimatedCost);
                    
                    if (currentPlayer.getCurrentBudget() < estimatedCost) {
                        showAlert(Alert.AlertType.WARNING, "Insufficient Funds", 
                            String.format("You need $%.2f more to build this railway line.", 
                                estimatedCost - currentPlayer.getCurrentBudget()));
                    }
                } else {
                    budgetLabel.setText("N/A");
                    buildButton.setDisable(true);
                }
            } else {
                lengthLabel.setText("N/A");
                costLabel.setText("N/A");
                budgetLabel.setText("N/A");
                buildButton.setDisable(true);
                showAlert(Alert.AlertType.WARNING, "Invalid Path", 
                    "Could not find a valid path between the selected stations.");
            }
        } else {
            railwayLineDetailsLabel.setText("");
            lengthLabel.setText("N/A");
            costLabel.setText("N/A");
            budgetLabel.setText("N/A");
            buildButton.setDisable(true);
        }
    }
    
    private double calculateLength(List<Position> path) {
        double length = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            Position current = path.get(i);
            Position next = path.get(i + 1);
            
            // Calculate distance between points
            int dx = next.getX() - current.getX();
            int dy = next.getY() - current.getY();
            
            // If diagonal movement, use diagonal distance (√2)
            if (dx != 0 && dy != 0) {
                length += Math.sqrt(2);
            } else {
                length += 1;
            }
        }
        return length;
    }
    
    @FXML
    private void handleBuild() {
        if (selectedFirstStation == null || selectedSecondStation == null) {
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Railway Line Construction");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Do you want to build this railway line?");
        
        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            RailwayLine builtLine = controller.buildRailwayLine(selectedFirstStation, selectedSecondStation);
            
            if (builtLine != null) {
                showAlert(Alert.AlertType.INFORMATION, "Success", 
                    String.format("Railway line built successfully!\nLength: %.2f units", builtLine.getLength()));
                getStage().close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", 
                    "Failed to build railway line. This could be due to:\n" +
                    "- Railway line already exists\n" +
                    "- Insufficient funds\n" +
                    "- No valid path available");
            }
        }
    }
    
    @FXML
    private void handleCancel() {
        getStage().close();
    }
    
    private Stage getStage() {
        return (Stage) buildButton.getScene().getWindow();
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 