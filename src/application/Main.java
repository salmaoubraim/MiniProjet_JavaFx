// Main.java - Version simplifiée
package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import models.User;
import controllers.LoginController;
import controllers.MainController;

public class Main extends Application {
    
    private Stage primaryStage;
    
    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        showLogin();
    }
    
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
    
    public void showLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();
            
            LoginController controller = loader.getController();
            controller.setMainApp(this);
            
            Scene scene = new Scene(root, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Boutique Chic - Connexion");


            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void showDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main.fxml"));
            Parent root = loader.load();
            
            MainController controller = loader.getController();
            controller.setMainApp(this);
            controller.setUtilisateur(user);
            
            // Page par défaut selon rôle
            if (user.getRole().equalsIgnoreCase("CASHIER")) {
                controller.chargerPage("ventes.fxml", "💰 Gestion des Ventes", controller.getBtnVentes());
            } else {
                controller.chargerPage("statistiques.fxml", "📊 Tableau de bord", controller.getBtnStatistiques());
            }
            
            Scene scene = new Scene(root, 1200, 600);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Boutique Chic - " + user.getRole());
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}