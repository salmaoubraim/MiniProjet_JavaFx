package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Produit;
import dao.ProduitDAO;

import java.util.List;

@SuppressWarnings("unused")
public class ProductsController {

    @FXML private TextField txtSearch;
    @FXML private TableView<Produit> productsTableView;
    @FXML private TableColumn<Produit, ImageView> colImage;
    @FXML private TableColumn<Produit, String> colName;
    @FXML private TableColumn<Produit, String> colCategory;
    @FXML private TableColumn<Produit, Double> colPrice;
    @FXML private TableColumn<Produit, Integer> colStock;
    @FXML private TableColumn<Produit, String> colSize;
    @FXML private TableColumn<Produit, String> colColor;
    @FXML private TableColumn<Produit, Void> colAction;

    private ObservableList<Produit> productList = FXCollections.observableArrayList();
    private ObservableList<Produit> filteredList = FXCollections.observableArrayList();

    private ProduitDAO produitDAO = new ProduitDAO();

    @FXML
    public void initialize() {
        // Colonnes
        colImage.setCellValueFactory(new PropertyValueFactory<>("imageView"));
        colName.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colSize.setCellValueFactory(new PropertyValueFactory<>("taille"));
        colColor.setCellValueFactory(new PropertyValueFactory<>("couleur"));

        setupActionColumn();
        loadProducts();
        setupSearch();
    }

    private void loadProducts() {
        List<Produit> produits = produitDAO.getAllProduits();
        productList.setAll(produits);
        filteredList.setAll(productList);
        productsTableView.setItems(filteredList);
    }

    private void setupSearch() {
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                filteredList.setAll(productList);
            } else {
                String txt = newVal.toLowerCase();
                filteredList.setAll(productList.filtered(p ->
                        p.getNom().toLowerCase().contains(txt) ||
                        (p.getCategorie() != null && p.getCategorie().toLowerCase().contains(txt)) ||
                        (p.getCouleur() != null && p.getCouleur().toLowerCase().contains(txt)) ||
                        (p.getTaille() != null && p.getTaille().toLowerCase().contains(txt))
                ));
            }
        });
    }

    private void setupActionColumn() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnView = new Button();
            private final Button btnEdit = new Button();
            private final Button btnDelete = new Button();
            private final HBox hbox = new HBox(5, btnView, btnEdit, btnDelete);

            {
                hbox.setStyle("-fx-alignment: center;");

                // Charger les icônes depuis resources/images/
                btnView.setGraphic(createIcon("/images/view.png"));
                btnEdit.setGraphic(createIcon("/images/edit.png"));
                btnDelete.setGraphic(createIcon("/images/delete.png"));

                String style = "-fx-background-color: transparent; -fx-cursor: hand;";
                btnView.setStyle(style);
                btnEdit.setStyle(style);
                btnDelete.setStyle(style);

                btnView.setOnAction(e -> {
                    if (getIndex() < getTableView().getItems().size()) {
                        showDetails(getTableView().getItems().get(getIndex()));
                    }
                });
                btnEdit.setOnAction(e -> {
                    if (getIndex() < getTableView().getItems().size()) {
                        showForm(getTableView().getItems().get(getIndex()));
                    }
                });
                btnDelete.setOnAction(e -> {
                    if (getIndex() < getTableView().getItems().size()) {
                        deleteProduct(getTableView().getItems().get(getIndex()));
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
    }

    /** Méthode utilitaire pour créer ImageView depuis PNG */
    private ImageView createIcon(String path) {
        ImageView iv = new ImageView(new javafx.scene.image.Image(getClass().getResourceAsStream(path)));
        iv.setFitWidth(18);    // largeur
        iv.setFitHeight(18);   // hauteur
        iv.setPreserveRatio(true);
        return iv;
    }

    @FXML
    private void handleAddNewProduct() {
        showForm(null);
    }

    private void showDetails(Produit product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ViewProductForm.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Détails du Produit");

            ViewProductFormController controller = loader.getController();
            controller.setProduct(product);

            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback: afficher un Alert simple
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Détails du produit");
            info.setHeaderText(product.getNom());
            info.setContentText("Catégorie: " + product.getCategorie() +
                    "\nPrix: " + product.getPrix() + " MAD" +
                    "\nStock: " + product.getStock() +
                    "\nTaille: " + product.getTaille() +
                    "\nCouleur: " + product.getCouleur() +
                    "\nDescription: " + product.getDescription());
            info.showAndWait();
        }
    }

    private void showForm(Produit product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddProductForm.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(product == null ? "Ajouter Produit" : "Modifier Produit");

            AddProductFormController controller = loader.getController();
            controller.setProduit(product);
            controller.refreshTableAfterSave = this::loadProducts;

            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture du formulaire: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void deleteProduct(Produit product) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Supprimer produit");
        confirm.setHeaderText("Supprimer " + product.getNom() + "?");
        confirm.setContentText("Cette action est irréversible.");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                boolean success = produitDAO.deleteProduit(product.getId());
                if (success) {
                    loadProducts();
                    Alert info = new Alert(Alert.AlertType.INFORMATION, "Produit supprimé avec succès!");
                    info.showAndWait();
                } else {
                    Alert error = new Alert(Alert.AlertType.ERROR, "Erreur lors de la suppression.");
                    error.showAndWait();
                }
            }
        });
    }
}