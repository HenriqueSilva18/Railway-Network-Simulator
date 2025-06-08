package pt.ipp.isep.dei.ui.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import pt.ipp.isep.dei.controller.template.ApplicationSession;
import pt.ipp.isep.dei.controller.template.ListTrainController;
import pt.ipp.isep.dei.domain.template.Station;
import pt.ipp.isep.dei.domain.template.Train;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class ListTrainsDialogController implements Initializable {
    @FXML private ListView<String> trainsListView;

    @FXML private Label trainNameLabel;
    @FXML private Label trainTypeLabel;
    @FXML private Label speedLabel;
    @FXML private Label capacityLabel;
    @FXML private Label fuelCostLabel;
    @FXML private Label routeLabel;

    @FXML private ListView<String> currentCargoListView;

    private final ListTrainController controller;
    private List<Train> trains;

    public ListTrainsDialogController() {
        this.controller = new ListTrainController();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        trains = controller.getAllTrains();
        trains.sort(Comparator.comparing(Train::getNameID));
        ObservableList<String> trainItems = FXCollections.observableArrayList();
        for (Train train : trains) {
            trainItems.add(train.getNameID() + " (" + train.getLocomotive().getNameID() + ")");
        }

        trainsListView.setItems(trainItems);


        trainsListView.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.intValue() >= 0) {
                updateTrainDetails(trains.get(newVal.intValue()));
            } else {
                clearTrainDetails();
            }
        });
    }

    private void updateTrainDetails(Train train) {
        trainNameLabel.setText(train.getNameID());
        trainTypeLabel.setText(train.getLocomotive().getType());
        speedLabel.setText(train.getLocomotive().getTopSpeed() + " km/h");
        capacityLabel.setText(String.valueOf(train.getCapacity()));
        fuelCostLabel.setText(train.getLocomotive().getFuelCost() + "");
        routeLabel.setText(controller.getRouteForTrain(train) != null ? controller.getRouteForTrain(train) : "No route assigned");

        ObservableList<String> cargoItems = FXCollections.observableArrayList();
        train.getCargo().forEach(cargo ->
                cargoItems.add(String.format("%s: %d units", cargo.getName(), cargo.getAmount())));
        currentCargoListView.setItems(cargoItems);
    }

    private void clearTrainDetails() {
        trainNameLabel.setText("");
        trainTypeLabel.setText("");
        speedLabel.setText("");
        capacityLabel.setText("");
        fuelCostLabel.setText("");
        routeLabel.setText("");
        currentCargoListView.getItems().clear();
    }

    @FXML
    private void handleClose() {
        ((Stage) trainsListView.getScene().getWindow()).close();
    }
}
