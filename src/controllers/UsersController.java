package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.User;
import config.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@SuppressWarnings("unused")
public class UsersController {

    @FXML private TextField txtSearch;
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, Void> colActions;

    private ObservableList<User> userList = FXCollections.observableArrayList();
    private ObservableList<User> filteredList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Colonnes
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        setupActionColumn();
        loadUsers();
        setupSearch();
    }

    private void loadUsers() {
        userList.clear();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT username, email, role FROM users WHERE is_active=1";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String username = rs.getString("username");
                String email = rs.getString("email");
                String role = rs.getString("role");
                userList.add(new User(username, email, role));
            }
            filteredList.setAll(userList);
            userTable.setItems(filteredList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupSearch() {
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                filteredList.setAll(userList);
            } else {
                String txt = newVal.toLowerCase();
                filteredList.setAll(userList.filtered(u ->
                        u.getUsername().toLowerCase().contains(txt) ||
                        (u.getEmail() != null && u.getEmail().toLowerCase().contains(txt)) ||
                        (u.getRole() != null && u.getRole().toLowerCase().contains(txt))
                ));
            }
        });
    }

    private void setupActionColumn() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button();
            private final Button btnDelete = new Button();
            private final HBox hbox = new HBox(5, btnEdit, btnDelete);

            {
                hbox.setStyle("-fx-alignment: center;");

                // Charger les icônes depuis resources/images/
                btnEdit.setGraphic(createIcon("/images/edit1.png"));
                btnDelete.setGraphic(createIcon("/images/delete.png"));

                String style = "-fx-background-color: transparent; -fx-cursor: hand;";
                btnEdit.setStyle(style);
                btnDelete.setStyle(style);
                
                btnEdit.setOnAction(e -> {
                    if (getIndex() < getTableView().getItems().size()) {
                        showEditForm(getTableView().getItems().get(getIndex()));
                    }
                });
                btnDelete.setOnAction(e -> {
                    if (getIndex() < getTableView().getItems().size()) {
                        deleteUser(getTableView().getItems().get(getIndex()));
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
    }

    /** Méthode utilitaire pour créer ImageView depuis PNG */
    private ImageView createIcon(String path) {
        ImageView iv = new ImageView(new javafx.scene.image.Image(getClass().getResourceAsStream(path)));
        iv.setFitWidth(18);    // largeur
        iv.setFitHeight(18);   // hauteur
        iv.setPreserveRatio(true);
        return iv;
    }

    @FXML
    private void handleAdd() {
        showEditForm(null);
    }

    private void showUserDetails(User user) {
        // View button - just show a simple alert with user info
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Détails de l'utilisateur");
        info.setHeaderText(user.getUsername());
        info.setContentText(
                "Email: " + user.getEmail() + 
                "\nRôle: " + user.getRole()
        );
        info.showAndWait();
    }

    private void showEditForm(User user) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(user == null ? "Ajouter un utilisateur" : "Modifier - " + user.getUsername());

        // VBox principal avec background beige
        javafx.scene.layout.VBox mainBox = new javafx.scene.layout.VBox(20);
        mainBox.setStyle("-fx-background-color: #f5f0eb;");
        mainBox.setPadding(new javafx.geometry.Insets(30));

        // Card blanc avec shadow
        javafx.scene.layout.VBox card = new javafx.scene.layout.VBox(20);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(156,106,74,0.2), 15,0,0,3);");
        card.setPadding(new javafx.geometry.Insets(25));

        // Titre
        Label lblTitle = new Label(user == null ? "Ajouter un Utilisateur" : "Modifier l'utilisateur");
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 22; -fx-text-fill: #6B4423;");

        // Separator
        javafx.scene.control.Separator separator = new javafx.scene.control.Separator();
        separator.setStyle("-fx-background-color: #D4B896;");

        // Form VBox
        javafx.scene.layout.VBox formBox = new javafx.scene.layout.VBox(15);

        // Nom d'utilisateur
        javafx.scene.layout.VBox usernameBox = new javafx.scene.layout.VBox(8);
        Label lblUsername = new Label("Nom d'utilisateur");
        lblUsername.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #6B4423;");
        TextField txtName = new TextField(user != null ? user.getUsername() : "");
        txtName.setPromptText("Entrez le nom d'utilisateur");
        txtName.setStyle("-fx-background-color: #FAF7F4; -fx-border-color: #C9A88A; -fx-border-radius: 8; -fx-border-width: 1.5; -fx-font-size: 14; -fx-pref-height: 40;");
        txtName.setPadding(new javafx.geometry.Insets(10, 15, 10, 15));
        usernameBox.getChildren().addAll(lblUsername, txtName);

        // Email
        javafx.scene.layout.VBox emailBox = new javafx.scene.layout.VBox(8);
        Label lblEmail = new Label("Email");
        lblEmail.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #6B4423;");
        TextField txtEmail = new TextField(user != null ? user.getEmail() : "");
        txtEmail.setPromptText("Entrez l'email");
        txtEmail.setStyle("-fx-background-color: #FAF7F4; -fx-border-color: #C9A88A; -fx-border-radius: 8; -fx-border-width: 1.5; -fx-font-size: 14; -fx-pref-height: 40;");
        txtEmail.setPadding(new javafx.geometry.Insets(10, 15, 10, 15));
        emailBox.getChildren().addAll(lblEmail, txtEmail);

        // Mot de passe (seulement pour ajout)
        javafx.scene.layout.VBox passwordBox = new javafx.scene.layout.VBox(8);
        PasswordField txtPassword = new PasswordField();
        if (user == null) {
            Label lblPassword = new Label("Mot de passe");
            lblPassword.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #6B4423;");
            txtPassword.setPromptText("Entrez le mot de passe");
            txtPassword.setStyle("-fx-background-color: #FAF7F4; -fx-border-color: #C9A88A; -fx-border-radius: 8; -fx-border-width: 1.5; -fx-font-size: 14; -fx-pref-height: 40;");
            txtPassword.setPadding(new javafx.geometry.Insets(10, 15, 10, 15));
            passwordBox.getChildren().addAll(lblPassword, txtPassword);
        }

        // Rôle
        javafx.scene.layout.VBox roleBox = new javafx.scene.layout.VBox(8);
        Label lblRole = new Label("Rôle");
        lblRole.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #6B4423;");
        ComboBox<String> cbRole = new ComboBox<>(FXCollections.observableArrayList("admin", "manager", "cashier"));
        cbRole.setValue(user != null ? user.getRole() : "cashier");
        cbRole.setPromptText("Sélectionnez le rôle");
        cbRole.setPrefWidth(450);
        cbRole.setStyle("-fx-background-color: #FAF7F4; -fx-border-color: #C9A88A; -fx-border-radius: 8; -fx-border-width: 1.5; -fx-font-size: 14;");
        roleBox.getChildren().addAll(lblRole, cbRole);

        // Ajouter les champs au form
        formBox.getChildren().addAll(usernameBox, emailBox);
        if (user == null) {
            formBox.getChildren().add(passwordBox);
        }
        formBox.getChildren().add(roleBox);

        // Boutons
        javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox(15);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        Button btnCancel = new Button("Annuler");
        btnCancel.setStyle("-fx-background-color: #D4B896; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 14; -fx-pref-height: 40; -fx-pref-width: 120;");
        btnCancel.setOnAction(e -> stage.close());

        Button btnSave = new Button(user == null ? "Ajouter" : "Enregistrer");
        btnSave.setStyle("-fx-background-color: #9C7A5C; -fx-text-fill: white; -fx-background-radius: 8; -fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 14; -fx-pref-height: 40; -fx-pref-width: 120;");
        btnSave.setOnAction(e -> {
            if (txtName.getText().isEmpty() || txtEmail.getText().isEmpty() || cbRole.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs obligatoires.");
                alert.showAndWait();
                return;
            }
            if (user == null && txtPassword.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Le mot de passe est obligatoire.");
                alert.showAndWait();
                return;
            }

            if (user == null) {
                insertUser(txtName.getText(), txtEmail.getText(), txtPassword.getText(), cbRole.getValue());
            } else {
                updateUser(user, txtName.getText(), txtEmail.getText(), cbRole.getValue());
            }
            stage.close();
        });

        buttonBox.getChildren().addAll(btnCancel, btnSave);

        // Assembler la card
        card.getChildren().addAll(lblTitle, separator, formBox, buttonBox);
        mainBox.getChildren().add(card);

        Scene scene = new Scene(mainBox, 550, user == null ? 520 : 450);
        stage.setScene(scene);
        stage.showAndWait();
    }

    private void insertUser(String username, String email, String password, String role) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO users(username, email, role, password) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, role);
            stmt.setString(4, password);
            stmt.executeUpdate();
            loadUsers();
            Alert info = new Alert(Alert.AlertType.INFORMATION, "Utilisateur ajouté avec succès!");
            info.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ajout: " + ex.getMessage());
            alert.showAndWait();
        }
    }

    private void updateUser(User user, String newUsername, String newEmail, String newRole) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE users SET username=?, email=?, role=? WHERE username=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newUsername);
            stmt.setString(2, newEmail);
            stmt.setString(3, newRole);
            stmt.setString(4, user.getUsername());
            stmt.executeUpdate();
            loadUsers();
            Alert info = new Alert(Alert.AlertType.INFORMATION, "Utilisateur modifié avec succès!");
            info.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la modification.");
            alert.showAndWait();
        }
    }

    private void deleteUser(User user) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Supprimer utilisateur");
        confirm.setHeaderText("Supprimer " + user.getUsername() + "?");
        confirm.setContentText("Cette action désactivera l'utilisateur.");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String sql = "UPDATE users SET is_active=0 WHERE username=?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, user.getUsername());
                    stmt.executeUpdate();
                    loadUsers();
                    Alert info = new Alert(Alert.AlertType.INFORMATION, "Utilisateur désactivé avec succès!");
                    info.showAndWait();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Alert error = new Alert(Alert.AlertType.ERROR, "Erreur lors de la suppression.");
                    error.showAndWait();
                }
            }
        });
    }
}