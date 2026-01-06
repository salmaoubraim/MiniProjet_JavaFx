package dao;
import java.sql.*;
import java.util.*;
import config.DatabaseConnection;

public class StatistiquesDAO {
    
    // ==================== STATISTIQUES GLOBALES ====================
    public Map<String, Object> getStatistiquesPeriode(String periode) {
        Map<String, Object> stats = new HashMap<>();
        String condition = getConditionPeriode(periode);
        
        String query = "SELECT " +
                      "COALESCE(SUM(v.total), 0) as chiffre_affaires, " +
                      "COUNT(DISTINCT v.id_vente) as nombre_ventes, " +
                      "COALESCE((SELECT SUM(quantite) FROM ligne_vente lv2 " +
                      "INNER JOIN vente v2 ON lv2.id_vente = v2.id_vente " +
                      "WHERE " + condition.replace("v.", "v2.") + "), 0) as articles_vendus " +
                      "FROM vente v " +
                      "WHERE " + condition;
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                stats.put("chiffre_affaires", rs.getDouble("chiffre_affaires"));
                stats.put("nombre_ventes", rs.getInt("nombre_ventes"));
                stats.put("articles_vendus", rs.getInt("articles_vendus"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return stats;
    }
    
    // ==================== ÉVOLUTION DES VENTES ====================
    public List<Map<String, Object>> getEvolutionVentes(String periode) {
        List<Map<String, Object>> evolution = new ArrayList<>();

        String condition = getConditionPeriode(periode);
        String groupBy = getGroupByPeriode(periode);
        
        String query =
                "SELECT " + groupBy + " AS periode, SUM(v.total) AS chiffre_affaires " +
                "FROM vente v " +
                "WHERE " + condition + " " +
                "GROUP BY " + groupBy + " " +
                "ORDER BY " + groupBy;
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Map<String, Object> point = new HashMap<>();
                point.put("periode", rs.getString("periode"));
                point.put("chiffre_affaires", rs.getDouble("chiffre_affaires"));
                evolution.add(point);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return evolution;
    }
    
    // ==================== TOP 5 PRODUITS ====================
    public List<Map<String, Object>> getTop5Produits(String periode) {
        List<Map<String, Object>> top5 = new ArrayList<>();
        String condition = getConditionPeriode(periode);
        
        String query = "SELECT p.name, " +
                      "SUM(lv.quantite) as quantite, " +
                      "SUM(lv.quantite * lv.prix_unitaire) as ca " +
                      "FROM ligne_vente lv " +
                      "INNER JOIN products p ON lv.id_produit = p.id " +
                      "INNER JOIN vente v ON lv.id_vente = v.id_vente " +
                      "WHERE " + condition + " " +
                      "GROUP BY p.id, p.name " +
                      "ORDER BY quantite DESC " +
                      "LIMIT 5";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Map<String, Object> produit = new HashMap<>();
                produit.put("name", rs.getString("name"));
                produit.put("quantite", rs.getInt("quantite"));
                produit.put("ca", rs.getDouble("ca"));
                top5.add(produit);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return top5;
    }
    
    // ==================== MÉTHODES UTILITAIRES ====================
    private String getConditionPeriode(String periode) {
        switch (periode) {
            case "Aujourd'hui":
                return "DATE(v.date_vente) = CURDATE()";
            case "Hier":
                return "DATE(v.date_vente) = DATE_SUB(CURDATE(), INTERVAL 1 DAY)";
            case "Cette semaine":
                return "YEARWEEK(v.date_vente, 1) = YEARWEEK(CURDATE(), 1)";
            case "Ce mois":
                return "MONTH(v.date_vente) = MONTH(CURDATE()) AND YEAR(v.date_vente) = YEAR(CURDATE())";
            case "Cette année":
                return "YEAR(v.date_vente) = YEAR(CURDATE())";
            default:
                return "1=1";
        }
    }
    
    private String getGroupByPeriode(String periode) {
        switch (periode) {
            case "Aujourd'hui":
            case "Hier":
                return "TIME(v.date_vente)";
            case "Cette semaine":
                return "DAYNAME(v.date_vente)";
            case "Ce mois":
                return "DATE(v.date_vente)";
            case "Cette année":
                return "MONTH(v.date_vente)";
            default:
                return "YEAR(v.date_vente)";
        }
    }
}