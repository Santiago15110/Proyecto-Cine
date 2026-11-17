package org.example.proyectocine;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.proyectocine.model.DatosIniciales;
import org.example.proyectocine.viewController.SceneManager;
import org.example.proyectocine.viewController.Vista;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {
        DatosIniciales.cargar();
        SceneManager.iniciar(stage);
        SceneManager.cambiarA(Vista.BIENVENIDA);
        stage.show();
    }
}
