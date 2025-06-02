package pt.ipp.isep.dei.ui.gui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pt.ipp.isep.dei.application.session.ApplicationSession;
// Substitua por a sua classe ApplicationSession ou equivalente
// import pt.ipp.isep.dei.controller.template.ApplicationSession;
// import pt.ipp.isep.dei.domain.template.Map; // Ou a sua classe de Map
// import pt.ipp.isep.dei.domain.template.Player; // Ou a sua classe de Player

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PlayerMenuGUIController implements Initializable {

    @FXML
    private BorderPane playerMainPane;

    @FXML
    private MenuItem selectMapScenarioMenuItem;
    @FXML
    private MenuItem saveGameMenuItem;
    @FXML
    private MenuItem loadGameMenuItem;
    @FXML
    private MenuItem logoutMenuItem;

    @FXML
    private MenuItem buildStationMenuItem;
    @FXML
    private MenuItem upgradeStationMenuItem;
    @FXML
    private MenuItem buildRailwayLineMenuItem;
    @FXML
    private MenuItem buyLocomotiveMenuItem;
    @FXML
    private MenuItem createRouteMenuItem;
    @FXML
    private MenuItem assignTrainMenuItem;

    @FXML
    private MenuItem viewCurrentMapMenuItem;
    @FXML
    private MenuItem listStationsMenuItem;
    @FXML
    private MenuItem listTrainsMenuItem;
    @FXML
    private MenuItem viewConnectivityMenuItem;
    @FXML
    private MenuItem viewMaintenanceRouteMenuItem;
    @FXML
    private MenuItem viewShortestRouteMenuItem;
    @FXML
    private MenuItem viewFinancialResultsMenuItem;


    @FXML
    private MenuItem runPauseSimulatorMenuItem;

    @FXML
    private MenuItem stationProfitAnalysisMenuItem;
    @FXML
    private MenuItem passengerArrivalsAnalysisMenuItem;
    @FXML
    private MenuItem cargoArrivalsAnalysisMenuItem;
    @FXML
    private MenuItem distributionAnalysisMenuItem;
    @FXML
    private MenuItem cargoRevenueAnalysisMenuItem;


    @FXML
    private MenuItem aboutMenuItem;

    @FXML
    private StackPane contentArea;

    @FXML
    private Label budgetLabel;
    @FXML
    private Label simulatorTimeLabel;

    // TODO: Descomentar e adaptar quando tiver a sua ApplicationSession
    private ApplicationSession appSession = ApplicationSession.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configurar estado inicial da UI
        updateBudgetDisplay();
        updateSimulatorTimeDisplay("Paused"); // Estado inicial do simulador
        updateMenuItemsState(); // Habilitar/desabilitar itens com base no estado (ex: mapa carregado)

    }

    private void updateBudgetDisplay() {
        // TODO: Obter o orçamento do jogador atual da ApplicationSession
        // Player currentPlayer = appSession.getCurrentPlayer();
        // if (currentPlayer != null) {
        //    budgetLabel.setText(String.format("$%.2f", currentPlayer.getCurrentBudget()));
        // } else {
        //    budgetLabel.setText("N/A");
        // }
        budgetLabel.setText("$1,000,000.00"); // Placeholder
    }

    private void updateSimulatorTimeDisplay(String time) {
        simulatorTimeLabel.setText(time);
    }

    private void updateMenuItemsState() {
        // TODO: Verificar se um mapa está carregado na ApplicationSession
        // boolean mapLoaded = appSession.getCurrentMap() != null;
        boolean mapLoaded = false; // Placeholder - assuma que nenhum mapa está carregado inicialmente

        // Itens que dependem de um mapa/cenário carregado
        saveGameMenuItem.setDisable(!mapLoaded);
        buildStationMenuItem.setDisable(!mapLoaded);
        upgradeStationMenuItem.setDisable(!mapLoaded);
        buildRailwayLineMenuItem.setDisable(!mapLoaded);
        buyLocomotiveMenuItem.setDisable(!mapLoaded);
        createRouteMenuItem.setDisable(!mapLoaded);
        assignTrainMenuItem.setDisable(!mapLoaded);
        viewCurrentMapMenuItem.setDisable(!mapLoaded);
        listStationsMenuItem.setDisable(!mapLoaded);
        listTrainsMenuItem.setDisable(!mapLoaded);
        viewConnectivityMenuItem.setDisable(!mapLoaded);
        viewMaintenanceRouteMenuItem.setDisable(!mapLoaded);
        viewShortestRouteMenuItem.setDisable(!mapLoaded);
        viewFinancialResultsMenuItem.setDisable(!mapLoaded);
        runPauseSimulatorMenuItem.setDisable(!mapLoaded);

        // As análises estatísticas podem ou não depender de um jogo ativo/mapa, ajuste conforme necessário
        // Se usarem dados de um ficheiro CSV fixo como em US15-18, podem estar sempre habilitadas
        // Se usarem dados do jogo atual, então:
        // stationProfitAnalysisMenuItem.setDisable(!mapLoaded);
        // passengerArrivalsAnalysisMenuItem.setDisable(!mapLoaded);
        // cargoArrivalsAnalysisMenuItem.setDisable(!mapLoaded);
        // distributionAnalysisMenuItem.setDisable(!mapLoaded);
        // cargoRevenueAnalysisMenuItem.setDisable(!mapLoaded);

    }


    // --- Manipuladores de Ação do Menu "File" ---
    @FXML
    void handleSelectMapScenario(ActionEvent event) {
        System.out.println("Select Map and Scenario clicked");
        // TODO: Abrir um diálogo para o utilizador selecionar um mapa e cenário.
        // Após a seleção, atualizar appSession e chamar updateMenuItemsState() e carregar visualização do mapa.
        // Exemplo: loadView("/fxml/SelectMapScenarioDialog.fxml", "Select Map & Scenario");
        // Se um mapa for carregado com sucesso:
        // appSession.setCurrentMap(selectedMap); // Atualizar sessão
        // updateMenuItemsState();
        // loadMapVisualization(); // Método para mostrar o mapa em contentArea
        showAlert("Not Implemented", "Select Map and Scenario functionality is not yet implemented.");
    }

    @FXML
    void handleSaveGame(ActionEvent event) {
        System.out.println("Save Game clicked"); //
        // TODO: Implementar lógica para guardar o estado atual do jogo (US23)
        showAlert("Not Implemented", "Save Game (US23) functionality is not yet implemented.");
    }

    @FXML
    void handleLoadGame(ActionEvent event) {
        System.out.println("Load Game clicked"); //
        // TODO: Implementar lógica para carregar um jogo guardado (US24)
        // Após carregar, atualizar appSession, updateMenuItemsState() e a UI
        showAlert("Not Implemented", "Load Game (US24) functionality is not yet implemented.");
    }

    @FXML
    void handleLogout(ActionEvent event) {
        System.out.println("Logout clicked");
        // TODO: Chamar o AuthenticationController para fazer logout
        // authController.doLogout();
        // Fechar esta janela e voltar ao ecrã de login
        // Exemplo: ((Stage) playerMainPane.getScene().getWindow()).close();
        // new LoginScreen().start(new Stage()); // Ou similar para mostrar o login
        showAlert("Not Implemented", "Logout functionality is not yet implemented.");
        try {
            Stage currentStage = (Stage) playerMainPane.getScene().getWindow();
            // Assumindo que o FXML do login é LoginMenuGUI.fxml e está no mesmo path relativo
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/pt/ipp/isep/dei/ui/gui/fxml/LoginMenuGUI.fxml"));
            Scene loginScene = new Scene(loginRoot);
            currentStage.setScene(loginScene);
            currentStage.setTitle("Login - Railway Management System");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the login screen.");
        }
    }

    // --- Manipuladores de Ação do Menu "Manage" ---
    @FXML
    void handleBuildStation(ActionEvent event) {
        System.out.println("Build Station clicked"); // Referência a US05
        // TODO: Abrir UI para construir estação (US05)
        showAlert("Not Implemented", "Build Station (US05) functionality is not yet implemented.");
    }

    @FXML
    void handleUpgradeStation(ActionEvent event) {
        System.out.println("Upgrade Station clicked"); // Referência a US06
        // TODO: Abrir UI para fazer upgrade de estação (US06)
        showAlert("Not Implemented", "Upgrade Station (US06) functionality is not yet implemented.");
    }


    @FXML
    void handleBuildRailwayLine(ActionEvent event) {
        System.out.println("Build Railway Line clicked"); // Referência a US08
        // TODO: Abrir UI para construir linha férrea (US08)
        showAlert("Not Implemented", "Build Railway Line (US08) functionality is not yet implemented.");
    }

    @FXML
    void handleBuyLocomotive(ActionEvent event) {
        System.out.println("Buy Locomotive clicked"); // Referência a US09
        // TODO: Abrir UI para comprar locomotiva (US09)
        showAlert("Not Implemented", "Buy Locomotive (US09) functionality is not yet implemented.");
    }

    @FXML
    void handleCreateRoute(ActionEvent event) {
        System.out.println("Create Route clicked"); // Referência a US10
        // TODO: Abrir UI para criar rota (US10)
        showAlert("Not Implemented", "Create Route (US10) functionality is not yet implemented.");
    }

    @FXML
    void handleAssignTrainToRoute(ActionEvent event) {
        System.out.println("Assign Train to Route clicked"); // Referência a US10
        // TODO: Abrir UI para atribuir comboio a rota (parte de US10)
        showAlert("Not Implemented", "Assign Train to Route functionality is not yet implemented.");
    }

    // --- Manipuladores de Ação do Menu "View" ---
    @FXML
    void handleViewCurrentMap(ActionEvent event) {
        System.out.println("View Current Map clicked");
        // TODO: Carregar/atualizar a visualização do mapa no contentArea
        Label mapLabel = new Label("Map visualization will be shown here.");
        contentArea.getChildren().setAll(mapLabel); // Substitui conteúdo anterior
        showAlert("Not Implemented", "View Current Map functionality is not yet implemented.");
    }

    @FXML
    void handleListStations(ActionEvent event) {
        System.out.println("View Stations Details clicked"); // Referência a US07
        // TODO: Mostrar detalhes das estações (US07), possivelmente numa nova janela/vista
        showAlert("Not Implemented", "View Stations Details (US07) functionality is not yet implemented.");
    }

    @FXML
    void handleListTrains(ActionEvent event) {
        System.out.println("View Trains clicked"); // Referência a US11
        // TODO: Mostrar lista de comboios (US11)
        showAlert("Not Implemented", "View Trains (US11) functionality is not yet implemented.");
    }

    @FXML
    void handleViewConnectivity(ActionEvent event) {
        System.out.println("Check Network Connectivity clicked"); // Referência a US13
        // TODO: Implementar UI para US13
        showAlert("Not Implemented", "Check Network Connectivity (US13) functionality is not yet implemented.");
    }

    @FXML
    void handleViewMaintenanceRoute(ActionEvent event) {
        System.out.println("View Maintenance Route clicked"); // Referência a US14
        // TODO: Implementar UI para US14
        showAlert("Not Implemented", "View Maintenance Route (US14) functionality is not yet implemented.");
    }

    @FXML
    void handleViewShortestRoute(ActionEvent event) {
        System.out.println("Find Shortest Route clicked"); // Referência a US27
        // TODO: Implementar UI para US27
        showAlert("Not Implemented", "Find Shortest Route (US27) functionality is not yet implemented.");
    }

    @FXML
    void handleViewFinancialResults(ActionEvent event) {
        System.out.println("View Year Financial Results clicked"); // Referência a US25
        // TODO: Implementar UI para US25
        showAlert("Not Implemented", "View Year Financial Results (US25) functionality is not yet implemented.");
    }


    // --- Manipuladores de Ação do Menu "Simulator" ---
    @FXML
    void handleRunPauseSimulator(ActionEvent event) {
        System.out.println("Play/Pause Simulator clicked"); // Referência a US12
        // TODO: Lógica para iniciar/pausar o simulador (US12)
        // Atualizar o texto do simulatorTimeLabel e possivelmente o texto do menu item
        showAlert("Not Implemented", "Play/Pause Simulator (US12) functionality is not yet implemented.");
    }

    // --- Manipuladores de Ação do Menu "Statistics" (Python/Jupyter Notebooks) ---
    // Estas US (15-18, 31) são para serem desenvolvidas em Python/Jupyter.
    // A integração pode ser abrir o ficheiro Jupyter Notebook ou exibir resultados pré-gerados.
    @FXML
    void handleStationProfitAnalysis(ActionEvent event) { // US15
        System.out.println("Station Profit Analysis clicked");
        showAlert("Python/Jupyter Task", "US15: Perform statistical analysis of average annual profit (Python).");
    }

    @FXML
    void handlePassengerArrivalsAnalysis(ActionEvent event) { // US16
        System.out.println("Passenger Arrivals Analysis clicked");
        showAlert("Python/Jupyter Task", "US16: Perform comparative analysis of passenger arrivals (Python).");
    }

    @FXML
    void handleCargoArrivalsAnalysis(ActionEvent event) { // US17
        System.out.println("Cargo Arrivals Analysis clicked");
        showAlert("Python/Jupyter Task", "US17: Perform analysis of cargo arriving at each station (Python).");
    }

    @FXML
    void handleDistributionAnalysis(ActionEvent event) { // US18
        System.out.println("Train/Passenger/Mail Distribution clicked");
        showAlert("Python/Jupyter Task", "US18: Perform analysis of train, passenger, and mail distribution (Python).");
    }
    @FXML
    void handleCargoRevenueAnalysis(ActionEvent event) { // US31
        System.out.println("Cargo Revenue Correlation clicked");
        showAlert("Python/Jupyter Task", "US31: Perform statistical analysis for cargo revenue correlation (Python).");
    }


    // --- Manipuladores de Ação do Menu "Help" ---
    @FXML
    void handleAbout(ActionEvent event) {
        System.out.println("About clicked");
        // TODO: Mostrar uma janela "Sobre" com informações da aplicação
        showAlert("About", "Railway Management System\nVersion: 1.0 (PI Sem2 2024-25)\nDeveloped by: Group 112");
    }


    // --- Métodos Utilitários ---

    /**
     * Carrega uma nova vista (FXML) na área de conteúdo principal.
     * @param fxmlPath Caminho para o ficheiro FXML.
     */
    private void loadViewToContentArea(String fxmlPath) {
        try {
            Parent viewRoot = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().setAll(viewRoot);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error Loading View", "Could not load view: " + fxmlPath);
        }
    }

    /**
     * Abre uma nova janela (Stage) como um diálogo modal.
     * @param fxmlPath Caminho para o ficheiro FXML do diálogo.
     * @param title Título da janela do diálogo.
     */
    private void openModalDialog(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent dialogRoot = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            // dialogStage.initOwner(((Stage) playerMainPane.getScene().getWindow())); // Define o proprietário se necessário
            Scene scene = new Scene(dialogRoot);
            dialogStage.setScene(scene);

            // Aqui você pode passar dados para o controlador do diálogo se necessário
            // Exemplo: YourDialogController controller = loader.getController();
            // controller.setData(...);

            dialogStage.showAndWait(); // Mostra e espera que seja fechado

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error Opening Dialog", "Could not open dialog: " + fxmlPath);
        }
    }


    private void showAlert(String title, String message) {
        // TODO: Usar um Alert do JavaFX em vez de System.out para melhor UI
        // Alert alert = new Alert(Alert.AlertType.INFORMATION);
        // alert.setTitle(title);
        // alert.setHeaderText(null);
        // alert.setContentText(message);
        // alert.showAndWait();
        System.out.println("[" + title + "] " + message); // Placeholder
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}