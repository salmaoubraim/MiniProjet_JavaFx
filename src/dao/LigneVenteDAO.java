package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import config.DatabaseConnection;
import models.LigneVente;

public class LigneVenteDAO {
    
    public boolean ajouterLigneVente(int idVente, int idProduit, int quantite, double prixUnitaire) {
        String query = "INSERT INTO ligne_vente (id_vente, id_produit, quantite, prix_unitaire) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, idVente);
            stmt.setInt(2, idProduit);
            stmt.setInt(3, quantite);
            stmt.setDouble(4, prixUnitaire);
            
            int rows = stmt.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Récupérer les lignes de ventes avec details
    public List<String[]> getLignesVenteAvecDetails(int idVente) {
        List<String[]> details = new ArrayList<>();
        String query = "SELECT p.name, lv.quantite, lv.prix_unitaire, (lv.quantite * lv.prix_unitaire) as total " +
                       "FROM ligne_vente lv " +
                       "INNER JOIN products p ON lv.id_produit = p.id " +
                       "WHERE lv.id_vente = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idVente);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String[] ligne = {
                    rs.getString("name"),
                    String.valueOf(rs.getInt("quantite")),
                    String.valueOf(rs.getDouble("prix_unitaire")),
                    String.valueOf(rs.getDouble("total"))
                };
                details.add(ligne);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }
    
    /**
     * Récupère toutes les lignes de vente pour une vente donnée
     * @param idVente L'ID de la vente
     * @return Liste des lignes de vente
     */
    public List<LigneVente> getLignesVenteParIdVente(int idVente) {
        List<LigneVente> lignes = new ArrayList<>();
        String query = "SELECT * FROM ligne_vente WHERE id_vente = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, idVente);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                LigneVente ligne = new LigneVente(
                    rs.getInt("id_ligne"),
                    rs.getInt("id_vente"),
                    rs.getInt("id_produit"),
                    rs.getInt("quantite"),
                    rs.getDouble("prix_unitaire")
                );
                lignes.add(ligne);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return lignes;
    }

    /**
     * Supprime toutes les lignes de vente pour une vente donnée
     * @param idVente L'ID de la vente
     * @return true si la suppression a réussi, false sinon
     */
    public boolean supprimerLignesVente(int idVente) {
        String query = "DELETE FROM ligne_vente WHERE id_vente = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, idVente);
            stmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}