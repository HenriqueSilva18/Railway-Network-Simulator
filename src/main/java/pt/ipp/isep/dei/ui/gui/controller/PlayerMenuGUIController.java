package pt.ipp.isep.dei.ui.gui.controller;

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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pt.ipp.isep.dei.controller.template.ApplicationSession;
import pt.ipp.isep.dei.controller.template.ViewScenarioLayoutController;
import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.domain.template.Player;
import pt.ipp.isep.dei.domain.template.Scenario;

import javafx.scene.control.ScrollPane; // Certifique-se que este import existe
import javafx.geometry.Insets;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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
    private MenuItem stationProfitAnalysisMenuItem; // Main menu
    @FXML
    private MenuItem passengerArrivalsAnalysisMenuItem; // Main menu
    @FXML
    private MenuItem cargoArrivalsAnalysisMenuItem; // Main menu
    @FXML
    private MenuItem distributionAnalysisMenuItem; // Main menu
    @FXML
    private MenuItem cargoRevenueAnalysisMenuItem; // Main menu

    @FXML
    private MenuItem aboutMenuItem;

    @FXML
    private StackPane contentArea;

    @FXML
    private Label budgetLabel;
    @FXML
    private Label simulatorTimeLabel;

    // Cards VBox elements from FXML
    @FXML
    private VBox infrastructureCard;
    @FXML
    private VBox operationsCard;
    @FXML
    private VBox viewCard;
    @FXML
    private VBox simulationCard; // Added for "Simulation Control" card
    @FXML
    private VBox financialCard;  // Added for "Financial Reports" card
    @FXML
    private VBox statisticsCard;

    @FXML
    private Label mapInfoPlaceholder; // Certifique-se que este fx:id existe no FXML



    // Context Menu Items - Instance variables to hold references
    // Infrastructure Card
    private MenuItem cardBuildStationItem;
    private MenuItem cardUpgradeStationItem;
    private MenuItem cardBuildRailwayItem;

    // Operations Card
    private MenuItem cardBuyLocomotiveItem;
    private MenuItem cardCreateRouteItem;
    private MenuItem cardAssignTrainItem;
    private MenuItem cardRunSimulatorItem;

    // View Card
    private MenuItem cardViewMapItem;
    private MenuItem cardListStationsItem;
    private MenuItem cardListTrainsItem;
    private MenuItem cardViewConnectivityItem;
    private MenuItem cardMaintenanceRouteItem;
    private MenuItem cardShortestRouteItem;
    private MenuItem cardFinancialResultsItem;

    // Simulation Card (New - placeholders)
    private MenuItem cardSimControlRunPauseItem;
    private MenuItem cardSimAdvanceTimeItem;


    // Financial Card (New - placeholders)
    private MenuItem cardFinViewReportItem;
    private MenuItem cardFinExportDataỊtem;


    // Statistics Card
    private MenuItem cardStationProfitItem;
    private MenuItem cardPassengerArrivalsItem;
    private MenuItem cardCargoArrivalsItem;
    private MenuItem cardDistributionItem;
    private MenuItem cardCargoRevenueItem;


    private ApplicationSession appSession = ApplicationSession.getInstance();
    private ViewScenarioLayoutController viewLayoutController; // Controller para obter dados do mapa


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.viewLayoutController = new ViewScenarioLayoutController(); // Inicializar o controller de layout

        updateBudgetDisplay();
        updateSimulatorTimeDisplay("Paused");
        setupCardContextMenus();
        updateMenuItemsState();
        updateMapInfoPlaceholderLabel(); // Atualiza o placeholder no início
        loadMapVisualization();          // Tenta carregar a visualização do mapa no início
    }

    private void setupCardContextMenus() {
        setupInfrastructureCardMenu();
        setupOperationsCardMenu();
        setupViewCardMenu();
        setupSimulationCardMenu();   // New Call
        setupFinancialCardMenu();    // New Call
        setupStatisticsCardMenu();
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
    void handleSimulationCardClick() { // NEWLY ADDED
        System.out.println("Simulation Control card clicked - context menu should show via setOnMouseClicked");
    }

    @FXML
    void handleFinancialCardClick() { // NEWLY ADDED
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

            contextMenu.getItems().addAll(cardBuildStationItem, cardUpgradeStationItem,
                    new SeparatorMenuItem(), cardBuildRailwayItem);

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

            // Note: This item also exists on the main menu bar.
            // And potentially on the new "Simulation Control" card.
            // Ensure consistent behavior or specific actions as needed.
            cardRunSimulatorItem = new MenuItem("Run/Pause Simulator (Ops)");
            cardRunSimulatorItem.setOnAction(this::handleRunPauseSimulator);

            contextMenu.getItems().addAll(cardBuyLocomotiveItem, cardCreateRouteItem, cardAssignTrainItem,
                    new SeparatorMenuItem(), cardRunSimulatorItem);

            setupCardHoverEffect(operationsCard);
            operationsCard.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY || event.getButton() == MouseButton.SECONDARY) {
                    contextMenu.show(operationsCard, event.getScreenX(), event.getScreenY());
                }
            });
        }
    }

    private void setupViewCardMenu() {
        if (viewCard != null) {
            ContextMenu contextMenu = new ContextMenu();

            cardViewMapItem = new MenuItem("View Current Map");
            cardViewMapItem.setOnAction(this::handleViewCurrentMap);

            cardListStationsItem = new MenuItem("List Stations");
            cardListStationsItem.setOnAction(this::handleListStations);

            cardListTrainsItem = new MenuItem("List Trains");
            cardListTrainsItem.setOnAction(this::handleListTrains);

            cardViewConnectivityItem = new MenuItem("Network Connectivity");
            cardViewConnectivityItem.setOnAction(this::handleViewConnectivity);

            cardMaintenanceRouteItem = new MenuItem("Maintenance Route");
            cardMaintenanceRouteItem.setOnAction(this::handleViewMaintenanceRoute);

            cardShortestRouteItem = new MenuItem("Shortest Route");
            cardShortestRouteItem.setOnAction(this::handleViewShortestRoute);

            // Note: This item also exists on the main menu bar.
            // And potentially on the new "Financial Reports" card.
            cardFinancialResultsItem = new MenuItem("Financial Results (View)");
            cardFinancialResultsItem.setOnAction(this::handleViewFinancialResults);

            contextMenu.getItems().addAll(
                    cardViewMapItem, new SeparatorMenuItem(),
                    cardListStationsItem, cardListTrainsItem, new SeparatorMenuItem(),
                    cardViewConnectivityItem, cardMaintenanceRouteItem, cardShortestRouteItem,
                    new SeparatorMenuItem(), cardFinancialResultsItem
            );

            setupCardHoverEffect(viewCard);
            viewCard.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY || event.getButton() == MouseButton.SECONDARY) {
                    contextMenu.show(viewCard, event.getScreenX(), event.getScreenY());
                }
            });
        }
    }

    /**
     * Configura o menu de contexto para o card de Simulation Control (NOVO)
     */
    private void setupSimulationCardMenu() {
        if (simulationCard != null) {
            ContextMenu contextMenu = new ContextMenu();

            // Example: Reusing the main run/pause simulator action
            cardSimControlRunPauseItem = new MenuItem("Run/Pause Simulation");
            cardSimControlRunPauseItem.setOnAction(this::handleRunPauseSimulator); // Reuses main handler

            cardSimAdvanceTimeItem = new MenuItem("Advance Time by 1 Step");
            cardSimAdvanceTimeItem.setOnAction(event -> showAlert("Not Implemented", "Advance Time (US_SIM_X) not implemented."));

            // Add more simulation-specific actions here
            contextMenu.getItems().addAll(cardSimControlRunPauseItem, cardSimAdvanceTimeItem);

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


    private void setupStatisticsCardMenu() {
        if (statisticsCard != null) {
            ContextMenu contextMenu = new ContextMenu();

            cardStationProfitItem = new MenuItem("Station Profit Analysis");
            cardStationProfitItem.setOnAction(this::handleStationProfitAnalysis);

            cardPassengerArrivalsItem = new MenuItem("Passenger Arrivals Analysis");
            cardPassengerArrivalsItem.setOnAction(this::handlePassengerArrivalsAnalysis);

            cardCargoArrivalsItem = new MenuItem("Cargo Arrivals Analysis");
            cardCargoArrivalsItem.setOnAction(this::handleCargoArrivalsAnalysis);

            cardDistributionItem = new MenuItem("Distribution Analysis");
            cardDistributionItem.setOnAction(this::handleDistributionAnalysis);

            cardCargoRevenueItem = new MenuItem("Cargo Revenue Analysis");
            cardCargoRevenueItem.setOnAction(this::handleCargoRevenueAnalysis);

            contextMenu.getItems().addAll(cardStationProfitItem, cardPassengerArrivalsItem, cardCargoArrivalsItem,
                    new SeparatorMenuItem(), cardDistributionItem, cardCargoRevenueItem);

            setupCardHoverEffect(statisticsCard);
            statisticsCard.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY || event.getButton() == MouseButton.SECONDARY) {
                    contextMenu.show(statisticsCard, event.getScreenX(), event.getScreenY());
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
        // Assuming appSession.isMapLoaded() is the correct way to check
        boolean mapLoaded = true;
        // If appSession doesn't have isMapLoaded(), use a placeholder for now:
        // boolean mapLoaded = false; // or true, for testing

        // Infrastructure Card Items
        if (cardBuildStationItem != null) cardBuildStationItem.setDisable(!mapLoaded);
        if (cardUpgradeStationItem != null) cardUpgradeStationItem.setDisable(!mapLoaded);
        if (cardBuildRailwayItem != null) cardBuildRailwayItem.setDisable(!mapLoaded);

        // Operations Card Items
        if (cardBuyLocomotiveItem != null) cardBuyLocomotiveItem.setDisable(!mapLoaded);
        if (cardCreateRouteItem != null) cardCreateRouteItem.setDisable(!mapLoaded);
        if (cardAssignTrainItem != null) cardAssignTrainItem.setDisable(!mapLoaded);
        if (cardRunSimulatorItem != null) cardRunSimulatorItem.setDisable(!mapLoaded);

        // View Card Items
        if (cardViewMapItem != null) cardViewMapItem.setDisable(!mapLoaded);
        if (cardListStationsItem != null) cardListStationsItem.setDisable(!mapLoaded);
        if (cardListTrainsItem != null) cardListTrainsItem.setDisable(!mapLoaded);
        if (cardViewConnectivityItem != null) cardViewConnectivityItem.setDisable(!mapLoaded);
        if (cardMaintenanceRouteItem != null) cardMaintenanceRouteItem.setDisable(!mapLoaded);
        if (cardShortestRouteItem != null) cardShortestRouteItem.setDisable(!mapLoaded);
        if (cardFinancialResultsItem != null) cardFinancialResultsItem.setDisable(!mapLoaded);

        // Simulation Card Items (NEW)
        if (cardSimControlRunPauseItem != null) cardSimControlRunPauseItem.setDisable(!mapLoaded);
        if (cardSimAdvanceTimeItem != null) cardSimAdvanceTimeItem.setDisable(!mapLoaded); // Or other conditions

        // Financial Card Items (NEW)
        if (cardFinViewReportItem != null) cardFinViewReportItem.setDisable(!mapLoaded);
        if (cardFinExportDataỊtem != null) cardFinExportDataỊtem.setDisable(!mapLoaded); // Or other conditions


        // Statistics Card Items - These might be always enabled or depend on other factors
        // For now, let's assume they don't depend on mapLoaded, or if they do:
        if (cardStationProfitItem != null) cardStationProfitItem.setDisable(!mapLoaded); // Example
        if (cardPassengerArrivalsItem != null) cardPassengerArrivalsItem.setDisable(!mapLoaded); // Example
        if (cardCargoArrivalsItem != null) cardCargoArrivalsItem.setDisable(!mapLoaded); // Example
        if (cardDistributionItem != null) cardDistributionItem.setDisable(!mapLoaded); // Example
        if (cardCargoRevenueItem != null) cardCargoRevenueItem.setDisable(!mapLoaded); // Example
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

    private void updateMenuItemsState() {
        // TODO: Verificar se um mapa está carregado na ApplicationSession
        // boolean mapLoaded = appSession.getCurrentMap() != null;
        boolean mapLoaded = appSession.getCurrentMap() != null && appSession.getCurrentScenario() != null;

        // File Menu
        selectMapScenarioMenuItem.setDisable(mapLoaded); // Typically disable if a map is already loaded
        loadGameMenuItem.setDisable(mapLoaded); // Similar logic might apply
        saveGameMenuItem.setDisable(!mapLoaded);

        // Infrastructure Menu (Main Menu Bar)
        buildStationMenuItem.setDisable(!mapLoaded);
        upgradeStationMenuItem.setDisable(!mapLoaded);
        buildRailwayLineMenuItem.setDisable(!mapLoaded);

        // Operations Menu (Main Menu Bar)
        buyLocomotiveMenuItem.setDisable(!mapLoaded);
        createRouteMenuItem.setDisable(!mapLoaded);
        assignTrainMenuItem.setDisable(!mapLoaded);
        runPauseSimulatorMenuItem.setDisable(!mapLoaded);

        // View Menu (Main Menu Bar)
        viewCurrentMapMenuItem.setDisable(!mapLoaded);
        listStationsMenuItem.setDisable(!mapLoaded);
        listTrainsMenuItem.setDisable(!mapLoaded);
        viewConnectivityMenuItem.setDisable(!mapLoaded);
        viewMaintenanceRouteMenuItem.setDisable(!mapLoaded);
        viewShortestRouteMenuItem.setDisable(!mapLoaded);
        viewFinancialResultsMenuItem.setDisable(!mapLoaded);

        // Statistics Menu (Main Menu Bar) - Assuming these also depend on a map being loaded
        stationProfitAnalysisMenuItem.setDisable(!mapLoaded);
        passengerArrivalsAnalysisMenuItem.setDisable(!mapLoaded);
        cargoArrivalsAnalysisMenuItem.setDisable(!mapLoaded);
        distributionAnalysisMenuItem.setDisable(!mapLoaded);
        cargoRevenueAnalysisMenuItem.setDisable(!mapLoaded);

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
        System.out.println("Save Game clicked");
        showAlert("Not Implemented", "Save Game (US23) functionality is not yet implemented.");
    }

    @FXML
    void handleLoadGame(ActionEvent event) {
        System.out.println("Load Game clicked");
        // TODO: Implement US24 - Load a saved game
        // This should set mapLoaded to true and update UI
        showAlert("Not Implemented", "Load Game (US24) functionality is not yet implemented.");
        // After successful load:
        // appSession.setMapLoaded(true); // Or your equivalent logic
        // updateMenuItemsState();
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
        showAlert("Not Implemented", "Build Station (US05) functionality is not yet implemented.");
        // loadViewToContentArea("/pt/ipp/isep/dei/ui/gui/fxml/us05_BuildStation.fxml");
    }

    @FXML
    void handleUpgradeStation(ActionEvent event) {
        System.out.println("Upgrade Station clicked");
        showAlert("Not Implemented", "Upgrade Station (US06) functionality is not yet implemented.");
    }

    @FXML
    void handleBuildRailwayLine(ActionEvent event) {
        System.out.println("Build Railway Line clicked");
        showAlert("Not Implemented", "Build Railway Line (US08) functionality is not yet implemented.");
    }

    @FXML
    void handleBuyLocomotive(ActionEvent event) {
        System.out.println("Buy Locomotive clicked");
        showAlert("Not Implemented", "Buy Locomotive (US09) functionality is not yet implemented.");
    }

    @FXML
    void handleCreateRoute(ActionEvent event) {
        System.out.println("Create Route clicked");
        showAlert("Not Implemented", "Create Route (US10) functionality is not yet implemented.");
    }

    @FXML
    void handleAssignTrainToRoute(ActionEvent event) {
        System.out.println("Assign Train to Route clicked");
        showAlert("Not Implemented", "Assign Train to Route functionality is not yet implemented.");
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
        showAlert("Not Implemented", "View Stations Details (US07) functionality is not yet implemented.");
        // loadViewToContentArea("/pt/ipp/isep/dei/ui/gui/fxml/us07_ListStations.fxml");
    }

    @FXML
    void handleListTrains(ActionEvent event) {
        System.out.println("View Trains clicked");
        showAlert("Not Implemented", "View Trains (US11) functionality is not yet implemented.");
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

    private boolean isSimulatorRunning = false; // Basic state for toggle

    @FXML
    void handleRunPauseSimulator(ActionEvent event) {
        isSimulatorRunning = !isSimulatorRunning; // Toggle state
        String status = isSimulatorRunning ? "Running" : "Paused";
        System.out.println("Play/Pause Simulator clicked. Status: " + status);
        updateSimulatorTimeDisplay(status); // Update label
        // TODO: Add actual simulator start/pause logic (US12)
        showAlert("Simulator Control", "Simulator is now " + status + " (US12 - Basic Toggle).");
    }

    // Statistics Handlers (Python/Jupyter tasks)
    @FXML
    void handleStationProfitAnalysis(ActionEvent event) {
        System.out.println("Station Profit Analysis clicked");
        showAlert("Python/Jupyter Task", "US15: Perform statistical analysis of average annual profit (Python).");
    }

    @FXML
    void handlePassengerArrivalsAnalysis(ActionEvent event) {
        System.out.println("Passenger Arrivals Analysis clicked");
        showAlert("Python/Jupyter Task", "US16: Perform comparative analysis of passenger arrivals (Python).");
    }

    @FXML
    void handleCargoArrivalsAnalysis(ActionEvent event) {
        System.out.println("Cargo Arrivals Analysis clicked");
        showAlert("Python/Jupyter Task", "US17: Perform analysis of cargo arriving at each station (Python).");
    }

    @FXML
    void handleDistributionAnalysis(ActionEvent event) {
        System.out.println("Train/Passenger/Mail Distribution clicked");
        showAlert("Python/Jupyter Task", "US18: Perform analysis of train, passenger, and mail distribution (Python).");
    }

    @FXML
    void handleCargoRevenueAnalysis(ActionEvent event) {
        System.out.println("Cargo Revenue Correlation clicked");
        showAlert("Python/Jupyter Task", "US31: Perform statistical analysis for cargo revenue correlation (Python).");
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

        if (currentMap != null && currentScenario != null) {
            if (this.viewLayoutController == null) {
                this.viewLayoutController = new ViewScenarioLayoutController();
            }
            ViewScenarioLayoutController.MapLayoutData layoutData = viewLayoutController.getMapLayoutData(currentMap, currentScenario);

            if (layoutData == null) {
                Label errorLabel = new Label("Erro ao gerar dados do mapa para visualização.");
                errorLabel.setStyle("-fx-text-fill: red;");
                contentArea.getChildren().add(errorLabel);
                StackPane.setAlignment(errorLabel, Pos.CENTER);
                return;
            }

            GridPane mapGrid = new GridPane();
            mapGrid.setHgap(2);
            mapGrid.setVgap(2);
            mapGrid.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);");
            mapGrid.setPadding(new Insets(20)); // Padding generoso para a "caixa"

            // Fonte maior para que o conteúdo do mapa seja maior
            Font baseFont = Font.font("Courier New", FontWeight.NORMAL, 18);
            Font boldFont = Font.font("Courier New", FontWeight.BOLD, 18);

            for (int y = 0; y < layoutData.height; y++) {
                for (int x = 0; x < layoutData.width; x++) {
                    ViewScenarioLayoutController.CellData cellData = layoutData.grid[y][x];
                    Text cellText = new Text(cellData.symbol + "  ");

                    cellText.setFont(baseFont);
                    switch (cellData.type) {
                        case CITY: cellText.setFill(Color.RED); cellText.setFont(boldFont); break;
                        case INDUSTRY: cellText.setFill(Color.BLUE); cellText.setFont(boldFont); break;
                        case STATION: cellText.setFill(Color.GREEN); cellText.setFont(boldFont); break;
                        default: cellText.setFill(Color.GRAY); break;
                    }
                    mapGrid.add(cellText, x, y);
                }
            }

            // O mapGrid agora irá calcular o seu tamanho preferido com base no conteúdo (fonte, padding, gaps).
            // Este tamanho será o tamanho da "caixa branca".

            ScrollPane scrollPane = new ScrollPane(mapGrid);
            // Para que o mapa (mapGrid) seja mostrado no seu tamanho natural dentro do ScrollPane.
            // Se o mapGrid for maior que o viewport do ScrollPane, as barras de scroll aparecem.
            scrollPane.setFitToWidth(false);
            scrollPane.setFitToHeight(false);

            scrollPane.setStyle("-fx-background-color:transparent; -fx-background:transparent;");

            // Para que o ScrollPane (a "caixa" do mapa) não se expanda para além do seu conteúdo
            // E assim possa ser centrado corretamente pelo StackPane (contentArea).
            // Isto significa que a "caixa" terá o tamanho exato do mapGrid (incluindo padding).
            scrollPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);


            contentArea.getChildren().add(scrollPane);
            StackPane.setAlignment(scrollPane, Pos.CENTER); // Centra o ScrollPane (com o mapa)

            System.out.println("Visualização gráfica do mapa carregada para: " + currentMap.getNameID());

        } else {
            VBox noMapContainer = new VBox(15);
            noMapContainer.setAlignment(Pos.CENTER);
            Label noMapLabel = new Label("Nenhum mapa selecionado.");
            noMapLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");
            Label instructionLabel = new Label("Por favor, selecione um mapa e cenário através do menu 'File > Select Map & Scenario'.");
            instructionLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
            noMapContainer.getChildren().addAll(noMapLabel, instructionLabel);
            contentArea.getChildren().add(noMapContainer);
            StackPane.setAlignment(noMapContainer, Pos.CENTER);
            System.out.println("Nenhum mapa para visualizar.");
        }
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
}