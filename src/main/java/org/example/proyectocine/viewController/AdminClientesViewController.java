package org.example.proyectocine.viewController;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.example.proyectocine.controller.AdminClientesController;
import org.example.proyectocine.model.CinemaException;
import org.example.proyectocine.model.Cliente;
import org.example.proyectocine.model.SolicitudRecarga;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class AdminClientesViewController {

    private static final DecimalFormat FORMATO_PESOS =
            new DecimalFormat("$ #,##0", DecimalFormatSymbols.getInstance(Locale.of("es", "CO")));
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML private TableView<Cliente> tblClientes;
    @FXML private TableColumn<Cliente, String> colCedula;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colCorreo;
    @FXML private TableColumn<Cliente, String> colSaldo;
    @FXML private TableColumn<Cliente, String> colPuntos;
    @FXML private TableColumn<Cliente, String> colEstadoTarjeta;

    @FXML private Label lblClienteSeleccionado;
    @FXML private TextField txtMontoRecarga;
    @FXML private Label lblMensaje;

    @FXML private Label lblTituloSolicitudes;
    @FXML private TableView<SolicitudRecarga> tblSolicitudes;
    @FXML private TableColumn<SolicitudRecarga, String> colSolFecha;
    @FXML private TableColumn<SolicitudRecarga, String> colSolCliente;
    @FXML private TableColumn<SolicitudRecarga, String> colSolCedula;
    @FXML private TableColumn<SolicitudRecarga, String> colSolMonto;
    @FXML private TableColumn<SolicitudRecarga, String> colSolSaldo;

    private final AdminClientesController controller = new AdminClientesController();

    @FXML
    private void initialize() {
        colCedula.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().getCedula()));
        colNombre.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().getNombre()));
        colCorreo.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().getCorreo()));
        colSaldo.setCellValueFactory(dato -> new SimpleStringProperty(
                FORMATO_PESOS.format(dato.getValue().getTheTarjeta().getSaldo())));
        colPuntos.setCellValueFactory(dato -> new SimpleStringProperty(
                String.valueOf(dato.getValue().getTheAcumulacion().getPuntos())));
        colEstadoTarjeta.setCellValueFactory(dato -> new SimpleStringProperty(
                dato.getValue().getTheTarjeta().isActiva() ? "Activa" : "Bloqueada"));

        tblClientes.getSelectionModel().selectedItemProperty().addListener((obs, anterior, seleccionado) -> {
            if (seleccionado != null) {
                lblClienteSeleccionado.setText("Cliente: " + seleccionado.getNombre() + " (CC: " + seleccionado.getCedula() + ")");
                lblMensaje.setText("");
            } else {
                lblClienteSeleccionado.setText("Selecciona un cliente de la tabla");
            }
        });

        colSolFecha.setCellValueFactory(dato -> new SimpleStringProperty(
                dato.getValue().getFechaSolicitud().format(FORMATO_FECHA)));
        colSolCliente.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().getCliente().getNombre()));
        colSolCedula.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().getCliente().getCedula()));
        colSolMonto.setCellValueFactory(dato -> new SimpleStringProperty(FORMATO_PESOS.format(dato.getValue().getMonto())));
        colSolSaldo.setCellValueFactory(dato -> new SimpleStringProperty(
                FORMATO_PESOS.format(dato.getValue().getCliente().getTheTarjeta().getSaldo())));

        cargarTabla();
        cargarSolicitudes();
    }

    private void cargarSolicitudes() {
        ArrayList<SolicitudRecarga> pendientes = controller.obtenerSolicitudesPendientes();
        tblSolicitudes.getItems().setAll(pendientes);
        lblTituloSolicitudes.setText(pendientes.isEmpty()
                ? "Solicitudes de recarga pendientes"
                : "Solicitudes de recarga pendientes (" + pendientes.size() + ")");
    }

    @FXML
    private void onAprobarSolicitud() {
        SolicitudRecarga solicitud = tblSolicitudes.getSelectionModel().getSelectedItem();
        if (solicitud == null) {
            mostrarMensaje("Selecciona una solicitud de la lista.", true);
            return;
        }
        try {
            controller.aprobarSolicitud(solicitud);
            cargarTabla();
            cargarSolicitudes();
            mostrarMensaje("Solicitud aprobada: se recargaron " + FORMATO_PESOS.format(solicitud.getMonto())
                    + " a " + solicitud.getCliente().getNombre(), false);
        } catch (CinemaException | IllegalArgumentException e) {
            mostrarMensaje(e.getMessage(), true);
        }
    }

    @FXML
    private void onRechazarSolicitud() {
        SolicitudRecarga solicitud = tblSolicitudes.getSelectionModel().getSelectedItem();
        if (solicitud == null) {
            mostrarMensaje("Selecciona una solicitud de la lista.", true);
            return;
        }
        try {
            controller.rechazarSolicitud(solicitud);
            cargarSolicitudes();
            mostrarMensaje("Solicitud de " + solicitud.getCliente().getNombre() + " rechazada.", false);
        } catch (CinemaException e) {
            mostrarMensaje(e.getMessage(), true);
        }
    }

    private void cargarTabla() {
        Cliente seleccionadoActual = tblClientes.getSelectionModel().getSelectedItem();
        tblClientes.getItems().setAll(controller.obtenerClientes());
        tblClientes.refresh();
        if (seleccionadoActual != null) {
            tblClientes.getSelectionModel().select(seleccionadoActual);
        }
    }

    @FXML
    private void onRecargar() {
        Cliente cliente = tblClientes.getSelectionModel().getSelectedItem();
        if (cliente == null) {
            mostrarMensaje("Debes seleccionar un cliente de la tabla.", true);
            return;
        }
        try {
            double monto = Double.parseDouble(txtMontoRecarga.getText().trim());
            controller.recargarTarjeta(cliente.getCedula(), monto);
            txtMontoRecarga.clear();
            cargarTabla();
            mostrarMensaje("Recarga exitosa por " + FORMATO_PESOS.format(monto) + " a " + cliente.getNombre(), false);
        } catch (NumberFormatException e) {
            mostrarMensaje("Ingresa un monto numérico válido.", true);
        } catch (CinemaException | IllegalArgumentException e) {
            mostrarMensaje(e.getMessage(), true);
        }
    }

    @FXML
    private void onBloquearTarjeta() {
        Cliente cliente = tblClientes.getSelectionModel().getSelectedItem();
        if (cliente == null) {
            mostrarMensaje("Debes seleccionar un cliente de la tabla.", true);
            return;
        }
        try {
            controller.bloquearTarjeta(cliente.getCedula());
            cargarTabla();
            mostrarMensaje("Tarjeta bloqueada para " + cliente.getNombre(), false);
        } catch (CinemaException e) {
            mostrarMensaje(e.getMessage(), true);
        }
    }

    @FXML
    private void onActivarTarjeta() {
        Cliente cliente = tblClientes.getSelectionModel().getSelectedItem();
        if (cliente == null) {
            mostrarMensaje("Debes seleccionar un cliente de la tabla.", true);
            return;
        }
        try {
            controller.activarTarjeta(cliente.getCedula());
            cargarTabla();
            mostrarMensaje("Tarjeta activada para " + cliente.getNombre(), false);
        } catch (CinemaException e) {
            mostrarMensaje(e.getMessage(), true);
        }
    }

    @FXML
    private void onVolver() {
        SceneManager.cambiarA(Vista.PANEL_ADMIN);
    }

    private void mostrarMensaje(String texto, boolean esError) {
        lblMensaje.setText(texto);
        lblMensaje.setStyle(esError ? "-fx-text-fill: #c0392b;" : "-fx-text-fill: #27ae60;");
    }
}