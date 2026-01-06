module MiniProjet {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens controllers to javafx.fxml;
    opens models to javafx.base;
    exports application;
}