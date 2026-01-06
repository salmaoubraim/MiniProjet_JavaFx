package models;

public class LigneVente {
    private int idLigne;
    private int idVente;
    private int idProduit;
    private int quantite;
    private double prixUnitaire;
    
    public LigneVente(int idLigne, int idVente, int idProduit, int quantite, double prixUnitaire) {
        this.idLigne = idLigne;
        this.idVente = idVente;
        this.idProduit = idProduit;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
    }
    
    // Getters et Setters
    public int getIdLigne() { return idLigne; }
    public void setIdLigne(int idLigne) { this.idLigne = idLigne; }
    
    public int getIdVente() { return idVente; }
    public void setIdVente(int idVente) { this.idVente = idVente; }
    
    public int getIdProduit() { return idProduit; }
    public void setIdProduit(int idProduit) { this.idProduit = idProduit; }
    
    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }
    
    public double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(double prixUnitaire) { this.prixUnitaire = prixUnitaire; }
}