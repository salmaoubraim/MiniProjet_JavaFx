package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Produit;
import dao.ProduitDAO;

import java.io.File;

public class AddProductFormController {
    
    @FXML private TextField txtName;
    @FXML private TextField txtCategory;
    @FXML private TextField txtPrice;
    @FXML private TextField txtStock;
    @FXML private TextField txtSize;
    @FXML private TextField txtColor;
    @FXML private TextArea txtDescription;
    @FXML private ImageView imgPreview;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;
    @FXML private Button btnChooseImage;
    
    private Produit produit; // produit à modifier (null si ajout)
    private ProduitDAO produitDAO = new ProduitDAO();
    private String selectedImagePath;
    
    // callback pour rafraîchir la table
    public Runnable refreshTableAfterSave;
    
    @FXML
    public void initialize() {
        // bouton pour choisir une image
        btnChooseImage.setOnAction(e -> chooseImage());
    }
    
    public void setProduit(Produit produit) {
        this.produit = produit;
        if (produit != null) {
            txtName.setText(produit.getNom());
            txtCategory.setText(produit.getCategorie());
            txtPrice.setText(String.valueOf(produit.getPrix()));
            txtStock.setText(String.valueOf(produit.getStock()));
            txtSize.setText(produit.getTaille());
            txtColor.setText(produit.getCouleur());
            txtDescription.setText(produit.getDescription());
            
            if (produit.getImageView() != null && produit.getImageView().getImage() != null) {
                imgPreview.setImage(produit.getImageView().getImage());
            }
        }
    }
    
    private void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File file = fileChooser.showOpenDialog(btnChooseImage.getScene().getWindow());
        if (file != null) {
            selectedImagePath = file.getAbsolutePath();
            Image img = new Image(file.toURI().toString());
            imgPreview.setImage(img);
            imgPreview.setFitWidth(100);
            imgPreview.setFitHeight(100);
            imgPreview.setPreserveRatio(true);
        }
    }
    
    @FXML
    private void handleSave() {
        try {
            String name = txtName.getText().trim();
            String category = txtCategory.getText().trim();
            String priceText = txtPrice.getText().trim();
            String stockText = txtStock.getText().trim();
            String size = txtSize.getText().trim();
            String color = txtColor.getText().trim();
            String description = txtDescription.getText().trim();
            
            // Validation
            if (name.isEmpty() || priceText.isEmpty() || stockText.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs obligatoires.");
                alert.showAndWait();
                return;
            }
            
            double price = Double.parseDouble(priceText);
            int stock = Integer.parseInt(stockText);
            
            boolean success;
            
            if (produit == null) { 
                // Ajout - ORDRE CORRECT: id, nom, categorie, prix, stock, taille, couleur, description
                Produit newProduit = new Produit(
                    null,
                    name,
                    category,
                    price,
                    stock,
                    size,
                    color,
                    description
                );
                success = produitDAO.addProduit(newProduit, selectedImagePath);
            } else { 
                // Modification - ORDRE CORRECT: id, nom, categorie, prix, taille, stock, couleur, description
                Produit updatedProduit = new Produit(
                    produit.getId(),
                    name,
                    category,
                    price,
                    size,
                    stock,
                    color,
                    description
                );
                success = produitDAO.updateProduit(updatedProduit, selectedImagePath);
            }
            
            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Produit enregistré avec succès!");
                alert.showAndWait();
                
                if (refreshTableAfterSave != null) {
                    refreshTableAfterSave.run();
                }
                
                ((Stage) btnSave.getScene().getWindow()).close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'enregistrement.");
                alert.showAndWait();
            }
            
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Prix et Quantité doivent être des nombres valides.");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'enregistrement: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    @FXML
    private void handleCancel() {
        ((Stage) btnCancel.getScene().getWindow()).close();
    }
}