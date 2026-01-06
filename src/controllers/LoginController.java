package controllers;

import dao.UserDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.User;
import application.Main;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;
    @FXML private Hyperlink linkRegister;

    private Main mainApp;
    private UserDAO userDAO;

    public LoginController() {
        userDAO = new UserDAO();
    }

    public void setMainApp(Main mainApp) { 
        this.mainApp = mainApp; 
    }

    @FXML
    private void handleLogin() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        // Validation dyal champs
        if (username == null || username.trim().isEmpty()) {
            afficherErreur("Erreur", "Le nom d'utilisateur est obligatoire");
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            afficherErreur("Erreur", "Le mot de passe est obligatoire");
            return;
        }

        // Tentative de connexion
        User user = userDAO.login(username, password);

        if (user != null) {
            // Connexion réussie
            System.out.println("✅ Connexion réussie pour: " + user.getUsername());
            
            if (mainApp != null) {
                mainApp.showDashboard(user);
            }
        } else {
            // Échec de connexion
            afficherErreur(
                "Échec de connexion", 
                "Nom d'utilisateur ou mot de passe incorrect.\nVeuillez réessayer."
            );
            
            // Nettoyer le champ password
            txtPassword.clear();
            txtPassword.requestFocus();
        }
    }

    /**
     * ★ NOUVELLE MÉTHODE: Ouvrir formulaire d'inscription
     */
    @FXML
    private void handleRegister() {
        try {
            System.out.println("📝 Ouverture formulaire d'inscription...");
            
            // Charger le formulaire d'inscription
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Register.fxml"));
            Parent root = loader.load();
            
            // Créer une nouvelle fenêtre modale
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Créer un compte - Boutique Chic");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            
            // Passer référence du LoginController au RegisterController
            RegisterController registerController = loader.getController();
            registerController.setLoginController(this);
            
            stage.showAndWait();
            
        } catch (Exception e) {
            System.err.println("❌ Erreur ouverture formulaire inscription: " + e.getMessage());
            e.printStackTrace();
            afficherErreur("Erreur", "Impossible d'ouvrir le formulaire d'inscription.");
        }
    }

    /**
     * Appelé après inscription réussie pour auto-login
     */
    public void autoLoginAfterRegister(String username) {
        txtUsername.setText(username);
        txtPassword.requestFocus();
        
        Alert info = new Alert(AlertType.INFORMATION);
        info.setTitle("Inscription Réussie");
        info.setHeaderText("Compte créé avec succès!");
        info.setContentText("Bienvenue " + username + "!\nVeuillez entrer votre mot de passe pour vous connecter.");
        info.showAndWait();
    }

    /**
     * Afficher un message d'erreur
     */
    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Permettre login avec touche Enter
     */
    @FXML
    public void initialize() {
        // Login avec Enter sur n'importe quel champ
        txtUsername.setOnAction(e -> handleLogin());
        txtPassword.setOnAction(e -> handleLogin());
        
        // DEBUG: Lister tous les users au démarrage
        System.out.println("\n🔧 MODE DEBUG ACTIVÉ");
        userDAO.listAllUsers();
    }

    // ===== Effets UI =====
    @FXML 
    private void onFieldHover(MouseEvent event) { 
        if (event.getSource() instanceof TextField) {
            TextField field = (TextField) event.getSource();
            field.setStyle(field.getStyle() + "; -fx-border-color: #9C6A4A;");
        } else if (event.getSource() instanceof PasswordField) {
            PasswordField field = (PasswordField) event.getSource();
            field.setStyle(field.getStyle() + "; -fx-border-color: #9C6A4A;");
        }
    }
    
    @FXML 
    private void onFieldExit(MouseEvent event) { 
        if (event.getSource() instanceof TextField) {
            TextField field = (TextField) event.getSource();
            field.setStyle(field.getStyle() + "; -fx-border-color: #e0e0e0;");
        } else if (event.getSource() instanceof PasswordField) {
            PasswordField field = (PasswordField) event.getSource();
            field.setStyle(field.getStyle() + "; -fx-border-color: #e0e0e0;");
        }
    }
    
    @FXML 
    private void onButtonHover(MouseEvent event) { 
        btnLogin.setStyle("-fx-background-color: #8B5E3C; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-weight: bold; -fx-font-size: 14; -fx-padding: 14; -fx-cursor: hand;"); 
    }
    
    @FXML 
    private void onButtonExit(MouseEvent event) { 
        btnLogin.setStyle("-fx-background-color: #9C6A4A; -fx-text-fill: white; -fx-background-radius: 10; -fx-font-weight: bold; -fx-font-size: 14; -fx-padding: 14; -fx-cursor: hand;"); 
    }
}