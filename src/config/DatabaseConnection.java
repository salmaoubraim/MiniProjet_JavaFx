package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    
    // Configuration de la base de données
    private static final String URL = "jdbc:mysql://localhost:3306/boutique_vetements";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    // Driver MySQL
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    
    // Obtenir une connexion à la base de données
    public static Connection getConnection() throws SQLException {
        try {
            // Charger le driver MySQL
            Class.forName(DRIVER);
            
            // Créer la connexion
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connexion à la base de données réussie: boutique_vetements");
            return conn;
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver MySQL introuvable: " + e.getMessage());
            System.err.println("💡 Ajoute mysql-connector-java dans ton projet!");
            throw new SQLException("Driver MySQL non trouvé", e);
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion à la base: " + e.getMessage());
            System.err.println("💡 Vérifie que MySQL est démarré et les credentials sont corrects!");
            throw e;
        }
    }
}