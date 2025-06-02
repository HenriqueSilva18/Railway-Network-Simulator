package pt.ipp.isep.dei.ui.gui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pt.ipp.isep.dei.application.controller.authorization.AuthenticationController;
import pt.ipp.isep.dei.ui.console.menu.AdminUI;
import pt.ipp.isep.dei.ui.console.menu.EditorUI;
import pt.ipp.isep.dei.ui.console.menu.MenuItem;
import pt.ipp.isep.dei.ui.console.menu.PlayerUI;
import pt.ipp.isep.dei.ui.console.utils.Utils;
import pt.isep.lei.esoft.auth.mappers.dto.UserRoleDTO;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * The type Login menu gui controller.
 */
public class LoginMenuGUIController {

    /**
     * The Scene.
     */
    @FXML
    public Scene scene;

    /**
     * The Root.
     */
    @FXML
    public Parent root;

    @FXML
    private Stage stage;

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button exitButton;

    @FXML
    private TextField passwordTextField;
    @FXML
    private Button showPasswordButton;

    private boolean isPasswordVisible = false;

    private final AuthenticationController controller = new AuthenticationController();

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w.-]+$");

    private boolean doLogin(ActionEvent event) {
        String id = emailField.getText();
        String pwd = getCurrentPassword();

        if (!isValidEmail(id)) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Invalid Email format.");
            return false;
        }

        boolean success = controller.doLogin(id, pwd);

        System.out.println("Login attempt for user: " + id + " - Success: " + success);

        if (!success) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Invalid credentials.");
            passwordField.clear();
            if (passwordTextField != null && isPasswordVisible) {
                passwordTextField.clear();
            }
            return false;
        }

        List<UserRoleDTO> roles = controller.getUserRoles();
        if ((roles == null) || (roles.isEmpty())) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "No role assigned to user.");
            return false;
        }

        UserRoleDTO role = selectsRole(roles);
        if (role == null) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "No role selected.");
            return false;
        }

        List<RoleFXML> roleFXMLList = getFXMLForRoles();
        return redirectToRoleUI(roleFXMLList, role, event);
    }


    private UserRoleDTO selectsRole(List<UserRoleDTO> roles) {
        if (roles.size() == 1) {
            return roles.get(0);
        } else {
            return (UserRoleDTO) Utils.showAndSelectOne(roles, "Select the role you want to adopt in this session:");
        }
    }

    private List<RoleFXML> getFXMLForRoles() {
        List<RoleFXML> list = new ArrayList<>();
        list.add(new RoleFXML(AuthenticationController.ROLE_PLAYER, "playermenu", "Player Menu"));
        list.add(new RoleFXML(AuthenticationController.ROLE_EDITOR, "editormenu", "Editor Menu"));
        list.add(new RoleFXML(AuthenticationController.ROLE_ADMIN, "adminmenu", "Admin Menu"));
        return list;
    }

    private boolean redirectToRoleUI(List<RoleFXML> fxmlList, UserRoleDTO role, ActionEvent event) {
        for (RoleFXML entry : fxmlList) {

            if (entry.role.equals(AuthenticationController.ROLE_EDITOR) || entry.role.equals(AuthenticationController.ROLE_ADMIN)) {
                showAlert(Alert.AlertType.INFORMATION, "Login Attempt", entry.title + " is not implemented yet.");
            }

            if (entry.role.equals(role.getDescription())) {
                try {
                    controller.doLogout();
                    switchMenusGUI(event, entry.fxmlFile, entry.title);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load " + entry.fxmlFile);
                    return false;
                }
            }
        }
        return false;
    }

    @FXML
    private void handleLoginButton(ActionEvent event) {
        boolean success = doLogin(event);
        if (!success) {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Login process failed.");
        }
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
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Criar nova Scene com as dimensões corretas
        Scene newScene;

        if (fileName.equals("playermenu")) {
            // Player menu - criar scene com tamanho base
            newScene = new Scene(newRoot, 1920, 1080);

            // Definir tamanho mínimo
            currentStage.setMinWidth(1000);
            currentStage.setMinHeight(700);


        } else if (fileName.equals("VFMMenu") || fileName.equals("MyAgenda")) {
            newScene = new Scene(newRoot, 900, 600);
            currentStage.setMinWidth(800);
            currentStage.setMinHeight(500);
            currentStage.setMaximized(false); // Não maximizar estas janelas

        } else {
            // Janelas menores (login, etc.)
            newScene = new Scene(newRoot, 400, 400);
            currentStage.setMinWidth(400);
            currentStage.setMinHeight(400);
            currentStage.setMaximized(false); // Não maximizar login
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

    /**
     * Close window.
     *
     * @param event the event
     */
    public void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private boolean isValidEmail(String email) {
        return email != null && !email.trim().isEmpty() && EMAIL_PATTERN.matcher(email).matches();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); // Removido header text para melhor aparência
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Esconder password
            passwordField.setText(passwordTextField.getText());
            passwordField.setVisible(true);
            passwordTextField.setVisible(false);
            showPasswordButton.setText("👁");
            isPasswordVisible = false;

            // Manter o foco no campo de password
            passwordField.requestFocus();
            passwordField.positionCaret(passwordField.getText().length());
        } else {
            // Mostrar password
            passwordTextField.setText(passwordField.getText());
            passwordField.setVisible(false);
            passwordTextField.setVisible(true);
            showPasswordButton.setText("🙈");
            isPasswordVisible = true;

            // Manter o foco no campo de texto
            passwordTextField.requestFocus();
            passwordTextField.positionCaret(passwordTextField.getText().length());
        }
    }

    @FXML
    private void onPasswordButtonHover() {
        showPasswordButton.setStyle(showPasswordButton.getStyle() +
                "-fx-background-color: rgba(0,0,0,0.1);");
    }

    @FXML
    private void onPasswordButtonExit() {
        String currentStyle = showPasswordButton.getStyle();
        if (currentStyle.contains("-fx-background-color: rgba(0,0,0,0.1);")) {
            showPasswordButton.setStyle(currentStyle.replace(
                    "-fx-background-color: rgba(0,0,0,0.1);", ""));
        }
    }

    // Método para obter a password atual
    private String getCurrentPassword() {
        return isPasswordVisible ? passwordTextField.getText() : passwordField.getText();
    }

    private static class RoleFXML {
        String role;
        String fxmlFile;
        String title;

        RoleFXML(String role, String fxmlFile, String title) {
            this.role = role;
            this.fxmlFile = fxmlFile;
            this.title = title;
        }
    }

}