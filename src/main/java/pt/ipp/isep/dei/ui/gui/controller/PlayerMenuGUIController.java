package pt.ipp.isep.dei.ui.gui.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import pt.ipp.isep.dei.controller.template.*;
import pt.ipp.isep.dei.domain.template.*;
import pt.ipp.isep.dei.repository.template.RailwayLineRepository;
import pt.ipp.isep.dei.repository.template.Repositories;
import pt.ipp.isep.dei.ui.gui.MainApp;

import javafx.scene.control.ScrollPane; // Certifique-se que este import existe
import javafx.geometry.Insets;
import pt.ipp.isep.dei.ui.gui.utils.AlertHelper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Optional;

public class PlayerMenuGUIController implements Initializable {

    @FXML
    private BorderPane playerMainPane;

    // Main Menu Items
    @FXML
    private MenuItem selectMapScenarioMenuItem;
    @FXML
    private MenuItem saveGameMenuItem;
    @FXML
    private MenuItem loadGameMenuItem;
    @FXML
    private MenuItem logoutMenuItem;

    @FXML
    private MenuItem buildStationMenuItem; // Main menu
    @FXML
    private MenuItem upgradeStationMenuItem; // Main menu
    @FXML
    private MenuItem buildRailwayLineMenuItem; // Main menu
    @FXML
    private MenuItem buyLocomotiveMenuItem; // Main menu
    @FXML
    private MenuItem createRouteMenuItem; // Main menu
    @FXML
    private MenuItem assignTrainMenuItem; // Main menu

    @FXML
    private MenuItem viewCurrentMapMenuItem; // Main menu
    @FXML
    private MenuItem listStationsMenuItem; // Main menu
    @FXML
    private MenuItem listTrainsMenuItem; // Main menu
    @FXML
    private MenuItem viewConnectivityMenuItem; // Main menu
    @FXML
    private MenuItem viewMaintenanceRouteMenuItem; // Main menu
    @FXML
    private MenuItem viewShortestRouteMenuItem; // Main menu
    @FXML
    private MenuItem viewFinancialResultsMenuItem; // Main menu

    @FXML
    private MenuItem runPauseSimulatorMenuItem; // Main menu

    @FXML
    private MenuItem simulationControlMenuItem; // Main menu

    @FXML
    private MenuItem aboutMenuItem;

    @FXML
    private StackPane contentArea;

    @FXML
    private Label budgetLabel;
    @FXML
    private Label simulatorTimeLabel;

    private Timeline simulationUpdateTimer;

    // Cards VBox elements from FXML
    @FXML
    private VBox infrastructureCard;
    @FXML
    private VBox operationsCard;
    @FXML
    private VBox simulationCard; // Added for "Simulation Control" card
    @FXML
    private VBox financialCard;  // Added for "Financial Reports" card

    @FXML
    private Label mapInfoPlaceholder;

    // Context Menu Items - Instance variables to hold references
    // Infrastructure Card
    private MenuItem cardBuildStationItem;
    private MenuItem cardUpgradeStationItem;
    private MenuItem cardBuildRailwayItem;
    private MenuItem cardListStationsItem;

    // Operations Card
    private MenuItem cardBuyLocomotiveItem;
    private MenuItem cardCreateRouteItem;
    private MenuItem cardAssignTrainItem;
    private MenuItem cardListTrainsItem;
    private MenuItem cardRunSimulatorItem;
    private MenuItem cardSimulatorControlItem;

    // Simulation Card (New - placeholders)
    private MenuItem cardSimControlRunPauseItem;
    private MenuItem cardSimAdvanceTimeItem;

    // Financial Card (New - placeholders)
    private MenuItem cardFinViewReportItem;
    private MenuItem cardFinExportDataỊtem;

