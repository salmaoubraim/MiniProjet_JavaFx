package models;

public class User {

    private int id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String role;
    private String phone;
    private boolean isActive;

    // Constructor minimal (pour compatibilité)
    public User(int id, String username, String password, String role, boolean isActive) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.isActive = isActive;
        this.fullName = username; // Par défaut
    }

    // Constructor complet
    public User(int id, String username, String password, String email, String fullName, 
                String role, String phone, boolean isActive) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.phone = phone;
        this.isActive = isActive;
    }
    
    public User(String username, String email, String role) {
        this.username = username;
        this.email = email;
        this.role = role;
    }

    // ===== GETTERS =====
    public int getId() { 
        return id; 
    }
    
    public String getUsername() { 
        return username; 
    }
    
    public String getPassword() { 
        return password; 
    }
    
    public String getEmail() { 
        return email; 
    }
    
    public String getFullName() { 
        return fullName != null ? fullName : username; 
    }
    
    public String getRole() { 
        return role; 
    }
    
    public String getPhone() { 
        return phone; 
    }
    
    public boolean isActive() { 
        return isActive; 
    }

    // ===== SETTERS =====
    public void setId(int id) { 
        this.id = id; 
    }
    
    public void setUsername(String username) { 
        this.username = username; 
    }
    
    public void setPassword(String password) { 
        this.password = password; 
    }
    
    public void setEmail(String email) { 
        this.email = email; 
    }
    
    public void setFullName(String fullName) { 
        this.fullName = fullName; 
    }
    
    public void setRole(String role) { 
        this.role = role; 
    }
    
    public void setPhone(String phone) { 
        this.phone = phone; 
    }
    
    public void setActive(boolean active) { 
        this.isActive = active; 
    }

    // ===== HELPER METHODS =====
    
    /**
     * Obtenir le rôle affiché en français
     */
    public String getRoleDisplay() {
        if (role == null) return "Employé";
        
        switch (role.toLowerCase()) {
            case "admin":
                return "Administrateur";
            case "manager":
                return "Responsable";
            case "cashier":
                return "Caissier";
            default:
                return role;
        }
    }

    /**
     * Vérifier si l'utilisateur est admin
     */
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(role);
    }

    /**
     * Vérifier si l'utilisateur est manager
     */
    public boolean isManager() {
        return "manager".equalsIgnoreCase(role);
    }

    /**
     * Obtenir initiales (pour avatar)
     */
    public String getInitials() {
        if (fullName != null && !fullName.isEmpty()) {
            String[] parts = fullName.split(" ");
            if (parts.length >= 2) {
                return (parts[0].charAt(0) + "" + parts[1].charAt(0)).toUpperCase();
            }
            return fullName.substring(0, Math.min(2, fullName.length())).toUpperCase();
        }
        return username.substring(0, Math.min(2, username.length())).toUpperCase();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role='" + role + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}