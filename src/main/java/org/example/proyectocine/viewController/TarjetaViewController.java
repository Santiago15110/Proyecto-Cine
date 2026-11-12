package org.example.proyectocine.viewController;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.example.proyectocine.controller.TarjetaController;
import org.example.proyectocine.model.AcumulacionPunto;
import org.example.proyectocine.model.CinemaException;
import org.example.proyectocine.model.Cliente;
import org.example.proyectocine.model.MovimientoSaldo;
import org.example.proyectocine.model.TarjetaVirtual;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class TarjetaViewController {

    private static final DecimalFormat FORMATO_PESOS =
            new DecimalFormat("$ #,##0", DecimalFormatSymbols.getInstance(Locale.of("es", "CO")));
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    @FXML private TextField txtCedula;
    @FXML private Label lblBusqueda;
    @FXML private VBox contenido;

    @FXML private Label lblNumero;
    @FXML private Label lblTitular;
    @FXML private Label lblSaldo;
    @FXML private Label lblPuntos;
    @FXML private Label lblVencimiento;

    @FXML private TextField txtMonto;
    @FXML private Label lblMensaje;

    @FXML private TableView<MovimientoSaldo> tblMovimientos;
    @FXML private TableColumn<MovimientoSaldo, String> colFecha;
    @FXML private TableColumn<MovimientoSaldo, String> colHora;
    @FXML private TableColumn<MovimientoSaldo, String> colTipo;
    @FXML private TableColumn<MovimientoSaldo, String> colMonto;
    @FXML private TableColumn<MovimientoSaldo, String> colSaldo;
    @FXML private TableColumn<MovimientoSaldo, String> colDescripcion;

    private final TarjetaController tarjetaController = new TarjetaController();

    /** Cliente cuya tarjeta se está mostrando. */
    private Cliente cliente;

    @FXML
    private void initialize() {
        // Como MovimientoSaldo es un record, sus "getters" no llevan get: fecha(), monto(), ...
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().fecha().format(FORMATO_FECHA)));
        colHora.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().hora().format(FORMATO_HORA)));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().tipoMovimiento().toString()));
        colMonto.setCellValueFactory(c -> new SimpleStringProperty(FORMATO_PESOS.format(c.getValue().monto())));
        colSaldo.setCellValueFactory(c -> new SimpleStringProperty(FORMATO_PESOS.format(c.getValue().saldoResultante())));
        colDescripcion.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().descripcion()));
    }

    /**
     * Muestra la tarjeta de un cliente. Cuando exista el login, la pantalla anterior
     * llamará a este método con el cliente que inició sesión.
     *
     * @param cliente cliente a mostrar
     */
    public void mostrarCliente(Cliente cliente) {
        this.cliente = cliente;
        contenido.setDisable(false);
        lblMensaje.setText("");
        actualizarDatos();
    }

    @FXML
    private void onConsultar() {
        String cedula = txtCedula.getText().trim();
        if (cedula.isEmpty()) {
            lblBusqueda.setText("Ingresa la cédula del cliente.");
            return;
        }
        try {
            mostrarCliente(tarjetaController.buscarCliente(cedula));
            lblBusqueda.setText("");
        } catch (CinemaException | IllegalArgumentException e) {
            cliente = null;
            contenido.setDisable(true);
            lblBusqueda.setText(e.getMessage());
        }
    }

    @FXML
    private void onRecargar() {
        if (cliente == null) {
            return;
        }
        String textoMonto = txtMonto.getText().trim().replace(".", "");
        if (!textoMonto.matches("\\d+")) {
            mostrarError("Escribe un monto válido, solo números.");
            return;
        }
        try {
            double monto = Double.parseDouble(textoMonto);
            tarjetaController.recargar(cliente, monto);
            mostrarExito("Recarga de " + FORMATO_PESOS.format(monto) + " realizada correctamente.");
            txtMonto.clear();
            actualizarDatos();
        } catch (CinemaException | IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void onVolver() {
        SceneManager.cambiarA(Vista.INICIO);
    }

    private void actualizarDatos() {
        TarjetaVirtual tarjeta = tarjetaController.obtenerTarjeta(cliente);
        AcumulacionPunto puntos = tarjetaController.obtenerPuntos(cliente);

        lblNumero.setText("N° " + tarjeta.getNumero());
        lblTitular.setText(cliente.getNombre().toUpperCase());
        lblSaldo.setText(FORMATO_PESOS.format(tarjeta.getSaldo()));
        lblPuntos.setText(String.valueOf(puntos.getPuntos()));
        lblVencimiento.setText("Vencen el " + puntos.getFechaVencimiento().format(FORMATO_FECHA));

        tblMovimientos.getItems().setAll(tarjetaController.obtenerMovimientosRecientes(cliente));
    }

    private void mostrarError(String mensaje) {
        lblMensaje.getStyleClass().removeAll("mensaje-error", "mensaje-exito");
        lblMensaje.getStyleClass().add("mensaje-error");
        lblMensaje.setText(mensaje);
    }

    private void mostrarExito(String mensaje) {
        lblMensaje.getStyleClass().removeAll("mensaje-error", "mensaje-exito");
        lblMensaje.getStyleClass().add("mensaje-exito");
        lblMensaje.setText(mensaje);
    }
}
