package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import config.DatabaseConnection;
import models.User;

public class UserDAO {

    /**
     * Authentification d'un utilisateur
     */
    public User login(String username, String password) {
        String sql = "SELECT id, username, password, email, full_name, role, phone, is_active " +
                     "FROM users " +
                     "WHERE TRIM(username) = TRIM(?) " +
                     "AND TRIM(password) = TRIM(?) " +
                     "AND is_active = 1";

        System.out.println("\n🔍 ===== TENTATIVE DE LOGIN =====");
        System.out.println("📝 Username: [" + username + "]");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String dbUsername = rs.getString("username");
                    String dbPassword = rs.getString("password");
                    String dbRole = rs.getString("role");
                    boolean isActive = rs.getBoolean("is_active");

                    System.out.println("✅ UTILISATEUR TROUVÉ");
                    System.out.println("🆔 ID: " + id);
                    System.out.println("🎭 Rôle: " + dbRole);
                    System.out.println("================================\n");

                    User user = new User(id, dbUsername, dbPassword, dbRole, isActive);
                    updateLastLogin(id);
                    return user;
                } else {
                    System.out.println("❌ ÉCHEC LOGIN - Identifiants incorrects\n");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ ERREUR SQL LOGIN: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * ★ NOUVELLE MÉTHODE: Enregistrer un nouvel utilisateur
     */
    public boolean registerUser(String username, String password, String email, 
                               String fullName, String phone, String role) {
        
        String sql = "INSERT INTO users (username, password, email, full_name, phone, role, is_active, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, 1, NOW())";

        System.out.println("\n📝 ===== ENREGISTREMENT UTILISATEUR =====");
        System.out.println("👤 Username: " + username);
        System.out.println("📧 Email: " + email);
        System.out.println("👨 Nom: " + fullName);
        System.out.println("🎭 Rôle: " + role);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.setString(4, fullName);
            stmt.setString(5, phone.isEmpty() ? null : phone);
            stmt.setString(6, role);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Utilisateur créé avec succès!");
                System.out.println("========================================\n");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ ERREUR REGISTRATION: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * ★ NOUVELLE MÉTHODE: Vérifier si username existe
     */
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE TRIM(username) = TRIM(?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    if (count > 0) {
                        System.out.println("⚠️ Username [" + username + "] existe déjà");
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur vérification username: " + e.getMessage());
        }

        return false;
    }

    /**
     * ★ NOUVELLE MÉTHODE: Vérifier si email existe
     */
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE TRIM(email) = TRIM(?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    if (count > 0) {
                        System.out.println("⚠️ Email [" + email + "] existe déjà");
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur vérification email: " + e.getMessage());
        }

        return false;
    }

    /**
     * Mettre à jour last_login
     */
    private void updateLastLogin(int userId) {
        String sql = "UPDATE users SET last_login = NOW() WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            System.out.println("📅 Last login mis à jour");
            
        } catch (SQLException e) {
            System.err.println("⚠️ Erreur last_login: " + e.getMessage());
        }
    }

    /**
     * Lister TOUS les utilisateurs (debug)
     */
    public void listAllUsers() {
        String sql = "SELECT id, username, password, full_name, role, is_active FROM users ORDER BY id";
        
        System.out.println("\n📋 ===== TOUS LES UTILISATEURS =====");
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println("\n👤 Utilisateur #" + count);
                System.out.println("   ID: " + rs.getInt("id"));
                System.out.println("   Username: [" + rs.getString("username") + "]");
                System.out.println("   Nom: " + rs.getString("full_name"));
                System.out.println("   Rôle: " + rs.getString("role"));
                System.out.println("   Actif: " + rs.getBoolean("is_active"));
            }
            
            System.out.println("\n📊 Total: " + count + " utilisateur(s)");
            System.out.println("=====================================\n");
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur liste users: " + e.getMessage());
        }
    }
}