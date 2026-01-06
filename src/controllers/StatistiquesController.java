package controllers;

import dao.*;
import models.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.util.List;
import java.util.Map;

// ==================== CONTRÔLEUR DES STATISTIQUES ====================
public class StatistiquesController {
    
    // -------------------- Composants FXML --------------------
    @FXML private ComboBox<String> periodeComboBox;
    
    // Cartes statistiques
    @FXML private Label chiffreAffairesLabel;
    @FXML private Label nombreVentesLabel;
    @FXML private Label ticketMoyenLabel;
    @FXML private Label articlesVendusLabel;
    
    // Graphique
    @FXML private LineChart<String, Number> ventesChart;
    
    // Top 5 produits
    @FXML private Label produit1Nom, produit1Quantite, produit1CA;
    @FXML private Label produit2Nom, produit2Quantite, produit2CA;
    @FXML private Label produit3Nom, produit3Quantite, produit3CA;
    @FXML private Label produit4Nom, produit4Quantite, produit4CA;
    @FXML private Label produit5Nom, produit5Quantite, produit5CA;
    
    // Stock faible
    @FXML private VBox stockFaibleVBox;
    @FXML private Label stockFaibleLabel;
    
    // -------------------- DAOs --------------------
    private VenteDAO venteDAO;
    private ProduitDAO produitDAO;
    private StatistiquesDAO statistiquesDAO;
    
    // ==================== INITIALISATION ====================
    @FXML
    public void initialize() {
        venteDAO = new VenteDAO();
        produitDAO = new ProduitDAO();
        statistiquesDAO = new StatistiquesDAO();
        
        // Initialiser le ComboBox des périodes
        periodeComboBox.setItems(FXCollections.observableArrayList(
            "Aujourd'hui",
            "Hier",
            "Cette semaine",
            "Ce mois",
            "Cette année",
            "Tous"
        ));
        periodeComboBox.setValue("Aujourd'hui");
        
        actualiserStatistiques(null);
    }
    
    // ==================== ACTUALISATION DES STATISTIQUES ====================
    @FXML
    void actualiserStatistiques(ActionEvent event) {
        String periode = periodeComboBox.getValue();
        
        // Récupérer les statistiques
        Map<String, Object> stats = statistiquesDAO.getStatistiquesPeriode(periode);
        
        // Calculer les valeurs
        double ca = (double) stats.getOrDefault("chiffre_affaires", 0.0);
        int nbVentes = (int) stats.getOrDefault("nombre_ventes", 0);
        int totalArticles = (int) stats.getOrDefault("articles_vendus", 0);
        double ticketMoyen = nbVentes > 0 ? ca / nbVentes : 0.0;
        
        // Mettre à jour l'interface
        chiffreAffairesLabel.setText(String.format("%.2f MAD", ca));
        nombreVentesLabel.setText(String.valueOf(nbVentes));
        ticketMoyenLabel.setText(String.format("%.2f MAD", ticketMoyen));
        articlesVendusLabel.setText(String.valueOf(totalArticles));
        
        chargerGraphiqueEvolution(periode);
        chargerTop5Produits(periode);
        chargerStockFaible();
    }
    
    // ==================== GRAPHIQUE D'ÉVOLUTION ====================
    private void chargerGraphiqueEvolution(String periode) {
        ventesChart.getData().clear();
        List<Map<String, Object>> evolution = statistiquesDAO.getEvolutionVentes(periode);
        
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Chiffre d'Affaires");
        
        for (Map<String, Object> point : evolution) {
            String p = (String) point.get("periode");
            double ca = (double) point.get("chiffre_affaires");
            serie.getData().add(new XYChart.Data<>(p, ca));
        }
        
        ventesChart.getData().add(serie);
    }
    
    // ==================== TOP 5 PRODUITS ====================
    private void chargerTop5Produits(String periode) {
        List<Map<String, Object>> top5 = statistiquesDAO.getTop5Produits(periode);
        
        // Tableaux des labels
        Label[] nomsLabels = {produit1Nom, produit2Nom, produit3Nom, produit4Nom, produit5Nom};
        Label[] quantitesLabels = {produit1Quantite, produit2Quantite, produit3Quantite, produit4Quantite, produit5Quantite};
        Label[] caLabels = {produit1CA, produit2CA, produit3CA, produit4CA, produit5CA};
        
        // Remplir les labels
        for (int i = 0; i < 5; i++) {
            if (i < top5.size()) {
                Map<String, Object> produit = top5.get(i);
                nomsLabels[i].setText((String) produit.get("name"));
                quantitesLabels[i].setText(String.valueOf(produit.get("quantite")));
                caLabels[i].setText(String.format("%.2f", produit.get("ca")));
            } else {
                nomsLabels[i].setText("-");
                quantitesLabels[i].setText("0");
                caLabels[i].setText("0.00");
            }
        }
    }
    
    // ==================== PRODUITS EN STOCK FAIBLE ====================
    private void chargerStockFaible() {
        List<Produit> produitsStockFaible = produitDAO.getProduitsStockFaible(10);
        
        stockFaibleVBox.getChildren().clear();
        
        if (produitsStockFaible.isEmpty()) {
            Label label = new Label("✓ Tous les produits ont un stock suffisant");
            label.setStyle("-fx-text-fill: green; -fx-font-style: italic;");
            stockFaibleVBox.getChildren().add(label);
        } else {
            for (Produit p : produitsStockFaible) {
                Label label = new Label(String.format("• %s - Stock: %d unités", 
                    p.getNom(), p.getStock()));
                label.setStyle("-fx-text-fill: #c0392b; -fx-font-size: 13px;");
                stockFaibleVBox.getChildren().add(label);
            }
        }
    }
}