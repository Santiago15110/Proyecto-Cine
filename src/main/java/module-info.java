module org.example.proyectocine {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.proyectocine to javafx.fxml;
    opens org.example.proyectocine.viewController to javafx.fxml;
    exports org.example.proyectocine;
    exports org.example.proyectocine.model;
    opens org.example.proyectocine.model to javafx.fxml;
}