package pt.ipp.isep.dei.ui.gui.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow; // Import adicionado
import javafx.stage.Stage;
import pt.ipp.isep.dei.controller.template.ApplicationSession;
import pt.ipp.isep.dei.controller.template.MapController;
import pt.ipp.isep.dei.controller.template.ViewScenarioLayoutController;
import pt.ipp.isep.dei.domain.template.*; // Import para City, Industry, etc.

import java.util.List;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class SelectMapScenarioDialogController {

    // ... (variáveis @FXML existentes) ...
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
    private GridPane mapGridPane;
    @FXML
    private Label mapNameLabel;
    @FXML
    private Label scenarioNameLabel;
    @FXML
    private Label periodLabel;
    @FXML
    private Label sizeLabel;

    // --- NOVO ELEMENTO FXML PARA A LEGENDA ---
    @FXML
    private TextFlow legendTextFlow;

    private MapController mapController;
    private ViewScenarioLayoutController layoutController;
    private ApplicationSession appSession;
    private List<Map> availableMaps;
    private LinkedHashMap<String, String> scenarioDisplayMap;

    public void initialize() {
        mapController = new MapController();
        layoutController = new ViewScenarioLayoutController();
        appSession = ApplicationSession.getInstance();

        clearPreview();
        buildLegend(); // Constrói a legenda colorida no início
        loadMaps();

        mapChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldMapName, newMapName) -> {
            if (newMapName != null) {
                Map selectedMap = availableMaps.stream()
                        .filter(map -> map.getNameID().equals(newMapName))
                        .findFirst()
                        .orElse(null);
                if (selectedMap != null) {
                    loadScenarios(selectedMap);
                    // Alteração: Passa null para o nome do cenário
                    updateMapPreview(selectedMap, null, null);
                }
            } else {
                scenarioChoiceBox.getItems().clear();
                scenarioChoiceBox.setDisable(true);
                confirmButton.setDisable(true);
                clearPreview();
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
                    // Alteração: Passa o nome do cenário (newScenario) para a pré-visualização
                    updateMapPreview(selectedMap, scenario, newScenario);
                }
            }
        });

        scenarioChoiceBox.setDisable(true);
        confirmButton.setDisable(true);
        messageLabel.setText("");
    }

    // --- NOVO MÉTODO PARA CONSTRUIR A LEGENDA ---
    private void buildLegend() {
        if (legendTextFlow == null) return;
        legendTextFlow.getChildren().clear();

        Text cSymbol = new Text("C");
        cSymbol.setFill(Color.RED);
        cSymbol.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
        Text cText = new Text("-Cidade   ");
        cText.setFont(Font.font("System", 12));

        Text iSymbol = new Text("I");
        iSymbol.setFill(Color.BLUE);
        iSymbol.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
        Text iText = new Text("-Indústria   ");
        iText.setFont(Font.font("System", 12));

        Text sSymbol = new Text("S");
        sSymbol.setFill(Color.GREEN);
        sSymbol.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
        Text sText = new Text("-Estação");
        sText.setFont(Font.font("System", 12));

        legendTextFlow.getChildren().addAll(cSymbol, cText, iSymbol, iText, sSymbol, sText);
    }


    // --- MÉTODO ATUALIZADO para receber o nome do cenário ---
    private void updateMapPreview(Map map, Scenario scenario, String scenarioDisplayName) {
        clearPreview();
        if (map == null) return;

        ViewScenarioLayoutController.MapLayoutData layoutData = layoutController.getMapLayoutData(map, scenario);
        if (layoutData == null) {
            mapNameLabel.setText("Erro ao renderizar os dados do mapa.");
            return;
        }

        mapNameLabel.setText("Mapa: " + layoutData.mapName);
        sizeLabel.setText("Tamanho: " + layoutData.width + "x" + layoutData.height);

        // Alteração: Usa o nome descritivo do cenário
        if (scenarioDisplayName != null) {
            scenarioNameLabel.setText("Cenário: " + scenarioDisplayName);
            periodLabel.setText("Período: " + layoutData.startDate + " - " + layoutData.endDate);
        } else {
            scenarioNameLabel.setText("Selecione um cenário");
            periodLabel.setText("");
        }

        // O resto do método permanece igual...
        for (int y = 0; y < layoutData.height; y++) {
            for (int x = 0; x < layoutData.width; x++) {
                ViewScenarioLayoutController.CellData cellData = layoutData.grid[y][x];
                Text cellText = new Text(cellData.symbol);
                cellText.setFont(Font.font("Courier New", FontWeight.NORMAL, 14));
                switch (cellData.type) {
                    case CITY:
                        cellText.setFill(Color.RED);
                        cellText.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
                        break;
                    case INDUSTRY:
                        cellText.setFill(Color.BLUE);
                        cellText.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
                        break;
                    case STATION:
                        cellText.setFill(Color.GREEN);
                        cellText.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
                        break;
                    default:
                        cellText.setFill(Color.BLACK);
                        break;
                }
                mapGridPane.add(cellText, x, y);
            }
        }

        for (int y = 0; y < layoutData.height; y++) {
            String info = getRowInfo(map, y, scenario);
            if (info != null && !info.isEmpty()) {
                Text infoText = new Text("  | " + info);
                infoText.setFont(Font.font("Courier New", 12));
                infoText.setFill(Color.DARKGRAY);
                mapGridPane.add(infoText, layoutData.width, y);
            }
        }
    }

    // ... (resto dos métodos: clearPreview, getRowInfo, loadMaps, loadScenarios, etc. permanecem iguais) ...
    private void clearPreview() {
        if (mapGridPane != null) {
            mapGridPane.getChildren().clear();
            mapGridPane.getColumnConstraints().clear();
            mapGridPane.getRowConstraints().clear();
        }
        mapNameLabel.setText("Selecione um mapa para ver a pré-visualização...");
        scenarioNameLabel.setText("");
        periodLabel.setText("");
        sizeLabel.setText("");
    }

    private String getRowInfo(Map map, int y, Scenario scenario) {
        StringBuilder info = new StringBuilder();
        Set<String> entitiesLabeled = new HashSet<>();
        for (City city : map.getCities()) {
            if (city.getPosition().getY() == y) {
                String cityKey = "C_" + city.getNameID();
                if (!entitiesLabeled.contains(cityKey)) {
                    if (info.length() > 0) {
                        info.append("; ");
                    }
                    info.append("Cidade ").append(city.getNameID());
                    if (scenario != null) {
                        info.append(" (D:").append(city.getDemandedCargo()).append(", S:").append(city.getSuppliedCargo()).append(")");
                    }
                    entitiesLabeled.add(cityKey);
                }
            }
        }
        for (Industry industry : map.getIndustries()) {
            if (industry.getPosition().getY() == y) {
                String industryKey = "I_" + industry.getNameID();
                if (!entitiesLabeled.contains(industryKey)) {
                    if (info.length() > 0) {
                        info.append("; ");
                    }
                    info.append("Indústria ").append(industry.getNameID());
                    if (scenario != null) {
                        info.append(" (Tipo:").append(industry.getType()).append(")");
                    }
                    entitiesLabeled.add(industryKey);
                }
            }
        }
        return info.toString();
    }

    private void loadMaps() {
        availableMaps = mapController.getAvailableMaps();
        if (availableMaps.isEmpty()) {
            messageLabel.setText("Não há mapas disponíveis.");
            mapChoiceBox.setDisable(true);
            return;
        }
        mapChoiceBox.setItems(FXCollections.observableArrayList(
                availableMaps.stream().map(Map::getNameID).collect(Collectors.toList())
        ));
        mapChoiceBox.setDisable(false);
    }

    private void loadScenarios(Map selectedMap) {
        List<String> scenarioIDs = mapController.getMapScenarios(selectedMap.getNameID());
        if (scenarioIDs.isEmpty()) {
            messageLabel.setText("Não há cenários para este mapa.");
            scenarioChoiceBox.getItems().clear();
            scenarioChoiceBox.setDisable(true);
            return;
        }

        List<String> filteredScenarioIDs = scenarioIDs.stream()
                .filter(id -> !id.equals("scenario3") && !id.equals("scenario4"))
                .collect(Collectors.toList());

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
            messageLabel.setText("Por favor, selecione um mapa e um cenário.");
            return;
        }

        String selectedScenarioID = scenarioDisplayMap.get(selectedScenarioDisplayName);

        if (mapController.loadMap(selectedMapName, selectedScenarioID)) {
            Player currentPlayer = appSession.getCurrentPlayer();
            if (currentPlayer != null) {
                currentPlayer.initializeScenarioBudget(selectedScenarioID);
            }
            messageLabel.setText("Mapa e cenário carregados com sucesso!");
            closeDialog();
        } else {
            messageLabel.setText("Falha ao carregar o mapa e o cenário.");
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