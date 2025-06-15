package pt.ipp.isep.dei.ui.gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pt.ipp.isep.dei.controller.template.ApplicationSession;
import pt.ipp.isep.dei.controller.template.StationBuildingController;
import pt.ipp.isep.dei.domain.template.*;
import pt.ipp.isep.dei.repository.template.Repositories;
import pt.ipp.isep.dei.repository.template.BuildingRepository;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class BuildStationDialogController implements Initializable {
    @FXML
    private ListView<StationType> stationTypeList;
    @FXML
    private Spinner<Integer> xCoordSpinner;
    @FXML
    private Spinner<Integer> yCoordSpinner;
    @FXML
    private VBox centerPointBox;
    @FXML
    private ComboBox<String> centerPointComboBox;
    @FXML
    private Label suggestedNameLabel;
    @FXML
    private CheckBox useCustomNameCheckbox;
    @FXML
    private TextField customNameField;
    @FXML
    private Label typeDetailsLabel;
    @FXML
    private Label costLabel;
    @FXML
    private Label radiusLabel;
    @FXML
    private Label currentBudgetLabel;
    @FXML
    private Label remainingBudgetLabel;
    @FXML
    private Label errorMessageLabel;
    @FXML
    private Button buildButton;
    @FXML
    private ListView<Building> initialBuildingList;
    @FXML
    private Label buildingNameLabel;
    @FXML
    private Label buildingTypeLabel;
    @FXML
    private Label buildingCostLabel;
    @FXML
    private Label buildingEffectLabel;

    private final StationBuildingController controller;
    private final BuildingRepository buildingRepository;
    private StationType selectedType;
    private String suggestedName;
    private Building selectedBuilding;

    public BuildStationDialogController() {
        this.controller = new StationBuildingController();
        this.buildingRepository = Repositories.getInstance().getBuildingRepository();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupStationTypeList();
        setupSpinners();
        setupCenterPointComboBox();
        setupCustomNameControls();
        setupBuildingList();
        setupListeners();
        updateBudgetLabels();
        clearError();
    }

    private void setupStationTypeList() {
        List<StationType> stationTypes = controller.getStationTypes();
        stationTypeList.getItems().addAll(stationTypes);
        stationTypeList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(StationType type, boolean empty) {
                super.updateItem(type, empty);
                if (empty || type == null) {
                    setText(null);
                } else {
                    setText(String.format("%s (Cost: %.2f, Radius: %d)",
                            type.getName(), type.getCost(), type.getEconomicRadius()));
                }
            }
        });
    }

    private void setupSpinners() {
        Map currentMap = controller.getCurrentMap();
        if (currentMap == null) {
            return;
        }

        int maxX = currentMap.getSize().getWidth() - 1;
        int maxY = currentMap.getSize().getHeight() - 1;

        SpinnerValueFactory.IntegerSpinnerValueFactory xFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, maxX, 0);
        SpinnerValueFactory.IntegerSpinnerValueFactory yFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, maxY, 0);
        
        xCoordSpinner.setValueFactory(xFactory);
        yCoordSpinner.setValueFactory(yFactory);
    }

    private void setupCenterPointComboBox() {
        centerPointComboBox.getItems().addAll("NE", "SE", "NW", "SW");
        centerPointComboBox.setValue("NE");
    }

    private void setupCustomNameControls() {
        useCustomNameCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> 
            customNameField.setDisable(!newVal));
    }

    private void setupBuildingList() {
        List<Building> availableBuildings = buildingRepository.getNewBuildingOptions();
        if (availableBuildings.isEmpty()) {
            showError("No buildings available. Please check if buildings are properly initialized.");
            return;
        }

        // Sort buildings by cost for better user experience
        availableBuildings.sort((b1, b2) -> Double.compare(b1.getCost(), b2.getCost()));
        
        initialBuildingList.getItems().addAll(availableBuildings);
        
        initialBuildingList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Building building, boolean empty) {
                super.updateItem(building, empty);
                if (empty || building == null) {
                    setText(null);
                } else {
                    setText(String.format("%s - %s (Cost: %.2f)",
                            building.getNameID(),
                            building.getType(),
                            building.getCost()));
                }
            }
        });

        // Select the first building by default
        if (!availableBuildings.isEmpty()) {
            initialBuildingList.getSelectionModel().select(0);
        }

        initialBuildingList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedBuilding = newVal;
            updateBuildingDetails();
            updateBudgetLabels();
        });
    }

    private void setupListeners() {
        stationTypeList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedType = newVal;
            updateStationDetails();
            centerPointBox.setVisible(selectedType != null && selectedType.requiresCenterPoint());
        });
    }

    private void updateStationDetails() {
        if (selectedType != null) {
            typeDetailsLabel.setText("Type: " + selectedType.getName());
            costLabel.setText(String.format("Cost: %.2f", selectedType.getCost()));
            radiusLabel.setText("Economic Radius: " + selectedType.getEconomicRadius());
            updateBudgetLabels();
        }
    }

    private void updateBuildingDetails() {
        if (selectedBuilding != null) {
            buildingNameLabel.setText("Name: " + selectedBuilding.getNameID());
            buildingTypeLabel.setText("Type: " + selectedBuilding.getType());
            buildingCostLabel.setText(String.format("Cost: %.2f", selectedBuilding.getCost()));
            buildingEffectLabel.setText("Effect: " + selectedBuilding.getEffect());
        } else {
            buildingNameLabel.setText("");
            buildingTypeLabel.setText("");
            buildingCostLabel.setText("");
            buildingEffectLabel.setText("");
        }
    }

    private void updateBudgetLabels() {
        Player currentPlayer = ApplicationSession.getInstance().getCurrentPlayer();
        if (currentPlayer == null) {
            showError("No player selected.");
            return;
        }

        double currentBudget = currentPlayer.getCurrentBudget();
        double stationCost = selectedType != null ? selectedType.getCost() : 0;
        double buildingCost = selectedBuilding != null ? selectedBuilding.getCost() : 0;
        double totalCost = stationCost + buildingCost;
        double remainingBudget = currentBudget - totalCost;

        currentBudgetLabel.setText(String.format("Current Budget: %.2f", currentBudget));
        remainingBudgetLabel.setText(String.format("Remaining Budget: %.2f", remainingBudget));
        
        // Update build button state
        buildButton.setDisable(selectedType == null || selectedBuilding == null || remainingBudget < 0);
        
        // Update labels color based on budget
        if (remainingBudget < 0) {
            remainingBudgetLabel.setStyle("-fx-text-fill: red;");
            showError("Insufficient budget for station and building.");
        } else {
            remainingBudgetLabel.setStyle("-fx-text-fill: black;");
            clearError();
        }
    }

    @FXML
    private void handlePreview() {
        clearError();
        
        if (selectedType == null) {
            showError("Please select a station type.");
            return;
        }

        if (selectedBuilding == null) {
            showError("Please select an initial building.");
            return;
        }

        Position position = new Position(xCoordSpinner.getValue(), yCoordSpinner.getValue());
        String centerPoint = selectedType.requiresCenterPoint() ? centerPointComboBox.getValue() : null;

        if (!controller.validatePosition(position)) {
            showError("Invalid position. Position is either occupied or out of bounds.");
            return;
        }

        if (!controller.previewStationPlacement(selectedType, position, centerPoint)) {
            showError("Cannot place station at this location.");
            return;
        }

        // Get suggested name
        City closestCity = controller.getClosestCity(position);
        if (closestCity == null) {
            showError("No nearby city found.");
            return;
        }

        String baseName = closestCity.getNameID() + " " + selectedType.getName();
        suggestedName = baseName;
        int count = 1;

        while (controller.isStationNameTaken(suggestedName)) {
            count++;
            suggestedName = baseName + " " + count;
        }

        suggestedNameLabel.setText("Suggested Name: " + suggestedName);
        buildButton.setDisable(false);
    }

    @FXML
    private void handleBuild() {
        String stationName = useCustomNameCheckbox.isSelected() ? 
            customNameField.getText() : suggestedName;

        try {
            stationName = controller.validateStationName(stationName);
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
            return;
        }

        if (selectedBuilding == null) {
            showError("Please select an initial building.");
            return;
        }

        Position position = new Position(xCoordSpinner.getValue(), yCoordSpinner.getValue());
        String centerPoint = selectedType.requiresCenterPoint() ? centerPointComboBox.getValue() : null;

        if (controller.buildStation(stationName, selectedType, position, centerPoint, selectedBuilding)) {
            updateBudgetLabels();
            showAlert("Success", "Station built successfully with initial building!");
            closeDialog();
        } else {
            showError("Failed to build station. Please check your budget and try again.");
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void showError(String message) {
        errorMessageLabel.setText(message);
    }

    private void clearError() {
        errorMessageLabel.setText("");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeDialog() {
        ((Stage) buildButton.getScene().getWindow()).close();
    }
} 