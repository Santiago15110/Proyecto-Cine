package org.example.proyectocine.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.example.proyectocine.controller.PanelClienteController;
import org.example.proyectocine.model.Cliente;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

public class PanelClienteViewController {

    private static final DecimalFormat FORMATO_PESOS =
            new DecimalFormat("$ #,##0", DecimalFormatSymbols.getInstance(Locale.of("es", "CO")));

    @FXML private Label lblSaludo;
    @FXML private Label lblSaldo;
    @FXML private Label lblEstadoTarjeta;
    @FXML private Label lblPuntos;
    @FXML private Label lblCompras;
    @FXML private Label lblTituloNotificaciones;
    @FXML private ListView<String> lstNotificaciones;

    private final PanelClienteController panelController = new PanelClienteController();

    @FXML
    private void initialize() {
        Cliente cliente = panelController.obtenerClienteEnSesion();
        if (cliente == null) {
            return;
        }
        lblSaludo.setText("Hola, " + primerNombre(cliente.getNombre()));
        lblSaldo.setText(FORMATO_PESOS.format(panelController.consultarSaldo(cliente)));
        lblEstadoTarjeta.setText(cliente.getTheTarjeta().isActiva() ? "Tarjeta activa" : "Tarjeta bloqueada");
        lblPuntos.setText(String.valueOf(panelController.consultarPuntos(cliente)));
        lblCompras.setText(String.valueOf(panelController.contarCompras(cliente)));

        ArrayList<String> notificaciones = panelController.obtenerNotificaciones(cliente);
        lstNotificaciones.getItems().setAll(notificaciones);
        lblTituloNotificaciones.setText(notificaciones.isEmpty()
                ? "Notificaciones" : "Notificaciones (" + notificaciones.size() + ")");
    }

    @FXML
    private void onCartelera() {
        SceneManager.cambiarA(Vista.CARTELERA);
    }

    @FXML
    private void onMiTarjeta() {
        SceneManager.cambiarA(Vista.TARJETA);
    }

    @FXML
    private void onCerrarSesion() {
        panelController.cerrarSesion();
        SceneManager.cambiarA(Vista.BIENVENIDA);
    }

    private static String primerNombre(String nombre) {
        String limpio = nombre.trim();
        int espacio = limpio.indexOf(' ');
        return espacio > 0 ? limpio.substring(0, espacio) : limpio;
    }
}
