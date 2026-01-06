package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import config.DatabaseConnection;
import models.Produit;

public class ProduitDAO {

    // ==================== RÉCUPÉRATION DES PRODUITS ====================
    public List<Produit> getAllProduits() {
        List<Produit> produits = new ArrayList<>();
        String query = "SELECT id, name, category, price, size, stock, color, description, image_url FROM products WHERE is_active = 1";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Produit p = new Produit(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getDouble("price"),
                    rs.getString("size"),
                    rs.getInt("stock"),
                    rs.getString("color"),
                    rs.getString("description")
                );
                
                // ✅ Charger l'image
                String imageUrl = rs.getString("image_url");
                javafx.scene.image.ImageView imgView = loadImage(imageUrl);
                p.setImageView(imgView);
                produits.add(p);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des produits: " + e.getMessage());
            e.printStackTrace();
        }
        return produits;
    }
    
    // ==================== GESTION DES IMAGES ====================
    private javafx.scene.image.ImageView loadImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return createDefaultImage();
        }
        
        // Tentative 1: Chemin absolu
        try {
            java.io.File imgFile = new java.io.File(imageUrl);
            if (imgFile.exists()) {
                javafx.scene.image.Image img = new javafx.scene.image.Image(
                    imgFile.toURI().toString(), 60, 60, true, true
                );
                System.out.println("✅ Image chargée (absolu): " + imageUrl);
                return new javafx.scene.image.ImageView(img);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Échec chemin absolu: " + imageUrl);
        }
        
        // Tentative 2: Relatif au working directory (images/fichier.jpg)
        try {
            String workingDir = System.getProperty("user.dir");
            java.io.File imgFile = new java.io.File(workingDir, imageUrl);
            if (imgFile.exists()) {
                javafx.scene.image.Image img = new javafx.scene.image.Image(
                    imgFile.toURI().toString(), 60, 60, true, true
                );
                System.out.println("✅ Image chargée (relatif): " + imgFile.getAbsolutePath());
                return new javafx.scene.image.ImageView(img);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Échec chemin relatif: " + imageUrl);
        }
        
        // Tentative 3: Resources (/images/fichier.jpg)
        try {
            String resourcePath = imageUrl.startsWith("/") ? imageUrl : "/" + imageUrl;
            java.io.InputStream imgStream = getClass().getResourceAsStream(resourcePath);
            if (imgStream != null) {
                javafx.scene.image.Image img = new javafx.scene.image.Image(imgStream, 60, 60, true, true);
                System.out.println("✅ Image chargée (resources): " + resourcePath);
                return new javafx.scene.image.ImageView(img);
            }
        } catch (Exception e) {
            System.err.println("⚠️ Échec resources: " + imageUrl);
        }
        
        // Toutes les tentatives ont échoué
        System.out.println("❌ Impossible de charger: " + imageUrl);
        return createDefaultImage();
    }
    
    /**
     * Crée une ImageView par défaut
     */
    private javafx.scene.image.ImageView createDefaultImage() {
        try {
            // Essayer de charger default.jpg depuis resources
            java.io.InputStream stream = getClass().getResourceAsStream("/images/default.jpg");
            if (stream != null) {
                javafx.scene.image.Image img = new javafx.scene.image.Image(stream, 60, 60, true, true);
                return new javafx.scene.image.ImageView(img);
            }
        } catch (Exception e) {
            // Ignorer
        }
        
        // Créer une image placeholder simple
        javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas(60, 60);
        javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(javafx.scene.paint.Color.web("#9C7A5C"));
        gc.fillRect(0, 0, 60, 60);
        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.setFont(new javafx.scene.text.Font(24));
        gc.fillText("?", 23, 40);
        
        javafx.scene.image.WritableImage img = new javafx.scene.image.WritableImage(60, 60);
        canvas.snapshot(null, img);
        return new javafx.scene.image.ImageView(img);
    }

    // ==================== AJOUT DE PRODUITS ====================
    public boolean addProduit(Produit produit) {
        return addProduit(produit, null);
    }
    
    public boolean addProduit(Produit produit, String imagePath) {
        String query = "INSERT INTO products (name, category, price, stock, size, color, description, image_url, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, produit.getNom());
            stmt.setString(2, produit.getCategorie() != null ? produit.getCategorie() : "");
            stmt.setDouble(3, produit.getPrix());
            stmt.setInt(4, produit.getStock());
            stmt.setString(5, produit.getTaille() != null ? produit.getTaille() : "");
            stmt.setString(6, produit.getCouleur() != null ? produit.getCouleur() : "");
            stmt.setString(7, produit.getDescription() != null ? produit.getDescription() : "");
            stmt.setString(8, imagePath);

            int rows = stmt.executeUpdate();
            
            if (rows > 0) {
                System.out.println("✅ Produit ajouté: " + produit.getNom());
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout du produit: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ==================== MODIFICATION DE PRODUITS ====================
    public boolean updateProduit(Produit produit) {
        return updateProduit(produit, null);
    }
    
    public boolean updateProduit(Produit produit, String imagePath) {
        String query;
        
        if (imagePath != null) {
            query = "UPDATE products SET name = ?, category = ?, price = ?, stock = ?, size = ?, color = ?, description = ?, image_url = ? WHERE id = ?";
        } else {
            query = "UPDATE products SET name = ?, category = ?, price = ?, stock = ?, size = ?, color = ?, description = ? WHERE id = ?";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, produit.getNom());
            stmt.setString(2, produit.getCategorie() != null ? produit.getCategorie() : "");
            stmt.setDouble(3, produit.getPrix());
            stmt.setInt(4, produit.getStock());
            stmt.setString(5, produit.getTaille() != null ? produit.getTaille() : "");
            stmt.setString(6, produit.getCouleur() != null ? produit.getCouleur() : "");
            stmt.setString(7, produit.getDescription() != null ? produit.getDescription() : "");
            
            if (imagePath != null) {
                stmt.setString(8, imagePath);
                stmt.setInt(9, produit.getId());
            } else {
                stmt.setInt(8, produit.getId());
            }

            int rows = stmt.executeUpdate();
            
            if (rows > 0) {
                System.out.println("✅ Produit modifié: " + produit.getNom());
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification du produit: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ==================== SUPPRESSION DE PRODUITS ====================
    public boolean deleteProduit(int id) {
        String query = "DELETE FROM products WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            
            if (rows > 0) {
                System.out.println("✅ Produit supprimé (ID: " + id + ")");
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression du produit: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    // ==================== GESTION DU STOCK ====================
    public boolean diminuerStock(int idProduit, int quantite) {
        String query = "UPDATE products SET stock = stock - ? WHERE id = ? AND stock >= ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, quantite);
            stmt.setInt(2, idProduit);
            stmt.setInt(3, quantite);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("❌ Erreur: " + e.getMessage());
            return false;
        }
    }

    public boolean augmenterStock(int idProduit, int quantite) {
        String query = "UPDATE products SET stock = stock + ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, quantite);
            stmt.setInt(2, idProduit);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ==================== RÉCUPÉRATION DES PRODUITS ====================
    
    // -------------------- Produits Par Id --------------------
    
    public Produit getProduitParId(int idProduit) {
        String query = "SELECT * FROM products WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, idProduit);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Produit(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getDouble("price"),
                    rs.getString("size"),
                    rs.getInt("stock"),
                    rs.getString("color"),
                    rs.getString("description")
                );
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // -------------------- Produits Par nom --------------------
    
    public int getIdProduitParNom(String nom) {
        String query = "SELECT id FROM products WHERE name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nom);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur: " + e.getMessage());
        }
        return -1;
    }

    // -------------------- Produits en STOCK faible --------------------
    
    public List<Produit> getProduitsStockFaible(int seuil) {
        List<Produit> produits = new ArrayList<>();
        String query = "SELECT * FROM products WHERE stock < ? AND is_active = 1 ORDER BY stock ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, seuil);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Produit p = new Produit(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getDouble("price"),
                    rs.getString("size"),
                    rs.getInt("stock"),
                    rs.getString("color"),
                    rs.getString("description")
                );
                produits.add(p);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur: " + e.getMessage());
        }
        return produits;
    }
}