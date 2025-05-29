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


import java.io.IOException;
import java.util.List;
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

    private final AuthenticationController controller = new AuthenticationController();

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w.-]+$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Z].*[A-Z].*[A-Z])(?=.*\\d.*\\d)[A-Za-z\\d!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]{7,}$");

    private boolean doLogin() {
        String id = emailField.getText();
        String pwd = passwordField.getText();

        if (!isValidEmail(id)) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Invalid Email format.");
            return false;
        }

        if (!isValidPassword(pwd)) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "The password must have at least seven alphanumeric characters, including three capital letters and two digits.");
            return false;
        }
        boolean success = controller.doLogin(id, pwd);
        if (!success) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Invalid credentials.");
        }
        return success;
    }

    @FXML
    private void handleLoginButton(ActionEvent event) {
        if (doLogin()) {
            try {
                switchMenusGUI(event, "mainMenu", "Main");
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not open main menu.");
            }
        }
    }

    /**
     * Switch menus gui.
     *
     * @param event    the event
     * @param fileName the file name
     * @param menuName the menu name
     * @throws IOException the io exception
     */
    public void switchMenusGUI(ActionEvent event, String fileName, String menuName) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/fxml/" + fileName + ".fxml"));
        // Get the current stage
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (fileName.equals("VFMMenu") || fileName.equals("MyAgenda")){
            stage.setScene(new Scene(root, 900, 600));
        }else{
            stage.setScene(new Scene(root, 400, 400));
        }

        stage.setTitle(menuName + " Menu");
        // Set the new scene or update the current scene with the new root
        stage.getScene().setRoot(root);
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
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}
