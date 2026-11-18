package org.example.proyectocine.viewController;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.proyectocine.controller.MisComprasController;
import org.example.proyectocine.model.CinemaException;
import org.example.proyectocine.model.Cliente;
import org.example.proyectocine.model.Compra;
import org.example.proyectocine.model.EstadoPagada;
import org.example.proyectocine.model.Funcion;
import org.example.proyectocine.model.LineaProducto;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Pantalla "Mis compras": historial del cliente y cancelación según la política de reembolso.
 */
public class MisComprasViewController {

    private static final Locale COLOMBIA = Locale.of("es", "CO");
    private static final DecimalFormat FORMATO_PESOS =
            new DecimalFormat("$ #,##0", DecimalFormatSymbols.getInstance(COLOMBIA));
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FORMATO_FUNCION =
            DateTimeFormatter.ofPattern("EEE d 'de' MMM, h:mm a", COLOMBIA);

    @FXML private Label lblSinSesion;
    @FXML private HBox contenido;

    @FXML private TableView<Compra> tblCompras;
    @FXML private TableColumn<Compra, String> colCodigo;
    @FXML private TableColumn<Compra, String> colFecha;
    @FXML private TableColumn<Compra, String> colPelicula;
    @FXML private TableColumn<Compra, String> colTotal;
    @FXML private TableColumn<Compra, String> colEstado;

    @FXML private Label lblSinSeleccion;
    @FXML private VBox panelDetalle;
    @FXML private Label lblDetCodigo;
    @FXML private Label lblDetEstado;
    @FXML private Label lblDetPelicula;
    @FXML private Label lblDetFuncion;
    @FXML private Label lblDetAsientos;
    @FXML private Label lblDetProductos;
    @FXML private Label lblDetPago;
    @FXML private Label lblReembolso;
    @FXML private Button btnCancelar;
    @FXML private Label lblMensaje;
    @FXML private Label lblResumen;

    private final MisComprasController comprasController = new MisComprasController();

    private Cliente cliente;

