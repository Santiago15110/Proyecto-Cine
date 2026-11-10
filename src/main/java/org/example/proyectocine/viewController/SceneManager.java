package org.example.proyectocine.viewController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Se encarga de cambiar entre las pantallas de la aplicación.
 * Todas las pantallas se muestran en la misma ventana (Stage).
 */
public final class SceneManager {

    private static final String RUTA_VISTAS = "/org/example/proyectocine/";

    private static Stage stage;

    private SceneManager() {
    }

    /**
     * Guarda la ventana principal. Se llama una sola vez, al arrancar la aplicación.
     *
     * @param stagePrincipal ventana principal que entrega JavaFX
     */
    public static void iniciar(Stage stagePrincipal) {
        stage = stagePrincipal;
    }

    /**
     * Cambia la ventana a la pantalla indicada.
     *
     * @param vista pantalla a mostrar
     * @param <T>   tipo del viewController de esa pantalla
     * @return el viewController de la pantalla, por si hay que pasarle datos
     * @throws IllegalStateException si no se llamó {@link #iniciar(Stage)} o si el FXML no se puede cargar
     */
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
