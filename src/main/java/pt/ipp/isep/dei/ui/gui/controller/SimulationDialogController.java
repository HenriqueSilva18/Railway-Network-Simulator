package pt.ipp.isep.dei.ui.gui.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import pt.ipp.isep.dei.controller.template.ApplicationSession;
import pt.ipp.isep.dei.controller.template.SimulatorController;
import pt.ipp.isep.dei.domain.template.*;
import pt.ipp.isep.dei.repository.template.Repositories;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class SimulationDialogController implements Initializable {

    @FXML private Label statusLabel;
    @FXML private Label dateLabel;
    @FXML private Label budgetLabel;
    @FXML private Button pauseResumeButton;
    @FXML private Button restartButton;
    @FXML private Button generateCargoButton;
    @FXML private Button viewCargoButton;
    @FXML private Button stopButton;
    @FXML private Button closeButton;

    private SimulatorController simulatorController;
    private Timeline simulationUpdateTimer;
    private ApplicationSession appSession;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.simulatorController = new SimulatorController();
        this.appSession = ApplicationSession.getInstance();

        setupSimulationUpdateTimer();
        simulationUpdateTimer.play();
    }

    private void setupSimulationUpdateTimer() {
        simulationUpdateTimer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            Platform.runLater(this::updateUI);
        }));
        simulationUpdateTimer.setCycleCount(Timeline.INDEFINITE);
    }

    private void updateUI() {
        String status = simulatorController.getSimulatorStatus();

        if (status == null || status.equals(Simulator.STATUS_STOPPED)) {
            statusLabel.setText(status == null ? "Not initiated" : "Stopped");
            dateLabel.setText("N/A");

            Player currentPlayer = appSession.getCurrentPlayer();
            if (currentPlayer != null) {
                budgetLabel.setText(String.format("$%.2f", currentPlayer.getCurrentBudget()));
            } else {
                budgetLabel.setText("N/A");
            }

            pauseResumeButton.setDisable(true);
            restartButton.setDisable(status == null);
            generateCargoButton.setDisable(true);
            viewCargoButton.setDisable(true);
            stopButton.setDisable(true);

        } else { // Running or Paused
            Simulator simulator = Repositories.getInstance().getSimulatorRepository().getActiveSimulator();
            if (simulator != null) {
                statusLabel.setText(status);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                dateLabel.setText(sdf.format(simulator.getCurrentSimulatedDate()));
                Player currentPlayer = appSession.getCurrentPlayer();
                if (currentPlayer != null) {
                    budgetLabel.setText(String.format("$%.2f", currentPlayer.getCurrentBudget()));
                }

                boolean isRunning = status.equals(Simulator.STATUS_RUNNING);
                pauseResumeButton.setText(isRunning ? "Pause" : "Resume");

                pauseResumeButton.setDisable(false);
                restartButton.setDisable(false);
                generateCargoButton.setDisable(false);
                viewCargoButton.setDisable(false);
                stopButton.setDisable(false);
            }
        }
    }

    @FXML
    private void handlePauseResume(ActionEvent event) {
        String status = simulatorController.getSimulatorStatus();
        if (status == null) return;

        if (status.equals(Simulator.STATUS_RUNNING)) {
            simulatorController.pauseSimulation();
        } else if (status.equals(Simulator.STATUS_PAUSED)) {
            simulatorController.resumeSimulation();
        }
        updateUI();
    }

    @FXML
    private void handleRestart(ActionEvent event) {
        if (confirm("Restart Simulation", "Are you sure? This will reset the simulation.")) {
            if (!simulatorController.restartSimulation()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to restart the simulation.");
            }
        }
    }

    @FXML
    private void handleStop(ActionEvent event) {
        if (confirm("Stop Simulation", "Are you sure? This will stop the simulation and generate a final report.")) {
            String report = simulatorController.stopSimulation();
            if (report != null) {
                simulationUpdateTimer.stop();
                updateUI();
                showTextDialog("Simulator Final Report", report);
                handleClose(null);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to stop the simulation.");
            }
        }
    }

    @FXML
    private void handleGenerateCargo(ActionEvent event) {
        if (simulatorController.generateCargo()) {
            //messages in english
            showAlert(Alert.AlertType.INFORMATION, "Success", "Cargo has been successfully generated for the stations.");
        } else {
            showAlert(Alert.AlertType.WARNING, "Cargo not generated", "Not possible to generate cargo. Stations may be full or no trains are assigned to routes.");
        }
    }

    @FXML
    private void handleViewCargo(ActionEvent event) {
        java.util.Map<String, List<Cargo>> stationCargo = simulatorController.getCargoGenerationDetails();
        if (stationCargo.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Sem Carga", "Não há carga disponível em nenhuma estação.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (java.util.Map.Entry<String, List<Cargo>> entry : stationCargo.entrySet()) {
            sb.append("Estação: ").append(entry.getKey()).append("\n");
            if (entry.getValue().isEmpty()) {
                sb.append("  - Sem carga disponível\n");
            } else {
                for (Cargo cargo : entry.getValue()) {
                    sb.append("  - ").append(cargo.toString()).append("\n");
                }
            }
            sb.append("\n");
        }
        showTextDialog("Carga Atual nas Estações", sb.toString());
    }

    @FXML
    private void handleClose(ActionEvent event) {
        if (simulationUpdateTimer != null) {
            simulationUpdateTimer.stop();
        }
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean confirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void showTextDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);

        TextArea textArea = new TextArea(content);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        VBox vbox = new VBox(textArea);
        VBox.setVgrow(textArea, javafx.scene.layout.Priority.ALWAYS);

        alert.getDialogPane().setContent(vbox);
        alert.getDialogPane().setPrefSize(600, 400);
        alert.setResizable(true);
        alert.showAndWait();
    }
}