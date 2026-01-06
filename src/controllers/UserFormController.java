package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.User;
import config.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class UserFormController {

    @FXML private Label lblTitle;
    @FXML private TextField txtUsername;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private VBox passwordBox;
    @FXML private ComboBox<String> cbRole;
    @FXML private Button btnSave;

    private User currentUser;
    public Runnable refreshTableAfterSave;

    @FXML
    public void initialize() {
        // Remplir le ComboBox avec les rôles
        cbRole.setItems(FXCollections.observableArrayList("admin", "manager", "cashier"));
        cbRole.setValue("cashier");
    }

    public void setUser(User user) {
        this.currentUser = user;
        if (user != null) {
            // Mode modification
            lblTitle.setText("Modifier l'utilisateur");
            txtUsername.setText(user.getUsername());
            txtEmail.setText(user.getEmail());
            cbRole.setValue(user.getRole());
            
            // Cacher le champ mot de passe en mode modification
            passwordBox.setVisible(false);
            passwordBox.setManaged(false);
        } else {
            // Mode ajout
            lblTitle.setText("Ajouter un Utilisateur");
            passwordBox.setVisible(true);
            passwordBox.setManaged(true);
        }
    }

    @FXML
    private void handleSave() {
        // Validation
        if (txtUsername.getText().isEmpty() || txtEmail.getText().isEmpty() || cbRole.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Champs manquants");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs obligatoires.");
            alert.showAndWait();
            return;
        }

        if (currentUser == null && txtPassword.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Mot de passe manquant");
            alert.setHeaderText(null);
            alert.setContentText("Le mot de passe est obligatoire pour un nouvel utilisateur.");
            alert.showAndWait();
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (currentUser == null) {
                // Insertion
                String sql = "INSERT INTO users(username, email, role, password) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, txtUsername.getText());
                stmt.setString(2, txtEmail.getText());
                stmt.setString(3, cbRole.getValue());
                stmt.setString(4, txtPassword.getText());
                stmt.executeUpdate();

                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Succès");
                info.setHeaderText(null);
                info.setContentText("Utilisateur ajouté avec succès!");
                info.showAndWait();
            } else {
                // Mise à jour
                String sql = "UPDATE users SET username=?, email=?, role=? WHERE username=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, txtUsername.getText());
                stmt.setString(2, txtEmail.getText());
                stmt.setString(3, cbRole.getValue());
                stmt.setString(4, currentUser.getUsername());
                stmt.executeUpdate();

                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Succès");
                info.setHeaderText(null);
                info.setContentText("Utilisateur modifié avec succès!");
                info.showAndWait();
            }

            if (refreshTableAfterSave != null) {
                refreshTableAfterSave.run();
            }
            closeWindow();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de l'enregistrement");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }
}