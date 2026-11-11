package org.example.proyectocine.model;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.proyectocine.viewController.SceneManager;
import org.example.proyectocine.viewController.Vista;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {
        SceneManager.iniciar(stage);
        SceneManager.cambiarA(Vista.INICIO);
        stage.show();
    }
}
