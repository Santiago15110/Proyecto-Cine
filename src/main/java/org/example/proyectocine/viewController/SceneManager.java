package org.example.proyectocine.viewController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public final class SceneManager {

    private static final String RUTA_VISTAS = "/org/example/proyectocine/";

    private static Stage stage;

    private SceneManager() {
    }

    public static void iniciar(Stage stagePrincipal) {
        stage = stagePrincipal;
    }

    public static <T> T cambiarA(Vista vista) {
        if (stage == null) {
            throw new IllegalStateException("SceneManager no está iniciado: llama a SceneManager.iniciar(stage) al arrancar");
        }
        URL ruta = SceneManager.class.getResource(RUTA_VISTAS + vista.getArchivo());
        if (ruta == null) {
            throw new IllegalStateException("No se encontró el archivo " + vista.getArchivo());
        }
        try {
            FXMLLoader loader = new FXMLLoader(ruta);
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle(vista.getTitulo());
            stage.sizeToScene();
            stage.centerOnScreen();
            return loader.getController();
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo cargar la pantalla " + vista.getArchivo(), e);
        }
    }
}
