package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import config.DatabaseConnection;
import models.Vente;

public class VenteDAO {
	
	// Get All Ventes
    public List<Vente> getAllVentes() {
        List<Vente> ventes = new ArrayList<>();
        String query = "SELECT * FROM vente ORDER BY id_vente DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Vente v = new Vente(
                    rs.getInt("id_vente"),
                    rs.getString("numero_facture"),
                    rs.getTimestamp("date_vente").toLocalDateTime(),
                    rs.getDouble("total")
                );
                ventes.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ventes;
    }
    
    // ajouterVente avec génération de numéro unique
    public int ajouterVente(double total) {
        // Générer le numéro de facture unique
        String numeroFacture = genererNumeroFacture();
        
        String query = "INSERT INTO vente (numero_facture, date_vente, total) VALUES (?, NOW(), ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, numeroFacture);
            stmt.setDouble(2, total);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                // Récupérer l'ID généré
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    // supprimerVente
    public boolean supprimerVente(int idVente) {
        String query = "DELETE FROM vente WHERE id_vente = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, idVente);
            int rowsAffected = stmt.executeUpdate();
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
	// Méthode pour générer un numéro de facture unique
    private String genererNumeroFacture() {
        String numeroFacture = null;
        String query = "SELECT MAX(CAST(SUBSTRING(numero_facture, 6) AS UNSIGNED)) as max_num FROM vente";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                int maxNum = rs.getInt("max_num");
                numeroFacture = String.format("FACT-%06d", maxNum + 1);
            } else {
                numeroFacture = "FACT-000001";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // En cas d'erreur, utiliser un timestamp pour garantir l'unicité
            numeroFacture = "FACT-" + System.currentTimeMillis();
        }
        
        return numeroFacture;
    }
}