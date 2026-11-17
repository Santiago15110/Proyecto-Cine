package org.example.proyectocine.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.proyectocine.controller.PanelAdminController;
import org.example.proyectocine.model.Admin;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class PanelAdminViewController {

    private static final DecimalFormat FORMATO_PESOS =
            new DecimalFormat("$ #,##0", DecimalFormatSymbols.getInstance(Locale.of("es", "CO")));

    @FXML private Label lblSaludo;
    @FXML private Label lblVentas;
    @FXML private Label lblClientes;
    @FXML private Label lblPeliculas;
    @FXML private Label lblFunciones;

    private final PanelAdminController panelController = new PanelAdminController();

    @FXML
    private void initialize() {
        Admin admin = panelController.obtenerAdminEnSesion();
        if (admin == null) {
            return;
        }
        lblSaludo.setText("Sesión iniciada como " + admin.getNombre());
        lblVentas.setText(FORMATO_PESOS.format(panelController.calcularVentasTotales()));
        lblClientes.setText(String.valueOf(panelController.contarClientes()));
        lblPeliculas.setText(String.valueOf(panelController.contarPeliculasEnCartelera()));
        lblFunciones.setText(panelController.contarFuncionesProgramadas() + " funciones programadas");
    }

    @FXML
    private void onReportes() {
        SceneManager.cambiarA(Vista.REPORTES);
    }

    @FXML
    private void onCerrarSesion() {
        panelController.cerrarSesion();
        SceneManager.cambiarA(Vista.BIENVENIDA);
    }
}
