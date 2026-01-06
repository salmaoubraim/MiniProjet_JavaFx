package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import models.Produit;

public class ViewProductFormController {

    @FXML private Label lblName, lblCategory, lblPrice, lblStock, lblSize, lblColor;
    @FXML private TextArea txtDescription;
    @FXML private ImageView imgView;
    @FXML private Button btnClose;

    public void setProduct(Produit product) {
        lblName.setText("Nom: " + product.getNom());
        lblCategory.setText("Catégorie: " + product.getCategorie());
        lblPrice.setText("Prix: " + product.getPrix() + " MAD");
        lblStock.setText("Quantité: " + product.getStock());
        lblSize.setText("Taille: " + product.getTaille());
        lblColor.setText("Couleur: " + product.getCouleur());
        txtDescription.setText(product.getDescription());

        // Si tu veux afficher l'image
        if (product.getImageView() != null) imgView.setImage(product.getImageView().getImage());
    }

    @FXML
    private void handleClose() {
        ((Stage) btnClose.getScene().getWindow()).close();
    }
}
