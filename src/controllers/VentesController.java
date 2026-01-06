package controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import dao.LigneVenteDAO;
import dao.ProduitDAO;
import dao.VenteDAO;

import java.io.IOException;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.LigneVente;
import models.Produit;
import models.Vente;
import javafx.stage.Modality;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;

public class VentesController {

	@FXML
	private TableColumn<ObservableList<String>, String> articlePrixColumn;
	@FXML
	private TableColumn<ObservableList<String>, String> articleProduitColumn;
	@FXML
	private TableColumn<ObservableList<String>, String> articleQuantiteColumn;
	@FXML
	private TableColumn<ObservableList<String>, String> articleTotalColumn;
	@FXML
	private TableView<ObservableList<String>> articlesTableView;
	@FXML
	private ComboBox<String> produitComboBox;
	@FXML
	private Spinner<Integer> quantiteSpinner;
	@FXML
	private Label totalFactureLabel;
	@FXML
	private TableColumn<Vente, LocalDate> venteDateColumn;
	@FXML
	private TableColumn<Vente, Integer> venteIdColumn;
	@FXML
	private TableColumn<Vente, Double> venteTotalColumn;
	@FXML
	private TableView<Vente> ventesTableView;

	private VenteDAO venteDAO;
	private ProduitDAO produitDAO;
	private LigneVenteDAO ligneVenteDAO;
	
	// Mot de passe administrateur
	private static final String ADMIN_PASSWORD = "admin12345";