    @FXML
    private void initialize() {
        colCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFecha().format(FORMATO_FECHA)));
        colPelicula.setCellValueFactory(c -> new SimpleStringProperty(textoPelicula(c.getValue())));
        colTotal.setCellValueFactory(c -> new SimpleStringProperty(textoPago(c.getValue())));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstado().getNombre()));

        tblCompras.getSelectionModel().selectedItemProperty().addListener((obs, anterior, nueva) -> {
            lblMensaje.setText("");
            mostrarDetalle(nueva);
        });

        cliente = comprasController.obtenerClienteEnSesion();
        boolean haySesion = cliente != null;
        lblSinSesion.setVisible(!haySesion);
        lblSinSesion.setManaged(!haySesion);
        contenido.setVisible(haySesion);
        contenido.setManaged(haySesion);

        mostrarDetalle(null);
        if (haySesion) {
            refrescar(null);
        }
    }

    // ===================== TABLA Y DETALLE =====================

    /**
     * Recarga la tabla y deja seleccionada la compra indicada (si sigue en la lista).
     */
    private void refrescar(Compra seleccionar) {
        tblCompras.getItems().setAll(comprasController.obtenerCompras(cliente));
        if (seleccionar != null) {
            tblCompras.getSelectionModel().select(seleccionar);
        }
        lblResumen.setText("Tu saldo: " + FORMATO_PESOS.format(comprasController.consultarSaldo(cliente))
                + "   ·   Tus puntos: " + comprasController.consultarPuntos(cliente));
    }

    private void mostrarDetalle(Compra compra) {
        boolean hayCompra = compra != null;
        panelDetalle.setVisible(hayCompra);
        panelDetalle.setManaged(hayCompra);
        lblSinSeleccion.setVisible(!hayCompra);
        lblSinSeleccion.setManaged(!hayCompra);
        if (!hayCompra) {
            return;
        }

        lblDetCodigo.setText("Compra " + compra.getId());
        lblDetEstado.setText(compra.getEstado().getNombre() + " · " + compra.getFecha().format(FORMATO_FECHA));

        Funcion funcion = compra.getFuncion();
        lblDetPelicula.setText(textoPelicula(compra));
        lblDetFuncion.setText(funcion == null ? "-"
                : capitalizar(funcion.getFechaHora().format(FORMATO_FUNCION)) + " · " + funcion.getSala().getNombre());
        lblDetAsientos.setText(compra.getEntradas().isEmpty() ? "-"
                : compra.getEntradas().stream().map(e -> e.getAsiento().getCodigo()).collect(Collectors.joining(", ")));
        lblDetProductos.setText(compra.getLineas().isEmpty() ? "-"
                : compra.getLineas().stream().map(LineaProducto::toString).collect(Collectors.joining(", ")));
        lblDetPago.setText(compra.isPagadaConPuntos()
                ? compra.getPuntosRedimidos() + " puntos"
                : FORMATO_PESOS.format(compra.getTotal()) + " con tu tarjeta");

        mostrarReembolso(compra);
    }

    /** Muestra cuánto se devolvería y activa el botón solo si se puede cancelar. */
    private void mostrarReembolso(Compra compra) {
        if (!(compra.getEstado() instanceof EstadoPagada)) {
            lblReembolso.setText("Esta compra está cancelada.");
            btnCancelar.setDisable(true);
            return;
        }
        try {
            double porcentaje = comprasController.consultarPorcentajeReembolso(compra);
            lblReembolso.setText("Si cancelas ahora se te devuelve el " + (int) porcentaje + " %: "
                    + textoReembolso(compra, porcentaje) + ".");
            btnCancelar.setDisable(false);
        } catch (CinemaException e) {
            lblReembolso.setText(e.getMessage() + ".");
            btnCancelar.setDisable(true);
        }
    }

    // ===================== BOTONES =====================

    @FXML
    private void onCancelarCompra() {
        Compra compra = tblCompras.getSelectionModel().getSelectedItem();
        if (compra == null) {
            return;
        }
        double porcentaje;
        try {
            porcentaje = comprasController.consultarPorcentajeReembolso(compra);
        } catch (CinemaException e) {
            mostrarMensaje(e.getMessage(), true);
            mostrarReembolso(compra);
            return;
        }

        String reembolso = textoReembolso(compra, porcentaje);
        if (!confirmar(compra, porcentaje, reembolso)) {
            return;
        }
        try {
            comprasController.cancelarCompra(cliente, compra);
            refrescar(compra);
            mostrarMensaje("Compra cancelada. Se te devolvieron " + reembolso + ".", false);
        } catch (CinemaException e) {
            mostrarMensaje(e.getMessage(), true);
        }
    }

    @FXML
    private void onVolver() {
        Navegacion.irAlPanel();
    }

    private boolean confirmar(Compra compra, double porcentaje, String reembolso) {
        ButtonType si = new ButtonType("Sí, cancelar", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, "", si, no);
        alerta.setTitle("Cancelar compra");
        alerta.setHeaderText("¿Seguro que quieres cancelar la compra " + compra.getId() + "?");
        alerta.setContentText("Se te devolverá el " + (int) porcentaje + " %: " + reembolso
                + ".\nEsta acción no se puede deshacer.");
        if (tblCompras.getScene() != null) {
            alerta.initOwner(tblCompras.getScene().getWindow());
        }
        Optional<ButtonType> respuesta = alerta.showAndWait();
        return respuesta.isPresent() && respuesta.get() == si;
    }

    // ===================== AUXILIARES =====================

    private String textoReembolso(Compra compra, double porcentaje) {
        if (compra.isPagadaConPuntos()) {
            return comprasController.calcularReembolsoPuntos(compra, porcentaje) + " puntos";
        }
        return FORMATO_PESOS.format(comprasController.calcularReembolsoDinero(compra, porcentaje));
    }

    private static String textoPelicula(Compra compra) {
        Funcion funcion = compra.getFuncion();
        return funcion == null ? "Solo confitería" : funcion.getPelicula().getTitulo();
    }

    private static String textoPago(Compra compra) {
        return compra.isPagadaConPuntos()
                ? compra.getPuntosRedimidos() + " pts"
                : FORMATO_PESOS.format(compra.getTotal());
    }

    private void mostrarMensaje(String texto, boolean esError) {
        lblMensaje.getStyleClass().removeAll("mensaje-error", "mensaje-exito");
        lblMensaje.getStyleClass().add(esError ? "mensaje-error" : "mensaje-exito");
        lblMensaje.setText(texto);
    }

    private static String capitalizar(String texto) {
        return texto.isEmpty() ? texto : Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }
}
