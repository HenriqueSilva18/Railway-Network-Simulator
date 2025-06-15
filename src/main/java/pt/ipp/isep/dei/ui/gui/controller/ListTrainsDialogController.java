package pt.ipp.isep.dei.ui.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pt.ipp.isep.dei.controller.template.ApplicationSession;
import pt.ipp.isep.dei.controller.template.ListTrainController;
import pt.ipp.isep.dei.domain.template.*;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class ListTrainsDialogController implements Initializable {
    @FXML private ListView<VBox> trainsListView;

    private final ListTrainController controller;

    public ListTrainsDialogController() {
        this.controller = new ListTrainController();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateTrainList();
    }

    private void updateTrainList() {
        List<Train> trains = controller.getAllTrains();
        trains.sort(Comparator.comparing(Train::getNameID));
        ObservableList<VBox> items = FXCollections.observableArrayList();

        for (Train train : trains) {
            VBox trainBox = new VBox(5);
            trainBox.setPadding(new Insets(10));
            trainBox.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5;");

            // Train header with name and status
            HBox headerBox = new HBox(10);
            headerBox.setAlignment(Pos.CENTER_LEFT);
            Label nameLabel = new Label(train.getNameID());
            nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            Label statusLabel = new Label(train.isInTransit() ? "In Transit" : (train.isAssignedToRoute() ? "Assigned" : "Available"));
            statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
            headerBox.getChildren().addAll(nameLabel, statusLabel);
            trainBox.getChildren().add(headerBox);

            // Locomotive information
            Locomotive locomotive = train.getLocomotive();
            if (locomotive != null) {
                VBox locoBox = new VBox(5);
                locoBox.setPadding(new Insets(5));
                locoBox.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e9ecef; -fx-border-radius: 3;");
                
                Label locoTitle = new Label("Locomotive: " + locomotive.getNameID());
                locoTitle.setStyle("-fx-font-weight: bold;");
                
                Label locoDetails = new Label(String.format("Type: %s, Power: %d, Max Speed: %d km/h, Fuel Cost: %.2f",
                    locomotive.getType(),
                    locomotive.getPower(),
                    locomotive.getTopSpeed(),
                    locomotive.getFuelCost()));
                
                locoBox.getChildren().addAll(locoTitle, locoDetails);
                trainBox.getChildren().add(locoBox);
            }

            // Carriages information
            List<Carriage> carriages = train.getCarriages();
            if (!carriages.isEmpty()) {
                VBox carriagesBox = new VBox(5);
                carriagesBox.setPadding(new Insets(5));
                carriagesBox.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e9ecef; -fx-border-radius: 3;");
                
                Label carriagesTitle = new Label("Carriages (" + carriages.size() + "):");
                carriagesTitle.setStyle("-fx-font-weight: bold;");
                carriagesBox.getChildren().add(carriagesTitle);

                for (Carriage carriage : carriages) {
                    HBox carriageBox = new HBox(10);
                    carriageBox.setPadding(new Insets(3));
                    
                    // Carriage number and capacity
                    Label carriageInfo = new Label(String.format("Carriage %d - Capacity: %d units",
                        carriages.indexOf(carriage) + 1,
                        carriage.getStorageCapacity()));
                    
                    // Current cargo if any
                    if (carriage.getCargo() != null) {
                        Label cargoInfo = new Label(String.format("Current Cargo: %s (%d units)",
                            carriage.getCargo().getName(),
                            carriage.getCargo().getAmount()));
                        cargoInfo.setStyle("-fx-text-fill: #28a745;");
                        carriageBox.getChildren().addAll(carriageInfo, cargoInfo);
                    } else {
                        carriageBox.getChildren().add(carriageInfo);
                    }
                    
                    carriagesBox.getChildren().add(carriageBox);
                }
                trainBox.getChildren().add(carriagesBox);
            }

            // Route information if assigned
            if (train.getAssignedRoute() != null) {
                Label routeLabel = new Label("Assigned to Route: " + train.getAssignedRoute().getNameID());
                routeLabel.setStyle("-fx-font-style: italic;");
                trainBox.getChildren().add(routeLabel);
            }

            items.add(trainBox);
        }

        trainsListView.setItems(items);
    }

    @FXML
    private void handleClose() {
        ((Stage) trainsListView.getScene().getWindow()).close();
    }
}