    private ApplicationSession appSession = ApplicationSession.getInstance();
    private ViewScenarioLayoutController viewLayoutController; // Controller para obter dados do mapa
    private CreateRouteController createRouteController;
    private AssignTrainController assignTrainController;
    private SimulatorController simulatorController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.viewLayoutController = new ViewScenarioLayoutController(); // Inicializar o controller de layout
        this.simulatorController = new SimulatorController();
        updateBudgetDisplay();
        // Configurar o temporizador que vai atualizar a label
        simulationUpdateTimer = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> updateSimulatorTimeDisplay())
        );
        simulationUpdateTimer.setCycleCount(Timeline.INDEFINITE);

        // Atualizar a UI uma vez no início para mostrar "Parado"
        updateSimulatorTimeDisplay();
        setupCardContextMenus();
        updateMenuItemsState();
        updateMapInfoPlaceholderLabel(); // Atualiza o placeholder no início
        loadMapVisualization();          // Tenta carregar a visualização do mapa no início
    }

    private void setupCardContextMenus() {
        setupInfrastructureCardMenu();
        setupOperationsCardMenu();
        setupSimulationCardMenu();
        setupFinancialCardMenu();
    }

    // --- Handlers for FXML onMouseClicked (can be simple or delegate) ---

    @FXML
    void handleInfrastructureCardClick() {
        System.out.println("Infrastructure card clicked - context menu should show via setOnMouseClicked");
    }

    @FXML
    void handleOperationsCardClick() {
        System.out.println("Operations card clicked - context menu should show via setOnMouseClicked");
    }

    @FXML
    void handleViewCardClick() {
        System.out.println("View card clicked - context menu should show via setOnMouseClicked");
    }

    @FXML
    void handleSimulationCardClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SimulationDialog.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Simulation Control Panel");
            // Use NONE para permitir interação com a janela principal em simultâneo
            dialogStage.initModality(Modality.NONE);
            dialogStage.initOwner(playerMainPane.getScene().getWindow());

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Not possible to open Simulation Control Panel: " + e.getMessage());
        }
    }

    @FXML
    void handleFinancialCardClick() {
        System.out.println("Financial Reports card clicked - context menu should show via setOnMouseClicked");
    }

    @FXML
    void handleStatisticsCardClick() {
        System.out.println("Statistics card clicked - context menu should show via setOnMouseClicked");
    }

    // --- Setup methods for each card's context menu ---

    private void setupInfrastructureCardMenu() {
        if (infrastructureCard != null) {
            ContextMenu contextMenu = new ContextMenu();

            cardBuildStationItem = new MenuItem("Build Station");
            cardBuildStationItem.setOnAction(this::handleBuildStation);

            cardUpgradeStationItem = new MenuItem("Upgrade Station");
            cardUpgradeStationItem.setOnAction(this::handleUpgradeStation);

            cardBuildRailwayItem = new MenuItem("Build Railway Line");
            cardBuildRailwayItem.setOnAction(this::handleBuildRailwayLine);

            cardListStationsItem = new MenuItem("View Stations Details");
            cardListStationsItem.setOnAction(this::handleListStations);

            contextMenu.getItems().addAll(
                cardBuildStationItem,
                cardUpgradeStationItem,
                cardBuildRailwayItem,
                new SeparatorMenuItem(),
                cardListStationsItem
            );

            setupCardHoverEffect(infrastructureCard);
            infrastructureCard.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY || event.getButton() == MouseButton.SECONDARY) {
                    contextMenu.show(infrastructureCard, event.getScreenX(), event.getScreenY());
                }
            });
        }
    }

    private void setupOperationsCardMenu() {
        if (operationsCard != null) {
            ContextMenu contextMenu = new ContextMenu();

            cardBuyLocomotiveItem = new MenuItem("Buy Locomotive");
            cardBuyLocomotiveItem.setOnAction(this::handleBuyLocomotive);

            cardCreateRouteItem = new MenuItem("Create Route");
            cardCreateRouteItem.setOnAction(this::handleCreateRoute);

            cardAssignTrainItem = new MenuItem("Assign Train to Route");
            cardAssignTrainItem.setOnAction(this::handleAssignTrainToRoute);

            cardListTrainsItem = new MenuItem("List Trains");
            cardListTrainsItem.setOnAction(this::handleListTrains);

            // Note: This item also exists on the main menu bar.
            // And potentially on the new "Simulation Control" card.
            // Ensure consistent behavior or specific actions as needed.
            cardRunSimulatorItem = new MenuItem("Run/Pause Simulator (Ops)");
            cardRunSimulatorItem.setOnAction(this::handleRunPauseSimulator);
            cardSimulatorControlItem = new MenuItem("Simulation Control");
            cardSimulatorControlItem.setOnAction(this::handleSimulationCardClick);

            contextMenu.getItems().addAll(cardBuyLocomotiveItem, cardCreateRouteItem, cardAssignTrainItem, cardListTrainsItem,
                    new SeparatorMenuItem(), cardRunSimulatorItem);

            setupCardHoverEffect(operationsCard);
            operationsCard.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY || event.getButton() == MouseButton.SECONDARY) {
                    contextMenu.show(operationsCard, event.getScreenX(), event.getScreenY());
                }
            });
        }
    }

    private void setupSimulationCardMenu() {
        if (simulationCard != null) {
            ContextMenu contextMenu = new ContextMenu();

            // Example: Reusing the main run/pause simulator action
            cardSimControlRunPauseItem = new MenuItem("Run/Pause Simulation");
            cardSimControlRunPauseItem.setOnAction(this::handleRunPauseSimulator); // Reuses main handler

            cardSimAdvanceTimeItem = new MenuItem("Advance Time by 1 Step");
            cardSimAdvanceTimeItem.setOnAction(event -> showAlert("Not Implemented", "Advance Time (US_SIM_X) not implemented."));

            // Add more simulation-specific actions here
            contextMenu.getItems().addAll(cardSimControlRunPauseItem, cardSimulatorControlItem,  cardSimAdvanceTimeItem);

            setupCardHoverEffect(simulationCard);
            simulationCard.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY || event.getButton() == MouseButton.SECONDARY) {
                    contextMenu.show(simulationCard, event.getScreenX(), event.getScreenY());
                }
            });
        }
    }

    /**
     * Configura o menu de contexto para o card de Financial Reports (NOVO)
     */
    private void setupFinancialCardMenu() {
        if (financialCard != null) {
            ContextMenu contextMenu = new ContextMenu();

            // Example: Reusing the main financial results view action
            cardFinViewReportItem = new MenuItem("View Annual Financial Report");
            cardFinViewReportItem.setOnAction(this::handleViewFinancialResults); // Reuses main handler

            cardFinExportDataỊtem = new MenuItem("Export Financial Data");
            cardFinExportDataỊtem.setOnAction(event -> showAlert("Not Implemented", "Export Financial Data (US_FIN_X) not implemented."));


            // Add more financial-specific actions here
            contextMenu.getItems().addAll(cardFinViewReportItem, cardFinExportDataỊtem);

            setupCardHoverEffect(financialCard);
            financialCard.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY || event.getButton() == MouseButton.SECONDARY) {
                    contextMenu.show(financialCard, event.getScreenX(), event.getScreenY());
                }
            });
        }
    }

    private ImageView createMenuIcon(String iconPath) {
        try {
            // Ensure the path is absolute from the classpath root
            String correctedPath = iconPath.startsWith("/") ? iconPath : "/" + iconPath;
            URL resourceUrl = getClass().getResource(correctedPath);
            if (resourceUrl == null) {
                System.err.println("Warning: Icon resource not found at " + correctedPath);
                return null;
            }
            ImageView icon = new ImageView(new Image(resourceUrl.toExternalForm()));
            icon.setFitWidth(16);
            icon.setFitHeight(16);
            icon.setPreserveRatio(true);
            return icon;
        } catch (Exception e) {
            System.err.println("Error loading icon: " + iconPath + " - " + e.getMessage());
            return null;
        }
    }

    private void setupCardHoverEffect(VBox card) {
        if (card != null) {
            String originalStyle = card.getStyle();
            if (originalStyle == null) originalStyle = ""; // Ensure not null

            final String finalOriginalStyle = originalStyle; // For use in lambda

            card.setOnMouseEntered(event -> {
                String hoverStyle = finalOriginalStyle +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 20, 0, 0, 8); " +
                        "-fx-scale-x: 1.02; -fx-scale-y: 1.02;";
                card.setStyle(hoverStyle);
            });

            card.setOnMouseExited(event -> {
                card.setStyle(finalOriginalStyle);
            });

            card.setOnMousePressed(event -> {
                String clickStyle = finalOriginalStyle +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 25, 0, 0, 10); " +
                        "-fx-scale-x: 0.98; -fx-scale-y: 0.98;";
                card.setStyle(clickStyle);
            });

            card.setOnMouseReleased(event -> {
                if (card.isHover()) {
                    String hoverStyle = finalOriginalStyle +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 20, 0, 0, 8); " +
                            "-fx-scale-x: 1.02; -fx-scale-y: 1.02;";
                    card.setStyle(hoverStyle);
                } else {
                    card.setStyle(finalOriginalStyle);
                }
            });
        }
    }

    /**
     * Updates the state of context menu items based on the current game state.
     */
    private void updateCardMenusState() {
        boolean mapLoaded = appSession.getCurrentMap() != null && appSession.getCurrentScenario() != null;

        // Infrastructure Card Items
        if (cardBuildStationItem != null) cardBuildStationItem.setDisable(!mapLoaded);
        if (cardUpgradeStationItem != null) cardUpgradeStationItem.setDisable(!mapLoaded);
        if (cardBuildRailwayItem != null) cardBuildRailwayItem.setDisable(!mapLoaded);
        if (cardListStationsItem != null) cardListStationsItem.setDisable(!mapLoaded);

        // Operations Card Items
        if (cardBuyLocomotiveItem != null) cardBuyLocomotiveItem.setDisable(!mapLoaded);
        if (cardCreateRouteItem != null) cardCreateRouteItem.setDisable(!mapLoaded);
        if (cardAssignTrainItem != null) cardAssignTrainItem.setDisable(!mapLoaded);
        if (cardListTrainsItem != null) cardListTrainsItem.setDisable(!mapLoaded);
        if (cardRunSimulatorItem != null) cardRunSimulatorItem.setDisable(!mapLoaded);
        if (cardSimulatorControlItem != null) cardSimulatorControlItem.setDisable(!mapLoaded);

        // Simulation Card Items
        if (cardSimControlRunPauseItem != null) cardSimControlRunPauseItem.setDisable(!mapLoaded);
        if (cardSimAdvanceTimeItem != null) cardSimAdvanceTimeItem.setDisable(!mapLoaded);

        // Financial Card Items
        if (cardFinViewReportItem != null) cardFinViewReportItem.setDisable(!mapLoaded);
        if (cardFinExportDataỊtem != null) cardFinExportDataỊtem.setDisable(!mapLoaded);
    }


    private void updateBudgetDisplay() {
        Player currentPlayer = appSession.getCurrentPlayer();
        if (currentPlayer != null && appSession.getCurrentMap() != null && appSession.getCurrentScenario() != null) {
            budgetLabel.setText(String.format("$%.2f", currentPlayer.getCurrentBudget()));
        } else {
            budgetLabel.setText("N/A");
        }
    }

    private String getScenarioDisplayName(String mapId, String scenarioId) {
        if (scenarioId == null) return "N/A";

        // Esta lógica é baseada no seu SelectMapScenarioDialogController
        // O ideal seria ter esta informação de forma mais centralizada/configurável
        if ("scenario1".equals(scenarioId)) {
            if ("italy".equals(mapId)) return "Italian Giolitti Era";
            if ("france".equals(mapId)) return "French Belle Époque";
            if ("iberian_peninsula".equals(mapId)) return "Iberian Early Industrial";
        } else if ("scenario2".equals(scenarioId)) {
            if ("italy".equals(mapId)) return "Italian Inter-War";
            if ("france".equals(mapId)) return "French Reconstruction";
            if ("iberian_peninsula".equals(mapId)) return "Iberian Inter-War";
        }
        return scenarioId; // Retorna o ID se não houver nome descritivo mapeado
    }



    private void updateSimulatorTimeDisplay(String time) {
        simulatorTimeLabel.setText(time);
    }

    /**
     * Atualiza a interface do utilizador com o estado atual e a data do simulador.
     * Este método é chamado periodicamente pelo Timeline.
     */
    private void updateSimulatorTimeDisplay() {
        // Obtém o simulador ativo através do repositório
        Simulator simulator = Repositories.getInstance().getSimulatorRepository().getActiveSimulator();

        if (simulator != null && !simulator.getStatus().equals(Simulator.STATUS_STOPPED)) {
            String status = simulator.getStatus();
            java.util.Date currentDate = simulator.getCurrentSimulatedDate();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = sdf.format(currentDate);

            // Combina o estado e a data e atualiza a label
            simulatorTimeLabel.setText(String.format("%s: %s", status, formattedDate));
        } else {
            // Se não houver simulação a decorrer, mostra "Parado"
            simulatorTimeLabel.setText("Stopped");
        }
    }

    private void updateMenuItemsState() {
        boolean mapLoaded = appSession.getCurrentMap() != null && appSession.getCurrentScenario() != null;

        // File Menu
        selectMapScenarioMenuItem.setDisable(mapLoaded);
        loadGameMenuItem.setDisable(mapLoaded);
        saveGameMenuItem.setDisable(!mapLoaded);

        // Infrastructure Menu (Main Menu Bar)
        buildStationMenuItem.setDisable(mapLoaded);
        upgradeStationMenuItem.setDisable(!mapLoaded);
        buildRailwayLineMenuItem.setDisable(!mapLoaded);

        // Operations Menu (Main Menu Bar)
        buyLocomotiveMenuItem.setDisable(!mapLoaded);
        createRouteMenuItem.setDisable(!mapLoaded);
        assignTrainMenuItem.setDisable(!mapLoaded);
        listTrainsMenuItem.setDisable(!mapLoaded);
        runPauseSimulatorMenuItem.setDisable(!mapLoaded);
        simulationControlMenuItem.setDisable(!mapLoaded);

        // View Menu (Main Menu Bar)
        viewCurrentMapMenuItem.setDisable(!mapLoaded);
        listStationsMenuItem.setDisable(!mapLoaded);
        viewConnectivityMenuItem.setDisable(!mapLoaded);
        viewMaintenanceRouteMenuItem.setDisable(!mapLoaded);
        viewShortestRouteMenuItem.setDisable(!mapLoaded);
        viewFinancialResultsMenuItem.setDisable(!mapLoaded);

        // Update context menus for cards as well
        updateCardMenusState();
    }

    // --- Main Menu Item Handlers ---
    @FXML
    void handleSelectMapScenario(ActionEvent event) {
        System.out.println("Select Map and Scenario clicked");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SelectMapScenarioDialog.fxml"));
            Parent dialogRoot = loader.load();

            SelectMapScenarioDialogController dialogController = loader.getController();
            dialogController.initController(this.simulatorController);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Select Map & Scenario");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(((Stage) playerMainPane.getScene().getWindow()));
            Scene scene = new Scene(dialogRoot);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            dialogStage.showAndWait();

            // Atualiza tudo após o fecho da janela
            updateMenuItemsState();
            updateBudgetDisplay();
            updateMapInfoPlaceholderLabel(); // Atualiza o placeholder com o nome correto do cenário
            loadMapVisualization();          // Carrega a visualização gráfica do mapa

            if (appSession.getCurrentMap() != null) {
                System.out.println("Map and scenario selected. UI Updated.");
                simulationUpdateTimer.play(); // Inicia o temporizador de atualização do simulador
            } else {
                System.out.println("No map and scenario selected or dialog cancelled.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error Loading Dialog", "Could not load the Select Map & Scenario dialog: " + e.getMessage());
        }
    }

    private void updateMapInfoPlaceholderLabel() {
        if (mapInfoPlaceholder != null) {
            Map currentMap = appSession.getCurrentMap();
            Scenario currentScenario = appSession.getCurrentScenario();

            if (currentMap != null && currentScenario != null) {
                String mapName = currentMap.getNameID();
                String scenarioDisplayName = getScenarioDisplayName(mapName, currentScenario.getNameID()); // Usa o helper
                mapInfoPlaceholder.setText(mapName + " (" + scenarioDisplayName + ")");
                mapInfoPlaceholder.setStyle("-fx-text-fill: #667eea; -fx-font-weight: bold;");
            } else {
                mapInfoPlaceholder.setText("Nenhum Mapa Selecionado");
                mapInfoPlaceholder.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
            }
        }
    }




    @FXML
    void handleSaveGame(ActionEvent event) {
        if (simulatorController == null) {
            simulatorController = new SimulatorController();
        }

        // Create a dialog to get the save name
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Save Game");
        dialog.setHeaderText("Enter a name for your saved game");
        dialog.setContentText("Save name:");

        // Show dialog and wait for response
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(saveName -> {
            if (simulatorController.saveGame(saveName)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Game saved successfully!");
                updateBudgetDisplay(); // Refresh UI
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save game. Please try again.");
            }
        });
    }

    @FXML
    void handleLoadGame(ActionEvent event) {
        if (simulatorController == null) {
            simulatorController = new SimulatorController();
        }

        List<String> savedGames = simulatorController.getSavedGames();
        if (savedGames.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Saved Games", "There are no saved games available.");
            return;
        }

        // Create a dialog to select the game to load
        ChoiceDialog<String> dialog = new ChoiceDialog<>(savedGames.get(0), savedGames);
        dialog.setTitle("Load Game");
        dialog.setHeaderText("Select a saved game to load");
        dialog.setContentText("Saved games:");

        // Show dialog and wait for response
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(saveName -> {
            if (confirm("Load Game", "Are you sure you want to load this game? Any unsaved progress will be lost.")) {
                if (simulatorController.loadGame(saveName)) {
                    // Update ApplicationSession with the new scenario and its associated map
                    Scenario loadedScenario = simulatorController.getScenarioRepository().getCurrentScenario();
                    Map loadedMap = loadedScenario.getMap();
                    appSession.setCurrentScenario(loadedScenario);
                    appSession.setCurrentMap(loadedMap);

                    System.out.println("handleLoadGame: appSession.currentScenario set to: " + (appSession.getCurrentScenario() != null ? appSession.getCurrentScenario().getNameID() : "null"));
                    System.out.println("handleLoadGame: appSession.currentMap set to: " + (appSession.getCurrentMap() != null ? appSession.getCurrentMap().getNameID() : "null"));

                    Platform.runLater(() -> {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Game loaded successfully!");
                        updateBudgetDisplay(); // Refresh UI
                        updateMapInfoPlaceholderLabel(); // Refresh map info
                        updateSimulatorTimeDisplay(); // Refresh simulator time
                        loadMapVisualization(); // Refresh map visualization
                        updateMenuItemsState(); // Refresh menu item states
                        simulationUpdateTimer.play(); // Start or resume the simulation timer
                    });
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to load game. Please try again.");
                }
            }
        });
    }

    @FXML
    void handleLogout(ActionEvent event) {
        System.out.println("Logout clicked");
        try {
            switchMenusGUI(event, "loginmenu", "Railway App");
            showAlert("Logout sucessfull", "You have successfully logged out of the Railway System Management.");
        } catch (IOException e) {
            e.printStackTrace(); // Para debug
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not open main menu: " + e.getMessage());
        }    }

    @FXML
    void handleBuildStation(ActionEvent event) {
        System.out.println("Build Station clicked");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BuildStationDialog.fxml"));
            Parent dialogRoot = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Build Station");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(playerMainPane.getScene().getWindow());

            Scene scene = new Scene(dialogRoot);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            dialogStage.showAndWait();

            // After dialog closes, update the budget display and map visualization
            updateBudgetDisplay();
            if (appSession.getCurrentMap() != null) {
                loadMapVisualization();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open Build Station dialog: " + e.getMessage());
        }
    }



    @FXML
    void handleUpgradeStation(ActionEvent event) {
        System.out.println("Upgrade Station clicked");

        // First, show the station selection dialog
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SelectStationDialog.fxml"));
            Parent dialogRoot = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Select Station");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(playerMainPane.getScene().getWindow());

            Scene scene = new Scene(dialogRoot);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            dialogStage.showAndWait();

            // After station selection dialog closes, check if a station was selected
            if (ApplicationSession.getInstance().getCurrentStation() != null) {
                // Now show the upgrade station dialog
                loader = new FXMLLoader(getClass().getResource("/fxml/UpgradeStationDialog.fxml"));
                dialogRoot = loader.load();

                dialogStage = new Stage();
                dialogStage.setTitle("Upgrade Station");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(playerMainPane.getScene().getWindow());

                scene = new Scene(dialogRoot);
                dialogStage.setScene(scene);
                dialogStage.setResizable(false);

                dialogStage.showAndWait();

                // After dialog closes, update the budget display and map visualization
                updateBudgetDisplay();
                if (appSession.getCurrentMap() != null) {
                    loadMapVisualization();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Could not open dialog: " + e.getMessage());
        }
    }

    @FXML
    void handleBuildRailwayLine(ActionEvent event) {
        System.out.println("Build Railway Line clicked");

        if (appSession.getCurrentMap() == null) {
            showAlert(Alert.AlertType.WARNING, "No Map Selected",
                    "Please select a map and scenario first.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BuildRailwayLineDialog.fxml"));
            Parent dialogRoot = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Build Railway Line");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(playerMainPane.getScene().getWindow());

            Scene scene = new Scene(dialogRoot);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            dialogStage.showAndWait();

            // After dialog closes, update the budget display and map visualization
            updateBudgetDisplay();
            if (appSession.getCurrentMap() != null) {
                loadMapVisualization();
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Could not open Build Railway Line dialog: " + e.getMessage());
        }
    }

    @FXML
    void handleBuyLocomotive(ActionEvent event) {
        System.out.println("Buy Locomotive clicked");
        if (appSession.getCurrentMap() == null) {
            showAlert(Alert.AlertType.WARNING, "No Map Selected",
                    "Please select a map and scenario first.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BuyLocomotiveDialog.fxml"));
            Parent dialogRoot = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Buy Locomotive");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(playerMainPane.getScene().getWindow());

            Scene scene = new Scene(dialogRoot);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            dialogStage.showAndWait();

            // After dialog closes, update the budget display and map visualization
            updateBudgetDisplay();
            if (appSession.getCurrentMap() != null) {
                loadMapVisualization();
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open Buy Locomotive dialog: " + e.getMessage());
        }

    }

    @FXML
    private void handleCreateRoute(ActionEvent event) {
        openDialog("/fxml/CreateRouteDialog.fxml", "Create New Route");
    }

    @FXML
    private void handleAssignTrainToRoute(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AssignTrainDialog.fxml"));
            Parent root = loader.load();
            
            // Get the controller
            AssignTrainToRouteDialogController controller = loader.getController();
            
            // Create and show the dialog
            Stage dialogStage = createDialogStage("Assign Train to Route", root);
            dialogStage.showAndWait();
            
            // Check if assignment was successful
            if (controller.isSuccessful()) {
                displayCargoForRoute(controller.getSelectedRoute());
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open Assign Train dialog: " + e.getMessage());
        }
    }

    // O método 'displayCargoForRoute' permanece o mesmo da correção anterior.
    private void displayCargoForRoute(Route route) {
        if (route == null) return;

        AssignTrainController cargoController = new AssignTrainController();

        // CORREÇÃO: Use o nome completo java.util.Map para evitar conflito.
        java.util.Map<Station, java.util.List<Cargo>> stationCargo = cargoController.getCargoesToPickUp(route);

        String cargoDetails = "No cargo to be picked up on this route.";
        if (stationCargo != null && !stationCargo.isEmpty()) {
            cargoDetails = stationCargo.entrySet().stream()
                    .map(entry -> "Station: " + entry.getKey().getNameID() + "\n" +
                            entry.getValue().stream()
                                    .map(Object::toString)
                                    .collect(java.util.stream.Collectors.joining("\n  - ", "  - ", "")))
                    .collect(java.util.stream.Collectors.joining("\n\n"));
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Assignment Successful");
        alert.setHeaderText("Train assigned to route '" + route.getNameID() + "'.");

        TextArea textArea = new TextArea("Cargo to be picked up:\n\n" + cargoDetails);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        alert.getDialogPane().setContent(textArea);
        alert.setResizable(true);
        alert.showAndWait();
    }

    // Método utilitário para abrir diálogos genéricos
    private void openDialog(String fxmlPath, String title) {
        try {
            URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) {
                throw new IOException("Cannot find FXML file: " + fxmlPath);
            }

            Parent root = FXMLLoader.load(fxmlUrl);
            // CORREÇÃO: Já não passamos o 'event'
            Stage dialogStage = createDialogStage(title, root);
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            AlertHelper.showAlert(Alert.AlertType.ERROR, "UI Error", "Could not open the requested dialog.\n" + e.getMessage());
        }
    }

    // Método utilitário para configurar um Stage de diálogo
    private Stage createDialogStage(String title, Parent root) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle(title);
        dialogStage.initModality(Modality.WINDOW_MODAL);

        // CORREÇÃO: Obter a janela a partir de um Node conhecido (playerMainPane)
        // em vez de usar o event source.
        if (playerMainPane != null && playerMainPane.getScene() != null) {
            dialogStage.initOwner(playerMainPane.getScene().getWindow());
        }

        dialogStage.setScene(new Scene(root));
        dialogStage.setResizable(false);
        return dialogStage;
    }


    @FXML
    void handleViewCurrentMap(ActionEvent event) {
        System.out.println("View Current Map clicked");
        if (appSession.getCurrentMap() != null) {
            loadMapVisualization();
        } else {
            showAlert("No Map", "Please select a map and scenario first.");
        }
    }

    @FXML
    void handleListStations(ActionEvent event) {
        System.out.println("View Stations Details clicked");

        if (appSession.getCurrentMap() == null) {
            showAlert(Alert.AlertType.WARNING, "No Map Selected",
                    "Please select a map and scenario first.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ListStationsDialog.fxml"));
            Parent dialogRoot = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Station List");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(playerMainPane.getScene().getWindow());

            Scene scene = new Scene(dialogRoot);
            dialogStage.setScene(scene);
            dialogStage.setResizable(true);

            dialogStage.showAndWait();

            // After dialog closes, update the budget display and map visualization
            // in case any upgrades were performed
            updateBudgetDisplay();
            if (appSession.getCurrentMap() != null) {
                loadMapVisualization();
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Could not open Station List dialog: " + e.getMessage());
        }
    }

    @FXML
    void handleListTrains(ActionEvent event) {
        System.out.println("View Trains Details clicked");

        if (appSession.getCurrentMap() == null) {
            showAlert(Alert.AlertType.WARNING, "No Map Selected",
                    "Please select a map and scenario first.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ListTrainsDialog.fxml"));
            Parent dialogRoot = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Train List");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(playerMainPane.getScene().getWindow());

            Scene scene = new Scene(dialogRoot);
            dialogStage.setScene(scene);
            dialogStage.setResizable(true);

            dialogStage.showAndWait();

            // After dialog closes, update the budget display and map visualization
            // in case any upgrades were performed
            updateBudgetDisplay();
            if (appSession.getCurrentMap() != null) {
                loadMapVisualization();
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Could not open Station List dialog: " + e.getMessage());
        }
    }

    @FXML
    void handleViewConnectivity(ActionEvent event) {
        System.out.println("Check Network Connectivity clicked");
        showAlert("Not Implemented", "Check Network Connectivity (US13) functionality is not yet implemented.");
    }

    @FXML
    void handleViewMaintenanceRoute(ActionEvent event) {
        System.out.println("View Maintenance Route clicked");
        showAlert("Not Implemented", "View Maintenance Route (US14) functionality is not yet implemented.");
    }

    @FXML
    void handleViewShortestRoute(ActionEvent event) {
        System.out.println("Find Shortest Route clicked");
        showAlert("Not Implemented", "Find Shortest Route (US27) functionality is not yet implemented.");
    }

    @FXML
    void handleViewFinancialResults(ActionEvent event) {
        System.out.println("View Year Financial Results clicked");
        showAlert("Not Implemented", "View Year Financial Results (US25) functionality is not yet implemented.");
    }

    /**
     * Gere o evento de clique para o botão de pausa/retoma da simulação.
     * Este método verifica o estado atual do simulador e invoca a ação apropriada.
     * @param event O evento de ação que acionou este manipulador.
     */
    @FXML
    void handleRunPauseSimulator(ActionEvent event) {
        // Obter uma instância do controlador do simulador

        // Obter o estado real do simulador a partir do controlador
        String status = simulatorController.getSimulatorStatus();

        // Se a simulação não tiver sido iniciada ou já tiver parado, informar o utilizador.
        if (status == null || status.equals(Simulator.STATUS_STOPPED)) {
            showAlert("No Simulator", "Please start the simulator first.");
            return;
        }

        // Executar uma ação com base no estado atual
        switch (status) {
            case Simulator.STATUS_RUNNING:
                // Se estiver a decorrer, colocar em pausa
                if (simulatorController.pauseSimulation()) {
                    showAlert("Simulator paused", "The simulation has been paused.");
                } else {
                    showAlert("Error", "Error pausing the simulation.");
                }
                break;

            case Simulator.STATUS_PAUSED:
                // Se estiver em pausa, retomar
                if (simulatorController.resumeSimulation()) {
                    showAlert("Simulator resumed", "The simulation has been resumed.");
                } else {
                    showAlert("Error", "Error resuming the simulation.");
                }
                break;
        }

        // A atualização da interface do utilizador (ex: texto do botão, etiqueta de estado) deve ser gerida
        // por um temporizador separado que verifica periodicamente o estado do simulador para manter a interface do utilizador sincronizada.
    }

    @FXML
    void handleAbout(ActionEvent event) {
        System.out.println("About clicked");
        showAlert("About", "Railway Management System\nVersion: 1.0 (PI Sem2 2024-25)\nDeveloped by: Group 112");
    }

    // --- Utility Methods ---
    private void loadViewToContentArea(String fxmlPath) {
        try {
            Parent viewRoot = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().setAll(viewRoot);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error Loading View", "Could not load view: " + fxmlPath);
        }
    }

    private void loadMapVisualization() {
        contentArea.getChildren().clear();

        Map currentMap = appSession.getCurrentMap();
        Scenario currentScenario = appSession.getCurrentScenario();

        System.out.println("loadMapVisualization: Current Map = " + (currentMap != null ? currentMap.getNameID() : "null"));
        System.out.println("loadMapVisualization: Current Scenario = " + (currentScenario != null ? currentScenario.getNameID() : "null"));

        if (currentMap != null && currentScenario != null) {
            System.out.println("loadMapVisualization: Map cities count = " + currentMap.getCities().size());
            System.out.println("loadMapVisualization: Map stations count = " + currentMap.getStations().size());
            System.out.println("loadMapVisualization: Map industries count = " + currentMap.getIndustries().size());

            if (this.viewLayoutController == null) {
                this.viewLayoutController = new ViewScenarioLayoutController();
            }
            ViewScenarioLayoutController.MapLayoutData layoutData = viewLayoutController.getMapLayoutData(currentMap, currentScenario);

            if (layoutData == null) {
                Label errorLabel = new Label("Error generating map visualization data.");
                errorLabel.setStyle("-fx-text-fill: red;");
                contentArea.getChildren().add(errorLabel);
                StackPane.setAlignment(errorLabel, Pos.CENTER);
                System.err.println("loadMapVisualization: Error generating map layout data.");
                return;
            }

            // Create a grid pane for the background
            GridPane mapGrid = new GridPane();
            mapGrid.setHgap(0);
            mapGrid.setVgap(0);
            mapGrid.setStyle("-fx-background-color: transparent;"); // Make grid background transparent
            mapGrid.setPadding(new Insets(20));
            mapGrid.setAlignment(Pos.CENTER);
            mapGrid.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
            mapGrid.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

            // Cell size for the grid
            double cellSize = 40;
            double iconSize = 32; // Size for the icons

            // Try to load icons, but don't fail if they're not found
            Image cityIcon = null;
            Image industryIcon = null;
            Image stationIcon = null;
            try {
                // Try to load icons from different possible paths
                URL cityUrl = getClass().getResource("/city.png");
                URL industryUrl = getClass().getResource("/industry.png");
                URL stationUrl = getClass().getResource("/icons/railway-station.png");

                if (cityUrl == null) {
                    cityUrl = getClass().getResource("/icons/city.png");
                }
                if (industryUrl == null) {
                    industryUrl = getClass().getResource("/icons/industry.png");
                }
                if (stationUrl == null) {
                    stationUrl = getClass().getResource("/railway-station.png");
                }

                if (cityUrl != null) {
                    cityIcon = new Image(cityUrl.toExternalForm(), iconSize, iconSize, true, true);
                }
                if (industryUrl != null) {
                    industryIcon = new Image(industryUrl.toExternalForm(), iconSize, iconSize, true, true);
                }
                if (stationUrl != null) {
                    stationIcon = new Image(stationUrl.toExternalForm(), iconSize, iconSize, true, true);
                }
            } catch (Exception e) {
                System.out.println("Icons not found, using shapes instead. Error: " + e.getMessage());
            }

            // First pass: Add background cells and entities
            for (int y = 0; y < layoutData.height; y++) {
                for (int x = 0; x < layoutData.width; x++) {
                    ViewScenarioLayoutController.CellData cellData = layoutData.grid[y][x];

                    // Create a cell with border
                    Rectangle cell = new Rectangle(cellSize, cellSize);
                    cell.setFill(Color.TRANSPARENT);
                    cell.setStroke(Color.LIGHTGRAY);
                    cell.setStrokeWidth(0.5);
                    mapGrid.add(cell, x, y);

                    // Add entities based on type
                    Node entityNode = null;
                    switch (cellData.type.toString()) {
                        case "CITY":
                            if (cityIcon != null) {
                                ImageView cityView = new ImageView(cityIcon);
                                cityView.setFitWidth(iconSize);
                                cityView.setFitHeight(iconSize);
                                entityNode = cityView;
                            } else {
                                Rectangle cityRect = new Rectangle(cellSize * 0.8, cellSize * 0.8);
                                cityRect.setFill(Color.RED);
                                cityRect.setArcHeight(10);
                                cityRect.setArcWidth(10);
                                entityNode = cityRect;
                            }
                            break;
                        case "INDUSTRY":
                            if (industryIcon != null) {
                                ImageView industryView = new ImageView(industryIcon);
                                industryView.setFitWidth(iconSize);
                                industryView.setFitHeight(iconSize);
                                entityNode = industryView;
                            } else {
                                Polygon industry = createIndustryShape(cellSize);
                                industry.setFill(Color.BLUE);
                                entityNode = industry;
                            }
                            break;
                        case "STATION":
                            if (stationIcon != null) {
                                ImageView stationView = new ImageView(stationIcon);
                                stationView.setFitWidth(iconSize);
                                stationView.setFitHeight(iconSize);
                                entityNode = stationView;
                            } else {
                                Circle station = new Circle(cellSize * 0.4);
                                station.setFill(Color.GREEN);
                                entityNode = station;
                            }
                            break;
                        default:
                            break;
                    }

                    if (entityNode != null) {
                        StackPane cellContent = new StackPane(entityNode);
                        cellContent.setMinSize(cellSize, cellSize);
                        cellContent.setPrefSize(cellSize, cellSize);
                        mapGrid.add(cellContent, x, y);
                    }
                }
            }

            // Create a container with exact size
            VBox mapContainer = new VBox();
            mapContainer.setMaxSize(layoutData.width * cellSize + 40, layoutData.height * cellSize + 40);
            mapContainer.setPrefSize(layoutData.width * cellSize + 40, layoutData.height * cellSize + 40);
            mapContainer.setMinSize(layoutData.width * cellSize + 40, layoutData.height * cellSize + 40);
            mapContainer.setStyle("-fx-background-color: transparent;"); // Make container background transparent
            mapContainer.setAlignment(Pos.CENTER);

            // Create a white background pane
            Pane backgroundPane = new Pane();
            backgroundPane.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);");
            backgroundPane.setMaxSize(layoutData.width * cellSize + 40, layoutData.height * cellSize + 40);
            backgroundPane.setPrefSize(layoutData.width * cellSize + 40, layoutData.height * cellSize + 40);
            backgroundPane.setMinSize(layoutData.width * cellSize + 40, layoutData.height * cellSize + 40);

            // Create a Pane for the railway lines that will overlay the background
            Pane railwayLinesPane = new Pane();
            railwayLinesPane.setMaxSize(layoutData.width * cellSize + 40, layoutData.height * cellSize + 40);
            railwayLinesPane.setPrefSize(layoutData.width * cellSize + 40, layoutData.height * cellSize + 40);
            railwayLinesPane.setMinSize(layoutData.width * cellSize + 40, layoutData.height * cellSize + 40);
            railwayLinesPane.setStyle("-fx-background-color: transparent;");
            railwayLinesPane.setMouseTransparent(true);

            // Add the railway lines to the overlay pane
            RailwayLineRepository railwayLineRepository = Repositories.getInstance().getRailwayLineRepository();
            List<RailwayLine> railwayLines = railwayLineRepository.getAll();

            if (railwayLines != null) {
                for (RailwayLine line : railwayLines) {
                    Station station1 = line.getStartStation();
                    Station station2 = line.getEndStation();

                    // Calculate positions for the line, accounting for the grid padding (20px)
                    double x1 = (station1.getPosition().getX() * cellSize) + cellSize/2 + 20;
                    double y1 = (station1.getPosition().getY() * cellSize) + cellSize/2 + 20;
                    double x2 = (station2.getPosition().getX() * cellSize) + cellSize/2 + 20;
                    double y2 = (station2.getPosition().getY() * cellSize) + cellSize/2 + 20;

                    Color lineColor = line.isElectrified() ? Color.DEEPSKYBLUE : Color.PURPLE;
                    double strokeWidth = 4;

                    if (line.isDoubleTrack()) {
                        double angle = Math.atan2(y2 - y1, x2 - x1);
                        double perpAngle = angle + Math.PI / 2;
                        double offset = 2.5; // This controls the space between tracks

                        // Create two thinner lines offset from the center
                        Line track1 = new Line();
                        track1.setStartX(x1 + offset * Math.cos(perpAngle));
                        track1.setStartY(y1 + offset * Math.sin(perpAngle));
                        track1.setEndX(x2 + offset * Math.cos(perpAngle));
                        track1.setEndY(y2 + offset * Math.sin(perpAngle));
                        track1.setStroke(lineColor);
                        track1.setStrokeWidth(strokeWidth / 1.5);
                        track1.setStrokeLineCap(StrokeLineCap.ROUND);

                        Line track2 = new Line();
                        track2.setStartX(x1 - offset * Math.cos(perpAngle));
                        track2.setStartY(y1 - offset * Math.sin(perpAngle));
                        track2.setEndX(x2 - offset * Math.cos(perpAngle));
                        track2.setEndY(y2 - offset * Math.sin(perpAngle));
                        track2.setStroke(lineColor);
                        track2.setStrokeWidth(strokeWidth / 1.5);
                        track2.setStrokeLineCap(StrokeLineCap.ROUND);

                        railwayLinesPane.getChildren().addAll(track1, track2);

                    } else {
                        // Create a single, thicker line in the center
                        Line railLine = new Line(x1, y1, x2, y2);
                        railLine.setStroke(lineColor);
                        railLine.setStrokeWidth(strokeWidth);
                        railLine.setStrokeLineCap(StrokeLineCap.ROUND);
                        railwayLinesPane.getChildren().add(railLine);
                    }
                }
            }

            // Add the grid to a centering container
            StackPane centeringPane = new StackPane();
            centeringPane.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
            centeringPane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
            centeringPane.setAlignment(Pos.CENTER);

            // Add all layers in the correct order: background, railway lines, then grid with entities
            centeringPane.getChildren().addAll(backgroundPane, railwayLinesPane, mapGrid);

            mapContainer.getChildren().add(centeringPane);

            // Create a scroll pane to hold the map
            ScrollPane scrollPane = new ScrollPane(mapContainer);
            scrollPane.setFitToWidth(false);
            scrollPane.setFitToHeight(false);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setStyle("-fx-background-color:transparent; -fx-background:transparent; -fx-background-insets: 0;");
            scrollPane.setPannable(true);

            // Center the scroll pane in the content area
            StackPane scrollPaneContainer = new StackPane(scrollPane);
            scrollPaneContainer.setMaxSize(layoutData.width * cellSize + 60, layoutData.height * cellSize + 60); // Add extra space for scroll bars
            scrollPaneContainer.setPrefSize(layoutData.width * cellSize + 60, layoutData.height * cellSize + 60);
            scrollPaneContainer.setMinSize(layoutData.width * cellSize + 60, layoutData.height * cellSize + 60);

            contentArea.getChildren().add(scrollPaneContainer);
            StackPane.setAlignment(scrollPaneContainer, Pos.CENTER);

            System.out.println("Map visualization loaded for: " + currentMap.getNameID());
        } else {
            VBox noMapContainer = new VBox(15);
            noMapContainer.setAlignment(Pos.CENTER);
            Label noMapLabel = new Label("No map selected.");
            noMapLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");
            Label instructionLabel = new Label("Please select a map and scenario through 'File > Select Map & Scenario'.");
            instructionLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
            noMapContainer.getChildren().addAll(noMapLabel, instructionLabel);
            contentArea.getChildren().add(noMapContainer);
            StackPane.setAlignment(noMapContainer, Pos.CENTER);
        }
    }

    private Polygon createIndustryShape(double size) {
        double halfSize = size * 0.4;
        Polygon industry = new Polygon(
                -halfSize, halfSize,    // Bottom left
                0, -halfSize,          // Top middle
                halfSize, halfSize     // Bottom right
        );
        industry.setStroke(Color.BLUE);
        industry.setStrokeWidth(1);
        return industry;
    }

    private void openModalDialog(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent dialogRoot = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            // dialogStage.initOwner(playerMainPane.getScene().getWindow()); // Set owner if needed

            Scene scene = new Scene(dialogRoot);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error Opening Dialog", "Could not open dialog: " + fxmlPath);
        }
    }

    private void showAlert(String title, String message) {
        // System.out.println("[" + title + "] " + message); // Keep console log if desired
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header text
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); // Removido header text para melhor aparência
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Switch menus gui - VERSÃO CORRIGIDA
     *
     * @param event    the event
     * @param fileName the file name
     * @param menuName the menu name
     * @throws IOException the io exception
     */
    public void switchMenusGUI(ActionEvent event, String fileName, String menuName) throws IOException {
        String fxmlFileNameWithExtension = fileName + ".fxml";
        String resourcePath = "/fxml/" + fxmlFileNameWithExtension;

        URL fxmlUrl = getClass().getResource(resourcePath);

        Parent newRoot = FXMLLoader.load(fxmlUrl);

        // Get the current stage
        Stage currentStage = (Stage) playerMainPane.getScene().getWindow();

        // Criar nova Scene com as dimensões corretas
        Scene newScene;

        if (fileName.equals("loginmenu")) {
            newScene = new Scene(newRoot); // A Scene tentará usar o tamanho preferido do newRoot
            currentStage.setScene(newScene);
            currentStage.setTitle(menuName);

            // Redimensionar explicitamente o STAGE para a tela de login
            currentStage.setWidth(450);    // Largura definida no FXML do Login
            currentStage.setHeight(500);   // Altura definida no FXML do Login
            currentStage.setMinWidth(450);
            currentStage.setMinHeight(500);
            currentStage.setMaximized(false);
            currentStage.setResizable(false);

        } else {
            newScene = new Scene(newRoot, 1920, 1080);

            currentStage.setMinWidth(1000);
            currentStage.setMinHeight(700);
        }

        // Definir o título
        currentStage.setTitle(menuName);

        // Definir a nova Scene
        currentStage.setScene(newScene);

        // Tornar a janela redimensionável
        currentStage.setResizable(true);

        // Mostrar a janela
        currentStage.show();

        System.out.println("Navegação para '" + menuName + "' concluída com sucesso.");
    }

    private boolean confirm(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}