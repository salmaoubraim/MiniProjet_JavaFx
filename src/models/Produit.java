package models;

import javafx.scene.image.ImageView;

public class Produit {
    
    private Integer id;
    private String nom;
    private String categorie;
    private double prix;
    private String taille;
    private int stock;
    private String couleur;
    private String description;
    private ImageView imageView;
    
    // Constructeur pour ajout (sans ID)
    public Produit(Integer id, String nom, String categorie, double prix, int stock, 
                   String taille, String couleur, String description) {
        this.id = id;
        this.nom = nom;
        this.categorie = categorie;
        this.prix = prix;
        this.stock = stock;
        this.taille = taille;
        this.couleur = couleur;
        this.description = description;
    }
    
    // Constructeur compatible avec ProduitDAO.getAllProduits()
    public Produit(int id, String nom, String categorie, double prix, 
                   String taille, int stock, String couleur, String description) {
        this.id = id;
        this.nom = nom;
        this.categorie = categorie;
        this.prix = prix;
        this.taille = taille;
        this.stock = stock;
        this.couleur = couleur;
        this.description = description;
    }
    
    // Constructeur pour formulaire (ajouter)
    public Produit(String nom, String categorie, double prix, int stock, 
                   String taille, String couleur, String description, ImageView imageView) {
        this.nom = nom;
        this.categorie = categorie;
        this.prix = prix;
        this.stock = stock;
        this.taille = taille;
        this.couleur = couleur;
        this.description = description;
        this.imageView = imageView;
    }
    
    // Getters
    public Integer getId() {
        return id;
    }
    
    public String getNom() {
        return nom;
    }
    
    public String getCategorie() {
        return categorie;
    }
    
    public double getPrix() {
        return prix;
    }
    
    public String getTaille() {
        return taille;
    }
    
    public int getStock() {
        return stock;
    }
    
    public String getCouleur() {
        return couleur;
    }
    
    public String getDescription() {
        return description;
    }
    
    public ImageView getImageView() {
        return imageView;
    }
    
    // Setters
    public void setId(Integer id) {
        this.id = id;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }
    
    public void setPrix(double prix) {
        this.prix = prix;
    }
    
    public void setTaille(String taille) {
        this.taille = taille;
    }
    
    public void setStock(int stock) {
        this.stock = stock;
    }
    
    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }
    
    @Override
    public String toString() {
        return "Produit{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", categorie='" + categorie + '\'' +
                ", prix=" + prix +
                ", taille='" + taille + '\'' +
                ", stock=" + stock +
                ", couleur='" + couleur + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}