package pt.ipp.isep.dei.ui.gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pt.ipp.isep.dei.controller.template.ApplicationSession;
import pt.ipp.isep.dei.controller.template.StationBuildingController;
import pt.ipp.isep.dei.domain.template.*;

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

    private final StationBuildingController controller;
    private StationType selectedType;
    private String suggestedName;

    public BuildStationDialogController() {
        this.controller = new StationBuildingController();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupStationTypeList();
        setupSpinners();
        setupCenterPointComboBox();
        setupCustomNameControls();
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
        SpinnerValueFactory.IntegerSpinnerValueFactory xFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0);
        SpinnerValueFactory.IntegerSpinnerValueFactory yFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0);
        
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

    private void updateBudgetLabels() {
        Player currentPlayer = ApplicationSession.getInstance().getCurrentPlayer();
        if (currentPlayer != null) {
            double currentBudget = currentPlayer.getCurrentBudget();
            currentBudgetLabel.setText(String.format("Current Budget: %.2f", currentBudget));
            
            if (selectedType != null) {
                double remainingBudget = currentBudget - selectedType.getCost();
                remainingBudgetLabel.setText(String.format("Remaining Budget After Build: %.2f", remainingBudget));
            }
        }
    }

    @FXML
    private void handlePreview() {
        clearError();
        
        if (selectedType == null) {
            showError("Please select a station type.");
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

        Position position = new Position(xCoordSpinner.getValue(), yCoordSpinner.getValue());
        String centerPoint = selectedType.requiresCenterPoint() ? centerPointComboBox.getValue() : null;

        if (controller.buildStation(stationName, selectedType, position, centerPoint)) {
            updateBudgetLabels();
            showAlert("Success", "Station built successfully!");
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