package org.example.proyectocine.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PaginaInicioViewController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void onRegistroClick() {
        SceneManager.cambiarA(Vista.REGISTRO);
    }

    @FXML
    protected void onTarjetaClick() {
        SceneManager.cambiarA(Vista.TARJETA);
    }

    @FXML
    protected void onReportesClick() {
        SceneManager.cambiarA(Vista.REPORTES);
    }
}
