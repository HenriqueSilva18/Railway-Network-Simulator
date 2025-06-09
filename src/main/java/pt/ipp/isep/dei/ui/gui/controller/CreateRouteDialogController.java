package pt.ipp.isep.dei.ui.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pt.ipp.isep.dei.controller.template.CreateRouteController;
import pt.ipp.isep.dei.domain.template.Station;
import pt.ipp.isep.dei.ui.gui.utils.AlertHelper;
import java.util.ArrayList;
import java.util.List;

public class CreateRouteDialogController {

    @FXML private TextField routeNameField;
    @FXML private ComboBox<Station> nextStationComboBox;
    @FXML private Button addStationButton;
    @FXML private ListView<Station> routeStationsListView;
    @FXML private Button removeLastStationButton;
    @FXML private Button createButton;

    private final CreateRouteController businessController = new CreateRouteController();
    private final ObservableList<Station> currentRouteStations = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        routeStationsListView.setItems(currentRouteStations);
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

    @FXML private void handleAddStation() {
        Station selected = nextStationComboBox.getValue();
        if (selected != null) {
            currentRouteStations.add(selected);
            updateNextStationChoices();
            updateState();
        }
    }

    @FXML private void handleRemoveLastStation() {
        if (!currentRouteStations.isEmpty()) {
            currentRouteStations.remove(currentRouteStations.size() - 1);
            updateNextStationChoices();
            updateState();
        }
    }

    private void updateNextStationChoices() {
        if (currentRouteStations.isEmpty()) {
            loadInitialStations();
        } else {
            Station lastStation = currentRouteStations.get(currentRouteStations.size() - 1);
            List<Station> connected = businessController.getConnectedStations(lastStation);
            connected.removeAll(currentRouteStations); // Evita adicionar a mesma estação em seguida
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

        if (businessController.createRoute(routeName, stations) != null) {
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