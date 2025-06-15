package pt.ipp.isep.dei.ui.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.ListCell;
import javafx.stage.Stage;
import pt.ipp.isep.dei.domain.template.Train;
import pt.ipp.isep.dei.domain.template.Route;
import pt.ipp.isep.dei.domain.template.Carriage;
import pt.ipp.isep.dei.controller.template.AssignTrainController;
import pt.ipp.isep.dei.controller.template.AssignmentStatus;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AssignTrainToRouteDialogController implements Initializable {
    @FXML private ComboBox<Train> trainComboBox;
    @FXML private ComboBox<Route> routeComboBox;
    @FXML private Button assignButton;
    @FXML private Button cancelButton;
    @FXML private Spinner<Integer> carriageCountSpinner;
    @FXML private Label errorLabel;

    private final AssignTrainController controller;
    private final List<Train> trains;
    private final List<Route> routes;
    private boolean successful = false;
    private Route selectedRoute;

    public AssignTrainToRouteDialogController() {
        this.controller = new AssignTrainController();
        this.trains = controller.getAvailableTrains();
        this.routes = controller.getAvailableRoutes();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize train combo box
        trainComboBox.setItems(FXCollections.observableArrayList(trains));
        trainComboBox.setCellFactory(param -> new ListCell<Train>() {
            @Override
            protected void updateItem(Train train, boolean empty) {
                super.updateItem(train, empty);
                if (empty || train == null) {
                    setText(null);
                } else {
                    setText(String.format("%s (Locomotive: %s)", train.getNameID(), train.getLocomotive().getType()));
                }
            }
        });
        trainComboBox.setButtonCell(new ListCell<Train>() {
            @Override
            protected void updateItem(Train train, boolean empty) {
                super.updateItem(train, empty);
                if (empty || train == null) {
                    setText(null);
                } else {
                    setText(String.format("%s (Locomotive: %s)", train.getNameID(), train.getLocomotive().getType()));
                }
            }
        });

        // Initialize route combo box
        routeComboBox.setItems(FXCollections.observableArrayList(routes));
        routeComboBox.setCellFactory(param -> new ListCell<Route>() {
            @Override
            protected void updateItem(Route route, boolean empty) {
                super.updateItem(route, empty);
                if (empty || route == null) {
                    setText(null);
                } else {
                    setText(String.format("%s (%d stations)", route.getNameID(), route.getStationSequence().size()));
                }
            }
        });

        // Initialize carriage count spinner
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9, 1);
        carriageCountSpinner.setValueFactory(valueFactory);

        // Add listeners to enable/disable assign button
        ListChangeListener<Object> selectionListener = change -> validateInput();
        trainComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> validateInput());
        routeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> validateInput());
        carriageCountSpinner.valueProperty().addListener((obs, oldVal, newVal) -> validateInput());

        // Initial validation
        validateInput();
    }

    private void validateInput() {
        boolean isValid = trainComboBox.getValue() != null && 
                         routeComboBox.getValue() != null && 
                         carriageCountSpinner.getValue() != null;
        assignButton.setDisable(!isValid);
        errorLabel.setText("");
    }

    @FXML
    private void handleAssign() {
        Train selectedTrain = trainComboBox.getValue();
        selectedRoute = routeComboBox.getValue();
        int carriageCount = carriageCountSpinner.getValue();

        if (selectedTrain == null || selectedRoute == null) {
            errorLabel.setText("Please select both a train and a route.");
            return;
        }

        // Add carriages to the train
        for (int i = 0; i < carriageCount; i++) {
            Carriage carriage = new Carriage(20); // Storage capacity of 20 units
            selectedTrain.addCarriage(carriage);
        }

        // Assign train to route
        AssignmentStatus status = controller.assignTrainToRoute(selectedRoute, selectedTrain);

        if (status == AssignmentStatus.SUCCESS) {
            // Just close the dialog without setting successful to true
            closeDialog();
        } else {
            String errorMessage;
            switch (status) {
                case ROUTE_ALREADY_HAS_TRAIN:
                    errorMessage = "Assignment failed: This route already has a train assigned.";
                    break;
                case TRAIN_ALREADY_ASSIGNED:
                    errorMessage = "Assignment failed: This train is already assigned to another route.";
                    break;
                case INVALID_INPUT:
                default:
                    errorMessage = "An unexpected error occurred. Please check your selection and try again.";
                    break;
            }
            errorLabel.setText(errorMessage);
        }
    }

    public boolean isSuccessful() {
        return successful;
    }

    public Route getSelectedRoute() {
        return selectedRoute;
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        ((Stage) trainComboBox.getScene().getWindow()).close();
    }
} 