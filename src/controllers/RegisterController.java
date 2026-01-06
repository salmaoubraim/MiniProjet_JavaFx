package controllers;

import dao.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegisterController {

    @FXML private TextField txtUsername;
    @FXML private TextField txtEmail;
    @FXML private TextField txtFullName;
    @FXML private TextField txtPhone;
    @FXML private TextField txtRole;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private Button btnRegister;
    @FXML private Button btnCancel;
    private UserDAO userDAO;
    private LoginController loginController;

    public RegisterController() {
        userDAO = new UserDAO();
    }

    @FXML
    public void initialize() {
        System.out.println("✅ RegisterController initialisé");
    }

    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    @FXML
    private void handleRegister() {

        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String fullName = txtFullName.getText().trim();
        String phone = txtPhone.getText().trim();
        String role = txtRole.getText().trim().toUpperCase();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        // ===== VALIDATIONS =====

        if (username.isEmpty()) {
            showError("Erreur", "Username obligatoire !");
            txtUsername.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            showError("Erreur", "Email obligatoire !");
            txtEmail.requestFocus();
            return;
        }

        if (fullName.isEmpty()) {
            showError("Erreur", "Nom complet obligatoire !");
            txtFullName.requestFocus();
            return;
        }

        if (role.isEmpty()) {
            showError("Erreur", "Rôle obligatoire !");
            txtRole.requestFocus();
            return;
        }

        // Role autorisé
        if (!role.equals("CASHIER") && !role.equals("MANAGER") && !role.equals("ADMIN")) {
            showError("Erreur", "Rôle invalide !");
            txtRole.requestFocus();
            return;
        }

        if (password.length() < 6) {
            showError("Erreur", "Mot de passe min 6 caractères");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Erreur", "Les mots de passe ne correspondent pas");
            txtPassword.clear();
            txtConfirmPassword.clear();
            return;
        }

        try {
            if (userDAO.usernameExists(username)) {
                showError("Erreur", "Username déjà utilisé");
                return;
            }

            if (userDAO.emailExists(email)) {
                showError("Erreur", "Email déjà utilisé");
                return;
            }

            boolean success = userDAO.registerUser(
                    username, password, email, fullName, phone, role
            );

            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText("Inscription réussie");
                alert.setContentText("Vous pouvez maintenant vous connecter");
                alert.showAndWait();

                if (loginController != null) {
                    loginController.autoLoginAfterRegister(username);
                }

                closeWindow();
            } else {
                showError("Erreur", "Erreur lors de l'inscription");
            }

        } catch (Exception e) {
            showError("Erreur système", e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