	// =======================( initialize )=======================
	@FXML
	public void initialize() {
		venteDAO = new VenteDAO();
		produitDAO = new ProduitDAO();
		ligneVenteDAO = new LigneVenteDAO();
		totalFactureLabel.setText("0.00");

		// Configuration des colonnes de TableView des ventes
		venteIdColumn.setCellValueFactory(new PropertyValueFactory<>("numeroFacture"));
		venteDateColumn.setCellValueFactory(new PropertyValueFactory<>("date_vente"));
		venteTotalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));

		// Configuration des colonnes de TableView de facture
		articleProduitColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
		articlePrixColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
		articleQuantiteColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
		articleTotalColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));

		SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
		quantiteSpinner.setValueFactory(valueFactory);

		// Chargement initial des données
		chargerVentes();
		chargerProduits();
	}

	// =====================( Button Ajouter )======================
	@FXML
	void ajouterArticle(ActionEvent event) {
		int quantite = quantiteSpinner.getValue();
		double prix = 0;
		double total;
		Produit produitTrouve = null;

		String produitSelection = produitComboBox.getValue();
		if (produitSelection == null)
			return; // pas de produit sélectionné

		for (Produit p : produitDAO.getAllProduits()) {
			if (p.getNom().equals(produitSelection)) {
				prix = p.getPrix();
				produitTrouve = p;
				break;
			}
		}
		if (produitTrouve == null)
			return; // produit non trouvé

		// Vérifier si le produit existe déjà dans le tableau
		ObservableList<String> ligneExistante = null;
		int quantiteActuelle = 0;

		for (ObservableList<String> ligne : articlesTableView.getItems()) {
			if (ligne.get(0).equals(produitSelection)) {
				ligneExistante = ligne;
				quantiteActuelle = Integer.parseInt(ligne.get(2));
				break;
			}
		}

		// Calculer la quantité totale (existante + nouvelle)
		int quantiteTotale = quantiteActuelle + quantite;

		// Vérification du stock
		if (produitTrouve.getStock() < quantiteTotale) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Stock insuffisant");
			alert.setHeaderText(null);
			alert.setContentText("Stock disponible: " + produitTrouve.getStock() + " unités seulement.");
			alert.showAndWait();
			return;
		}

		// Si le produit existe déjà, mettre à jour la quantité
		if (ligneExistante != null) {
			ligneExistante.set(2, String.valueOf(quantiteTotale));
			double nouveauTotal = prix * quantiteTotale;
			ligneExistante.set(3, String.valueOf(nouveauTotal));
			articlesTableView.refresh();
		} else {
			// Ajouter un nouveau produit
			total = prix * quantite;
			ObservableList<String> ligne = FXCollections.observableArrayList(produitSelection, String.valueOf(prix),
					String.valueOf(quantite), String.valueOf(total));
			articlesTableView.getItems().add(ligne);
		}

		// Réinitialiser les champs
		produitComboBox.setValue("Sélectionner...");
		quantiteSpinner.getValueFactory().setValue(1);

		// Mettre a jour le total
		totalFactureLabel.setText(String.valueOf(calculerTotalGeneral()));
	}

	// =====================( Button Annuler )======================
	@FXML
	void annulerVente(ActionEvent event) {
		if (articlesTableView.getItems().isEmpty()) {
			return; // Rien à annuler
		}

		// Demander confirmation
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Confirmation");
		alert.setHeaderText("Annuler la facture");
		alert.setContentText("Voulez-vous vraiment annuler cette facture ?");

		alert.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				// Vider tous les articles
				articlesTableView.getItems().clear();

				// Réinitialiser les champs
				produitComboBox.setValue("Sélectionner...");
				quantiteSpinner.getValueFactory().setValue(1);
				totalFactureLabel.setText("0.00");
			}
		});
	}

	// =====================( Button Retirer )======================
	@FXML
	void retirerArticle(ActionEvent event) {
		ObservableList<String> ligneSelectionnee = articlesTableView.getSelectionModel().getSelectedItem();
		if (ligneSelectionnee != null) {
			articlesTableView.getItems().remove(ligneSelectionnee);
			totalFactureLabel.setText(String.valueOf(calculerTotalGeneral()));
		}
	}

	// ===================( Button validerVente )====================
	@FXML
	void validerVente(ActionEvent event) {

	    // Vérifier s'il y a des articles dans la facture
	    if (articlesTableView.getItems().isEmpty()) {
	        Alert alert = new Alert(Alert.AlertType.WARNING);
	        alert.setTitle("Facture vide");
	        alert.setHeaderText(null);
	        alert.setContentText("Veuillez ajouter au moins un article avant de valider la vente.");
	        alert.showAndWait();
	        return;
	    }

	    // Alerte de confirmation
	    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
	    confirmation.setTitle("Confirmation de vente");
	    confirmation.setHeaderText("Confirmer la vente");
	    confirmation.setContentText("Êtes-vous sûr de vouloir valider cette vente ?");

	    Optional<ButtonType> result = confirmation.showAndWait();

	    if (result.isEmpty() || result.get() != ButtonType.OK) {
	        return;
	    }

	    // Calculer le total de la vente
	    double totalVente = calculerTotalGeneral();

	    // Enregistrer la vente et récupérer son ID
	    int idVente = venteDAO.ajouterVente(totalVente);

	    if (idVente <= 0) {
	        Alert alert = new Alert(Alert.AlertType.ERROR);
	        alert.setTitle("Erreur");
	        alert.setHeaderText(null);
	        alert.setContentText("Erreur lors de l'enregistrement de la vente.");
	        alert.showAndWait();
	        return;
	    }

	    // Enregistrer chaque ligne de vente et mettre à jour le stock
	    for (ObservableList<String> ligne : articlesTableView.getItems()) {
	        String nomProduit = ligne.get(0);
	        double prixUnitaire = Double.parseDouble(ligne.get(1));
	        int quantiteVendue = Integer.parseInt(ligne.get(2));

	        int idProduit = produitDAO.getIdProduitParNom(nomProduit);

	        if (idProduit > 0) {
	        	boolean ajouterLigneVenteResult = ligneVenteDAO.ajouterLigneVente(idVente, idProduit, quantiteVendue, prixUnitaire);
	        	boolean diminuerStockVenteResult = produitDAO.diminuerStock(idProduit, quantiteVendue);
	            if(!ajouterLigneVenteResult || !diminuerStockVenteResult) return;
	        }
	    }

	    // Message de succès avec le numéro de facture
	    Alert alert = new Alert(Alert.AlertType.INFORMATION);
	    alert.setTitle("Vente validée");
	    alert.setHeaderText(null);
	    alert.setContentText(
	            "Vente enregistrée avec succès!\n" +
	            "Total: " + String.format("%.2f", totalVente) + " MAD");
	    alert.showAndWait();

	    // Réinitialiser la facture
	    articlesTableView.getItems().clear();
	    produitComboBox.setValue("Sélectionner...");
	    quantiteSpinner.getValueFactory().setValue(1);
	    totalFactureLabel.setText("0.00");

	    // Recharger les données
	    chargerVentes();
	    chargerProduits();
	}
	
	// ====================( Button Actualiser )====================
	@FXML
	void actualiserVentes(ActionEvent event) {
		chargerVentes();
	}

	// ====================( Button VoirDetails )===================
	@FXML
	void voirDetails(ActionEvent event) {
		// Récupérer la vente sélectionnée
		Vente venteSelectionnee = ventesTableView.getSelectionModel().getSelectedItem();

		if (venteSelectionnee == null) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Aucune sélection");
			alert.setHeaderText(null);
			alert.setContentText("Veuillez sélectionner une vente pour voir les détails.");
			alert.showAndWait();
			return;
		}

		try {
			// Charger le FXML
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/detailVente.fxml"));
			Parent root = loader.load();

			// Récupérer le controller et passer les données
			DetailVenteController controller = loader.getController();
			controller.chargerDetails(venteSelectionnee);

			// Créer une nouvelle fenêtre
			Stage stage = new Stage();
			stage.setTitle("Détails de la vente " + venteSelectionnee.getNumeroFacture());
			stage.setScene(new Scene(root));
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Erreur");
			alert.setHeaderText(null);
			alert.setContentText("Impossible d'ouvrir les détails de la vente.");
			alert.showAndWait();
		}
	}

	// ====================( Button Supprimer Vente )===================
	@FXML
	void supprimerVente(ActionEvent event) {
		// Récupérer la vente sélectionnée
		Vente venteSelectionnee = ventesTableView.getSelectionModel().getSelectedItem();

		if (venteSelectionnee == null) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Aucune sélection");
			alert.setHeaderText(null);
			alert.setContentText("Veuillez sélectionner une vente à supprimer.");
			alert.showAndWait();
			return;
		}

		// Demander le mot de passe
		if (!demanderMotDePasse()) {
			return;
		}

		// Demander confirmation après validation du mot de passe
		Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
		confirmation.setTitle("Confirmation de suppression");
		confirmation.setHeaderText("Supprimer la vente N° " + venteSelectionnee.getNumeroFacture());
		confirmation.setContentText(
			"Êtes-vous sûr de vouloir supprimer cette vente ?\n" +
			"Date: " + venteSelectionnee.getDate_vente() + "\n" +
			"Total: " + String.format("%.2f", venteSelectionnee.getTotal()) + " MAD\n\n" +
			"Cette action est irréversible!"
		);

		Optional<ButtonType> result = confirmation.showAndWait();

		if (result.isEmpty() || result.get() != ButtonType.OK) {
			return;
		}

		// Procéder à la suppression
		try {
			// Récupérer l'ID de la vente
			int idVente = venteSelectionnee.getId_vente();
			
			// Récupérer les lignes de vente pour restaurer le stock
			List<LigneVente> lignesVente = ligneVenteDAO.getLignesVenteParIdVente(idVente);
			
			// Restaurer le stock pour chaque produit vendu
			for (LigneVente ligne : lignesVente) {
				// Récupérer le nom du produit
				// Augmenter le stock
				produitDAO.augmenterStock(ligne.getIdProduit(), ligne.getQuantite());
			}
			
			// Supprimer les lignes de vente
			boolean lignesSupprimeesResult = ligneVenteDAO.supprimerLignesVente(idVente);
			
			// Supprimer la vente
			boolean venteSupprimeesResult = venteDAO.supprimerVente(idVente);
			
			if (lignesSupprimeesResult && venteSupprimeesResult) {
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Suppression réussie");
				alert.setHeaderText(null);
				alert.setContentText("La vente N° " + idVente + " a été supprimée avec succès.");
				alert.showAndWait();
				
				// Recharger les données
				chargerVentes();
				chargerProduits();
			} else {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Erreur");
				alert.setHeaderText(null);
				alert.setContentText("Erreur lors de la suppression de la vente.");
				alert.showAndWait();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Erreur");
			alert.setHeaderText(null);
			alert.setContentText("Une erreur est survenue lors de la suppression: " + e.getMessage());
			alert.showAndWait();
		}
	}

	// =========================================================================================
	
	// Méthode simple pour demander le mot de passe
	private boolean demanderMotDePasse() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Authentification requise");
		dialog.setHeaderText("Mot de passe administrateur");
		dialog.setContentText("Veuillez entrer le mot de passe:");
		
		// Masquer le texte saisi (pour la sécurité)
		dialog.getEditor().setPromptText("Mot de passe");
		
		Optional<String> result = dialog.showAndWait();
		
		if (result.isPresent()) {
			if(!result.get().equals(ADMIN_PASSWORD)) {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Accès refusé");
				alert.setHeaderText(null);
				alert.setContentText("Mot de passe incorrect. La suppression a été annulée.");
				alert.showAndWait();
			}
			
			return result.get().equals(ADMIN_PASSWORD);
		}
		
		return false;
	}
	
	// chargerVentes
	private void chargerVentes() {
		ObservableList<Vente> ventes = FXCollections.observableArrayList(venteDAO.getAllVentes());
		ventesTableView.setItems(ventes);
	}

	// chargerProduits
	private void chargerProduits() {
		List<String> names = new ArrayList<>();
		for (Produit p : produitDAO.getAllProduits()) {
			names.add(p.getNom());
		}
		produitComboBox.setItems(FXCollections.observableArrayList(names));
	}

	// Calculer le total de tous les produits ajoutés
	private double calculerTotalGeneral() {
		double totalGeneral = 0;
		for (ObservableList<String> ligne : articlesTableView.getItems()) {
			// L'index 3 correspond à la colonne "Total" de chaque ligne
			totalGeneral += Double.parseDouble(ligne.get(3));
		}
		return totalGeneral;
	}
}