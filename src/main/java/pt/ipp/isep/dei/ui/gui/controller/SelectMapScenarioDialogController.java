package pt.ipp.isep.dei.ui.gui.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import pt.ipp.isep.dei.controller.template.ApplicationSession;
import pt.ipp.isep.dei.controller.template.MapController;
import pt.ipp.isep.dei.controller.template.ViewScenarioLayoutController;
import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.domain.template.Player;
import pt.ipp.isep.dei.domain.template.Scenario;

import java.util.List;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class SelectMapScenarioDialogController {

    @FXML
    private ChoiceBox<String> mapChoiceBox;

    @FXML
    private ChoiceBox<String> scenarioChoiceBox;

    @FXML
    private Button confirmButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label messageLabel;

    @FXML
    private TextFlow mapPreviewArea;

    private MapController mapController;
    private ViewScenarioLayoutController layoutController;
    private ApplicationSession appSession;
    private List<Map> availableMaps;
    private LinkedHashMap<String, String> scenarioDisplayMap; // To store display name -> scenario ID

    public void initialize() {
        mapController = new MapController();
        layoutController = new ViewScenarioLayoutController();
        appSession = ApplicationSession.getInstance();

        // Initialize mapPreviewArea with default text
        if (mapPreviewArea != null) {
            Text defaultText = new Text("Select a map to view preview...");
            mapPreviewArea.getChildren().add(defaultText);

            // Set monospace font for better text alignment
            mapPreviewArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");
        } else {
            System.err.println("Warning: mapPreviewArea not injected by FXML loader!");
        }

        loadMaps();

        mapChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldMapName, newMapName) -> {
            if (newMapName != null) {
                Map selectedMap = availableMaps.stream()
                        .filter(map -> map.getNameID().equals(newMapName))
                        .findFirst()
                        .orElse(null);
                if (selectedMap != null) {
                    loadScenarios(selectedMap);
                    updateMapPreview(selectedMap, null); // Show map without scenario initially
                }
            } else {
                scenarioChoiceBox.getItems().clear();
                scenarioChoiceBox.setDisable(true);
                confirmButton.setDisable(true);
                Text defaultText = new Text("Select a map to view preview...");
                mapPreviewArea.getChildren().setAll(defaultText);
            }
        });

        scenarioChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldScenario, newScenario) -> {
            confirmButton.setDisable(newScenario == null);
            if (newScenario != null) {
                String selectedMapName = mapChoiceBox.getValue();
                Map selectedMap = availableMaps.stream()
                        .filter(map -> map.getNameID().equals(selectedMapName))
                        .findFirst()
                        .orElse(null);

                if (selectedMap != null) {
                    String scenarioId = scenarioDisplayMap.get(newScenario);
                    Scenario scenario = mapController.getScenario(scenarioId);
                    updateMapPreview(selectedMap, scenario);
                }
            }
        });

        scenarioChoiceBox.setDisable(true);
        confirmButton.setDisable(true);
        messageLabel.setText("");
    }

    private void updateMapPreview(Map map, Scenario scenario) {
        if (mapPreviewArea == null) {
            System.err.println("Error: mapPreviewArea is null!");
            return;
        }

        mapPreviewArea.getChildren().clear();

        // Set monospace font and increase size for better readability
        mapPreviewArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 14px; -fx-padding: 10px;");

        if (map != null) {
            try {
                String layout = layoutController.renderSimpleScenarioLayout(map, scenario);
                String[] lines = layout.split("\n");

                for (String line : lines) {
                    // Check if this line contains map symbols that need coloring
                    if (containsMapSymbols(line)) {
                        TextFlow coloredLine = createColoredMapLine(line);
                        mapPreviewArea.getChildren().add(coloredLine);
                    } else {
                        // Regular text line (header, legend, etc.)
                        Text lineText = new Text(line + "\n");
                        lineText.setFont(Font.font("Courier New", 14));
                        lineText.setFill(Color.BLACK);
                        mapPreviewArea.getChildren().add(lineText);
                    }
                }

            } catch (Exception e) {
                System.err.println("Error rendering map layout: " + e.getMessage());
                e.printStackTrace();
                Text errorText = new Text("Error rendering map preview: " + e.getMessage());
                errorText.setFill(Color.RED);
                mapPreviewArea.getChildren().add(errorText);
            }
        } else {
            Text defaultText = new Text("Select a map to view preview...");
            defaultText.setFont(Font.font("Courier New", 14));
            mapPreviewArea.getChildren().add(defaultText);
        }
    }

    private boolean containsMapSymbols(String line) {
        // Check if line contains map symbols (but not legend lines)
        return (line.contains("C") || line.contains("I") || line.contains("S") || line.contains("."))
                && !line.trim().startsWith("C -")
                && !line.trim().startsWith("I -")
                && !line.trim().startsWith("S -")
                && !line.trim().startsWith("Legend:")
                && !line.trim().startsWith(". -")
                && !line.trim().startsWith("Map:")
                && !line.trim().startsWith("Scenario:")
                && !line.trim().startsWith("Period:")
                && !line.trim().startsWith("Size:");
    }

    private TextFlow createColoredMapLine(String line) {
        TextFlow textFlow = new TextFlow();

        // Split the line at the " | " separator if it exists
        String[] parts = line.split(" \\| ", 2);
        String mapPart = parts[0];
        String infoPart = parts.length > 1 ? parts[1] : "";

        // Process the map symbols part
        for (int i = 0; i < mapPart.length(); i++) {
            char c = mapPart.charAt(i);
            Text charText = new Text(String.valueOf(c));

            switch (c) {
                case 'C':
                    charText.setFill(Color.RED);
                    charText.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
                    break;
                case 'I':
                    charText.setFill(Color.BLUE);
                    charText.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
                    break;
                case 'S':
                    charText.setFill(Color.GREEN);
                    charText.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
                    break;
                default:
                    charText.setFill(Color.BLACK);
                    charText.setFont(Font.font("Courier New", 14)); // Normal weight for non-symbols
                    break;
            }
            textFlow.getChildren().add(charText);
        }

        // Add the info part if it exists
        if (!infoPart.isEmpty()) {
            Text separator = new Text(" | ");
            separator.setFont(Font.font("Courier New", 14));
            separator.setFill(Color.BLACK);
            textFlow.getChildren().add(separator);

            Text info = new Text(infoPart);
            info.setFont(Font.font("Courier New", 14));
            info.setFill(Color.DARKGRAY);
            textFlow.getChildren().add(info);
        }

        // Add newline
        Text newline = new Text("\n");
        textFlow.getChildren().add(newline);

        return textFlow;
    }

    private void loadMaps() {
        availableMaps = mapController.getAvailableMaps();
        System.out.println("Available maps: " + availableMaps.size()); // Debug print
        if (availableMaps.isEmpty()) {
            messageLabel.setText("No maps available.");
            mapChoiceBox.setDisable(true);
            return;
        }
        mapChoiceBox.setItems(FXCollections.observableArrayList(
                availableMaps.stream().map(Map::getNameID).collect(Collectors.toList())
        ));
        mapChoiceBox.setDisable(false);

        // Remove auto-selection of first map
        // mapChoiceBox.getSelectionModel().selectFirst();
    }

    private void loadScenarios(Map selectedMap) {
        List<String> scenarioIDs = mapController.getMapScenarios(selectedMap.getNameID());
        System.out.println("Loading scenarios for map: " + selectedMap.getNameID());
        System.out.println("Available scenarios: " + scenarioIDs);

        if (scenarioIDs.isEmpty()) {
            messageLabel.setText("No scenarios available for this map.");
            scenarioChoiceBox.getItems().clear();
            scenarioChoiceBox.setDisable(true);
            return;
        }

        // Filter out scenario3 and scenario4 for all maps
        List<String> filteredScenarioIDs = scenarioIDs.stream()
                .filter(id -> !id.equals("scenario3") && !id.equals("scenario4"))
                .collect(Collectors.toList());

        System.out.println("Filtered scenarios: " + filteredScenarioIDs);

        scenarioDisplayMap = new LinkedHashMap<>();
        for (String id : filteredScenarioIDs) {
            String displayName;
            if (id.equals("scenario1")) {
                displayName = selectedMap.getNameID().equals("italy") ? "Italian Giolitti Era" :
                        selectedMap.getNameID().equals("france") ? "French Belle Époque" :
                                selectedMap.getNameID().equals("iberian_peninsula") ? "Iberian Early Industrial" : id;
            } else if (id.equals("scenario2")) {
                displayName = selectedMap.getNameID().equals("italy") ? "Italian Inter-War" :
                        selectedMap.getNameID().equals("france") ? "French Reconstruction" :
                                selectedMap.getNameID().equals("iberian_peninsula") ? "Iberian Inter-War" : id;
            } else {
                displayName = id;
            }
            scenarioDisplayMap.put(displayName, id);
            System.out.println("Added scenario: " + displayName + " -> " + id);
        }

        scenarioChoiceBox.setItems(FXCollections.observableArrayList(new ArrayList<>(scenarioDisplayMap.keySet())));
        scenarioChoiceBox.setDisable(false);
        messageLabel.setText("");
    }

    @FXML
    void handleConfirm(ActionEvent event) {
        String selectedMapName = mapChoiceBox.getValue();
        String selectedScenarioDisplayName = scenarioChoiceBox.getValue();

        if (selectedMapName == null || selectedScenarioDisplayName == null) {
            messageLabel.setText("Please select both a map and a scenario.");
            return;
        }

        String selectedScenarioID = scenarioDisplayMap.get(selectedScenarioDisplayName);
        System.out.println("Loading map: " + selectedMapName + " with scenario: " + selectedScenarioID);

        if (mapController.loadMap(selectedMapName, selectedScenarioID)) {
            Player currentPlayer = appSession.getCurrentPlayer();
            if (currentPlayer != null) {
                currentPlayer.initializeScenarioBudget(selectedScenarioID);
                System.out.println("Player budget initialized for scenario: " + selectedScenarioID);
            }
            messageLabel.setText("Map and scenario loaded successfully!");
            closeDialog();
        } else {
            messageLabel.setText("Failed to load map and scenario.");
            System.err.println("Failed to load map: " + selectedMapName + " with scenario: " + selectedScenarioID);
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }
}