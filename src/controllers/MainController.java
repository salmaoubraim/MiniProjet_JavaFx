// MainController.java - Avec contrôle d'accès SANS modifier le style
package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.Scene;
import java.io.IOException;
import application.Main;
import models.User;

public class MainController {
    
    private static MainController instance;
    
    @FXML private ScrollPane conteneurCentral;
    @FXML private Label titrePageLabel;
    @FXML private Label utilisateurLabel;
    @FXML private Label initialeLabel;
    @FXML private Label roleLabel;
    
    @FXML private Button btnVentes;
    @FXML private Button btnProduits;
    @FXML private Button btnStatistiques;
    @FXML private Button btnUtilisateurs;
    
    private Button dernierBoutonActif;
    private Main mainApp;
    private String userRole;
    
    private static final String BUTTON_ACTIVE_STYLE = "-fx-background-color: #734e35;";
    private static final String BUTTON_INACTIVE_STYLE = "-fx-background-color: transparent;";
    
    @FXML
    public void initialize() {
        instance = this;
    }
    
    public static MainController getInstance() {
        return instance;
    }
    
    public Button getBtnStatistiques() { 
        return btnStatistiques; 
    }
    
    public Button getBtnVentes() { 
        return btnVentes; 
    }
    
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }
    
    public void setUtilisateur(User user) {
        if (user != null) {
            String nom = user.getFullName();
            String role = user.getRole();
            
            if (nom != null && !nom.isEmpty()) {
                String initiale = nom.substring(0, 1).toUpperCase();
                initialeLabel.setText(initiale);
                utilisateurLabel.setText(nom);
            }
            
            if (role != null && !role.isEmpty()) {
                roleLabel.setText(role);
                userRole = role.toUpperCase();
                
                // Masquer les boutons selon le rôle
                btnProduits.setVisible(userRole.equals("ADMIN") || userRole.equals("MANAGER"));
                btnStatistiques.setVisible(userRole.equals("ADMIN") || userRole.equals("MANAGER"));
                btnUtilisateurs.setVisible(userRole.equals("ADMIN"));
            }
        }
    }
    
    @FXML
    void naviguerVers(ActionEvent event) {
        Button boutonClique = (Button) event.getSource();
        
        if (boutonClique == btnVentes) {
            chargerPage("ventes.fxml", "💰 Gestion des Ventes", btnVentes);
        } else if (boutonClique == btnProduits) {
            chargerPage("produits.fxml", "📦 Gestion des Produits", btnProduits);
        } else if (boutonClique == btnStatistiques) {
            chargerPage("statistiques.fxml", "📊 Tableau de bord", btnStatistiques);
        } else if (boutonClique == btnUtilisateurs) {
            chargerPage("utilisateurs.fxml", "🔐 Gestion des Utilisateurs", btnUtilisateurs);
        }
    }
    
    public void chargerPage(String fxmlFile, String titre, Button boutonActif) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/" + fxmlFile));
            Parent page = loader.load();
            
            conteneurCentral.setContent(page);
            titrePageLabel.setText(titre);
            
            updateButtonStyles(boutonActif);
            
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible de charger la page");
            alert.setContentText("Fichier: " + fxmlFile);
            alert.showAndWait();
        }
    }
    
    private void updateButtonStyles(Button boutonActif) {
        if (dernierBoutonActif != null) {
            String baseStyle = getBaseButtonStyle(dernierBoutonActif);
            dernierBoutonActif.setStyle(baseStyle + BUTTON_INACTIVE_STYLE);
        }
        
        if (boutonActif != null) {
            String baseStyle = getBaseButtonStyle(boutonActif);
            boutonActif.setStyle(baseStyle + BUTTON_ACTIVE_STYLE);
            dernierBoutonActif = boutonActif;
        }
    }
    
    private String getBaseButtonStyle(Button button) {
        String currentStyle = button.getStyle();
        if (currentStyle == null) return "";
        return currentStyle.replaceAll("-fx-background-color:[^;]*;?", "");
    }
    
    @FXML
    void deconnexion(ActionEvent event) throws IOException {
        System.out.println("🔓 Déconnexion en cours...");
        
        // Nettoyer l'instance singleton
        instance = null;
        dernierBoutonActif = null;
        userRole = null;
        
        // Récupérer le stage
        Stage stage = (Stage) conteneurCentral.getScene().getWindow();
        
        // mainApp pour retourner au login
        if (mainApp != null) {
            mainApp.showLogin();
            System.out.println("✅ Déconnexion réussie");
        }
    }
}