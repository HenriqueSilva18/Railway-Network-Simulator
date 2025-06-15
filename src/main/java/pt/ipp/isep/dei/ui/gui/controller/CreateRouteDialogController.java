package pt.ipp.isep.dei.ui.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pt.ipp.isep.dei.controller.template.CreateRouteController;
import pt.ipp.isep.dei.domain.template.*;
import pt.ipp.isep.dei.ui.gui.utils.AlertHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class CreateRouteDialogController {

    @FXML private TextField routeNameField;
    @FXML private ComboBox<Station> nextStationComboBox;
    @FXML private Button addStationButton;
    @FXML private ListView<VBox> routeStationsListView;
    @FXML private Button removeLastStationButton;
    @FXML private Button createButton;

    private final CreateRouteController businessController = new CreateRouteController();
    private final ObservableList<Station> currentRouteStations = FXCollections.observableArrayList();
    private final List<CargoMode> cargoModes = new ArrayList<>();

    @FXML
    public void initialize() {
        routeStationsListView.setItems(FXCollections.observableArrayList());
        loadInitialStations();
        updateState();
    }

    private void loadInitialStations() {
        List<Station> allStations = businessController.getAvailableStations();
        if (allStations.isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.INFORMATION,"No Stations", "No stations available on the map.");
        }
        nextStationComboBox.setItems(FXCollections.observableArrayList(allStations));
    }

    private void updateStationList() {
        ObservableList<VBox> items = FXCollections.observableArrayList();
        for (int i = 0; i < currentRouteStations.size(); i++) {
            Station station = currentRouteStations.get(i);
            CargoMode mode = cargoModes.get(i);
            
            VBox stationBox = new VBox(5);
            stationBox.setStyle("-fx-padding: 5; -fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 3;");
            
            // Station info row
            HBox stationInfoRow = new HBox(10);
            Label numberLabel = new Label(String.format("%d.", i + 1));
            numberLabel.setStyle("-fx-font-weight: bold;");
            
            Label stationLabel = new Label(station.getNameID());
            stationLabel.setStyle("-fx-font-weight: bold;");
            
            Label modeLabel = new Label(mode.toString());
            modeLabel.setStyle(getCargoModeStyle(mode));
            
            stationInfoRow.getChildren().addAll(numberLabel, stationLabel, modeLabel);
            
            // Cargo info section
            VBox cargoInfoBox = new VBox(2);
            cargoInfoBox.setStyle("-fx-padding: 5 0 0 20;");
            
            Label cargoTitleLabel = new Label("Available Cargo:");
            cargoTitleLabel.setStyle("-fx-font-weight: bold;");
            cargoInfoBox.getChildren().add(cargoTitleLabel);
            
            List<Cargo> availableCargo = station.getAvailableCargo();
            if (!availableCargo.isEmpty()) {
                // Group cargo by type and sum their amounts
                Map<String, Integer> cargoAmounts = new HashMap<>();
                for (Cargo cargo : availableCargo) {
                    cargoAmounts.merge(cargo.getName(), cargo.getAmount(), Integer::sum);
                }
                
                // Display grouped cargo with their total amounts
                for (Map.Entry<String, Integer> entry : cargoAmounts.entrySet()) {
                    Label cargoLabel = new Label(String.format("  %s: %d units", entry.getKey(), entry.getValue()));
                    cargoInfoBox.getChildren().add(cargoLabel);
                }
            } else {
                Label noCargoLabel = new Label("  No cargo available");
                noCargoLabel.setStyle("-fx-text-fill: gray;");
                cargoInfoBox.getChildren().add(noCargoLabel);
            }
            
            stationBox.getChildren().addAll(stationInfoRow, cargoInfoBox);
            items.add(stationBox);
        }
        routeStationsListView.setItems(items);
    }

    private String getCargoModeStyle(CargoMode mode) {
        String color;
        switch (mode) {
            case FULL:
                color = "#4CAF50";
                break;
            case HALF:
                color = "#2196F3";
                break;
            case AVAILABLE:
                color = "#FF9800";
                break;
            default:
                color = "#000000";
        }
        return String.format("-fx-text-fill: %s; -fx-font-weight: bold; -fx-padding: 3 8; -fx-background-color: %s20; -fx-border-color: %s; -fx-border-width: 1; -fx-border-radius: 3;", 
            color, color, color);
    }

    private CargoMode showCargoModeDialog(Station station) {
        Dialog<CargoMode> dialog = new Dialog<>();
        dialog.setTitle("Select Cargo Mode");
        dialog.setHeaderText("Select cargo mode for station: " + station.getNameID());

        // Create a VBox to hold the description and buttons
        VBox content = new VBox(15);
        content.setPadding(new javafx.geometry.Insets(20));
        content.setStyle("-fx-background-color: white;");

        // Create styled labels for each option
        VBox fullBox = createOptionBox("FULL", 
            "The train will wait at this station until all carriages are fully loaded before departing",
            "#4CAF50"); // Green color

        VBox halfBox = createOptionBox("HALF",
            "The train will depart from this station as soon as at least 50% of its carriages are loaded",
            "#2196F3"); // Blue color

        VBox availableBox = createOptionBox("AVAILABLE",
            "The train will depart from this station with whatever cargo is currently available, regardless of capacity",
            "#FF9800"); // Orange color

        content.getChildren().addAll(fullBox, halfBox, availableBox);
        dialog.getDialogPane().setContent(content);

        // Style the dialog
        dialog.getDialogPane().setStyle("-fx-background-color: white;");
        dialog.getDialogPane().getScene().getWindow().setOnShown(e -> {
            dialog.getDialogPane().getScene().getWindow().sizeToScene();
        });

        ButtonType fullButton = new ButtonType("FULL", ButtonBar.ButtonData.OK_DONE);
        ButtonType halfButton = new ButtonType("HALF", ButtonBar.ButtonData.OK_DONE);
        ButtonType availableButton = new ButtonType("AVAILABLE", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(fullButton, halfButton, availableButton, cancelButton);

        // Style the buttons
        dialog.getDialogPane().lookupButton(fullButton).setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        dialog.getDialogPane().lookupButton(halfButton).setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        dialog.getDialogPane().lookupButton(availableButton).setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        dialog.getDialogPane().lookupButton(cancelButton).setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == fullButton) return CargoMode.FULL;
            if (dialogButton == halfButton) return CargoMode.HALF;
            if (dialogButton == availableButton) return CargoMode.AVAILABLE;
            return null;
        });

        return dialog.showAndWait().orElse(CargoMode.AVAILABLE);
    }

    private VBox createOptionBox(String title, String description, String color) {
        VBox box = new VBox(5);
        box.setPadding(new javafx.geometry.Insets(10));
        box.setStyle("-fx-background-color: " + color + "20; -fx-border-color: " + color + "; -fx-border-width: 2; -fx-border-radius: 5;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        Label descLabel = new Label(description);
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-font-size: 12px;");

        box.getChildren().addAll(titleLabel, descLabel);
        return box;
    }

    @FXML private void handleAddStation() {
        Station selected = nextStationComboBox.getValue();
        if (selected != null) {
            CargoMode mode = showCargoModeDialog(selected);
            if (mode != null) {
                currentRouteStations.add(selected);
                cargoModes.add(mode);
                updateNextStationChoices();
                updateStationList();
                updateState();
            }
        }
    }

    @FXML private void handleRemoveLastStation() {
        if (!currentRouteStations.isEmpty()) {
            currentRouteStations.remove(currentRouteStations.size() - 1);
            cargoModes.remove(cargoModes.size() - 1);
            updateNextStationChoices();
            updateStationList();
            updateState();
        }
    }

    private void updateNextStationChoices() {
        if (currentRouteStations.isEmpty()) {
            loadInitialStations();
        } else {
            Station lastStation = currentRouteStations.get(currentRouteStations.size() - 1);
            List<Station> connected = businessController.getConnectedStations(lastStation);
            connected.removeAll(currentRouteStations);
            nextStationComboBox.setItems(FXCollections.observableArrayList(connected));
        }
        nextStationComboBox.getSelectionModel().clearSelection();
    }

    @FXML public void updateState() {
        removeLastStationButton.setDisable(currentRouteStations.isEmpty());
        addStationButton.setDisable(nextStationComboBox.getItems().isEmpty());

        boolean hasName = routeNameField.getText() != null && !routeNameField.getText().trim().isEmpty();
        boolean hasEnoughStations = currentRouteStations.size() >= 2;
        createButton.setDisable(!hasName || !hasEnoughStations);
    }

    @FXML private void handleCreateRoute() {
        String routeName = routeNameField.getText().trim();
        List<Station> stations = new ArrayList<>(currentRouteStations);

        if (businessController.createRoute(routeName, stations, cargoModes) != null) {
            AlertHelper.showAlert(Alert.AlertType.CONFIRMATION,"Success", "Route '" + routeName + "' created!");
            closeWindow();
        } else {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Creation Failed", "Could not create route. Check if all stations are sequentially connected.");
        }
    }

    @FXML private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        ((Stage) createButton.getScene().getWindow()).close();
    }
}