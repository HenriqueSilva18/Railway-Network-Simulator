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
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class SimulationDialogController implements Initializable {

    @FXML private Label statusLabel;
    @FXML private Label dateLabel;
    @FXML private Label budgetLabel;
    @FXML private Button pauseResumeButton;
    @FXML private Button restartButton;
    @FXML private Button viewCargoButton;
    @FXML private Button stopButton;

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
                    double currentBudget = currentPlayer.getCurrentBudget();
                    budgetLabel.setText(String.format("$%.2f", currentBudget));
                    
                    // Check for bankruptcy (below $1000)
                    if (currentBudget < 1000) {
                        String report = simulatorController.stopSimulation();
                        if (report != null) {
                            simulationUpdateTimer.stop();
                            showTextDialog("Bankruptcy Alert - Simulation Stopped", 
                                "The simulation has been stopped due to bankruptcy.\n\n" +
                                "Your budget has fallen below $1000.\n\n" +
                                "Final Report:\n" + report);
                            Stage stage = (Stage) stopButton.getScene().getWindow();
                            stage.close();
                        }
                        return;
                    }
                }

                boolean isRunning = status.equals(Simulator.STATUS_RUNNING);
                pauseResumeButton.setText(isRunning ? "Pause" : "Resume");

                pauseResumeButton.setDisable(false);
                restartButton.setDisable(false);
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
        if (confirm("Restart Simulation", "Are you sure? This will reset the simulation and your budget to its initial value.")) {
            // Get current scenario ID before restarting
            Scenario currentScenario = ApplicationSession.getInstance().getCurrentScenario();
            if (currentScenario != null) {
                String scenarioId = currentScenario.getNameID();
                Player currentPlayer = appSession.getCurrentPlayer();
                if (currentPlayer != null) {
                    // Reset budget to initial value
                    currentPlayer.initializeScenarioBudget(scenarioId);
                }
            }
            
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
                Stage stage = (Stage) stopButton.getScene().getWindow();
                stage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to stop the simulation.");
            }
        }
    }

    @FXML
    private void handleViewCargo(ActionEvent event) {
        Map<String, List<Cargo>> stationCargo = simulatorController.getCargoGenerationDetails();
        if (stationCargo.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Cargo", "There is no cargo available at any station.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Current Cargo at Stations:\n\n");
        
        for (Map.Entry<String, List<Cargo>> entry : stationCargo.entrySet()) {
            sb.append("Station: ").append(entry.getKey()).append("\n");
            if (entry.getValue().isEmpty()) {
                sb.append("  - No cargo available\n");
            } else {
                for (Cargo cargo : entry.getValue()) {
                    sb.append("  - ").append(cargo.toString()).append("\n");
                }
            }
            sb.append("\n");
        }
        
        showTextDialog("Current Cargo at Stations", sb.toString());
    }

    private boolean confirm(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
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