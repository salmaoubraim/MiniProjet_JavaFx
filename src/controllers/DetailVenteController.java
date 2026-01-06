package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import models.Vente;
import javafx.beans.property.SimpleStringProperty;
import java.time.format.DateTimeFormatter;
import java.util.List;
import dao.LigneVenteDAO;

public class DetailVenteController {
    
    @FXML
    private Label idVenteLabel;
    
    @FXML
    private Label dateVenteLabel;
    
    @FXML
    private Label totalVenteLabel;
    
    @FXML
    private TableView<ObservableList<String>> detailTableView;
    
    @FXML
    private TableColumn<ObservableList<String>, String> colProduit;
    
    @FXML
    private TableColumn<ObservableList<String>, String> colQuantite;
    
    @FXML
    private TableColumn<ObservableList<String>, String> colPrixUnitaire;
    
    @FXML
    private TableColumn<ObservableList<String>, String> colTotal;
    
    private LigneVenteDAO ligneVenteDAO;
    
    @FXML
    public void initialize() {
        ligneVenteDAO = new LigneVenteDAO();
        
        // Configuration des colonnes
        colProduit.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
        colQuantite.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
        colPrixUnitaire.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
        colTotal.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));
    }
    
    public void chargerDetails(Vente vente) {
        // Afficher le numéro de facture au lieu de l'ID
        if (vente.getNumeroFacture() != null && !vente.getNumeroFacture().isEmpty()) {
            idVenteLabel.setText("Facture: " + vente.getNumeroFacture());
        } else {
            idVenteLabel.setText("Vente #" + vente.getId_vente());
        }
        
        // Correction : utiliser LocalDateTime au lieu de LocalDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        dateVenteLabel.setText("Date: " + vente.getDate_vente().format(formatter));
        
        totalVenteLabel.setText(String.format("%.2f MAD", vente.getTotal()));
        
        // Charger les lignes de détail
        List<String[]> details = ligneVenteDAO.getLignesVenteAvecDetails(vente.getId_vente());
        
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        for (String[] ligne : details) {
            data.add(FXCollections.observableArrayList(ligne));
        }
        
        detailTableView.setItems(data);
    }
    
    @FXML
    void fermer(ActionEvent event) {
        Stage stage = (Stage) detailTableView.getScene().getWindow();
        stage.close();
    }
}