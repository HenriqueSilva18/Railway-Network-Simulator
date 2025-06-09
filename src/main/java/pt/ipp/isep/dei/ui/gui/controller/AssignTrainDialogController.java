package pt.ipp.isep.dei.ui.gui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import pt.ipp.isep.dei.controller.template.AssignTrainController;
import pt.ipp.isep.dei.controller.template.AssignmentStatus;
import pt.ipp.isep.dei.domain.template.Route;
import pt.ipp.isep.dei.domain.template.Train;
import pt.ipp.isep.dei.ui.gui.utils.AlertHelper;

public class AssignTrainDialogController {

    @FXML private ComboBox<Route> routeComboBox;
    @FXML private ComboBox<Train> trainComboBox;
    @FXML private Button assignButton;

    private final AssignTrainController businessController = new AssignTrainController();
    private boolean successful = false;
    private Route selectedRoute;

    @FXML
    public void initialize() {
        loadData();
        assignButton.setDisable(true);
        routeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> validateSelection());
        trainComboBox.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> validateSelection());
    }

    private void loadData() {
        routeComboBox.setItems(FXCollections.observableArrayList(businessController.getAvailableRoutes()));
        trainComboBox.setItems(FXCollections.observableArrayList(businessController.getAvailableTrains()));
    }

    private void validateSelection() {
        assignButton.setDisable(routeComboBox.getValue() == null || trainComboBox.getValue() == null);
    }

    @FXML
    private void handleAssign() {
        selectedRoute = routeComboBox.getValue();
        Train selectedTrain = trainComboBox.getValue();

        // Chama o método atualizado do controlador de negócio
        AssignmentStatus status = businessController.assignTrainToRoute(selectedRoute, selectedTrain);

        if (status == AssignmentStatus.SUCCESS) {
            this.successful = true;
            closeWindow();
            return; // Sai do método em caso de sucesso
        }

        // Se chegou aqui, houve um erro. Define a mensagem apropriada.
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

        // Mostra o alerta de erro
        this.successful = false;
        AlertHelper.showAlert(Alert.AlertType.ERROR,"Assignment Failed", errorMessage);
    }

    public boolean isSuccessful() { return successful; }
    public Route getSelectedRoute() { return selectedRoute; }

    @FXML private void handleCancel() { closeWindow(); }
    private void closeWindow() { ((Stage) assignButton.getScene().getWindow()).close(); }
}