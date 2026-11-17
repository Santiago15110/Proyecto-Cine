package org.example.proyectocine.viewController;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import org.example.proyectocine.controller.TarjetaController;
import org.example.proyectocine.model.AcumulacionPunto;
import org.example.proyectocine.model.Cliente;
import org.example.proyectocine.model.MovimientoSaldo;
import org.example.proyectocine.model.RegistroPuntos;
import org.example.proyectocine.model.TarjetaVirtual;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TarjetaViewController {

    private static final DecimalFormat FORMATO_PESOS =
            new DecimalFormat("$ #,##0", DecimalFormatSymbols.getInstance(Locale.of("es", "CO")));
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    @FXML private Label lblSinSesion;
    @FXML private VBox contenido;

    @FXML private Label lblEstado;
    @FXML private Label lblNumero;
    @FXML private Label lblTitular;
    @FXML private Label lblSaldo;
    @FXML private Label lblPuntos;
    @FXML private Label lblVencimiento;
    @FXML private Label lblAviso;

    @FXML private TableView<MovimientoSaldo> tblMovimientos;
    @FXML private TableColumn<MovimientoSaldo, String> colFecha;
    @FXML private TableColumn<MovimientoSaldo, String> colHora;
    @FXML private TableColumn<MovimientoSaldo, String> colTipo;
    @FXML private TableColumn<MovimientoSaldo, String> colMonto;
    @FXML private TableColumn<MovimientoSaldo, String> colSaldo;
    @FXML private TableColumn<MovimientoSaldo, String> colDescripcion;

    @FXML private TableView<RegistroPuntos> tblPuntos;
    @FXML private TableColumn<RegistroPuntos, String> colPtsObtenidos;
    @FXML private TableColumn<RegistroPuntos, String> colPtsFecha;
    @FXML private TableColumn<RegistroPuntos, String> colPtsVence;
    @FXML private TableColumn<RegistroPuntos, String> colPtsDisponibles;
    @FXML private TableColumn<RegistroPuntos, String> colPtsEstado;
    @FXML private TableColumn<RegistroPuntos, String> colPtsOrigen;

    private final TarjetaController tarjetaController = new TarjetaController();

    private Cliente cliente;

    @FXML
    private void initialize() {
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().fecha().format(FORMATO_FECHA)));
        colHora.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().hora().format(FORMATO_HORA)));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().tipoMovimiento().toString()));
        colMonto.setCellValueFactory(c -> new SimpleStringProperty(FORMATO_PESOS.format(c.getValue().monto())));
        colSaldo.setCellValueFactory(c -> new SimpleStringProperty(FORMATO_PESOS.format(c.getValue().saldoResultante())));
        colDescripcion.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().descripcion()));

        colPtsObtenidos.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getPuntosObtenidos())));
        colPtsFecha.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaObtencion().format(FORMATO_FECHA)));
        colPtsVence.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFechaVencimiento().format(FORMATO_FECHA)));
        colPtsDisponibles.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getPuntosDisponibles())));
        colPtsEstado.setCellValueFactory(c -> new SimpleStringProperty(estadoPuntos(c.getValue())));
        colPtsOrigen.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getReferencia() == null || c.getValue().getReferencia().isBlank()
                        ? "-" : c.getValue().getReferencia()));

        mostrarCliente(tarjetaController.obtenerClienteEnSesion());
    }

    /**
     * Muestra la tarjeta de un cliente. Si es null, muestra el aviso de iniciar sesión.
     *
     * @param cliente cliente a mostrar
     */
    public void mostrarCliente(Cliente cliente) {
        this.cliente = cliente;
        boolean haySesion = cliente != null;
        lblSinSesion.setVisible(!haySesion);
        lblSinSesion.setManaged(!haySesion);
        contenido.setVisible(haySesion);
        contenido.setManaged(haySesion);
        if (haySesion) {
            actualizarDatos();
        }
    }

    @FXML
    private void onVolver() {
        Navegacion.irAlPanel();
    }

    private void actualizarDatos() {
        TarjetaVirtual tarjeta = tarjetaController.obtenerTarjeta(cliente);
        AcumulacionPunto puntos = tarjetaController.obtenerPuntos(cliente);

        lblNumero.setText("N° " + tarjeta.getNumero());
        lblTitular.setText(cliente.getNombre().toUpperCase());
        lblSaldo.setText(FORMATO_PESOS.format(tarjeta.getSaldo()));

        lblEstado.getStyleClass().removeAll("estado-activa", "estado-bloqueada");
        if (tarjeta.isActiva()) {
            lblEstado.setText("ACTIVA");
            lblEstado.getStyleClass().add("estado-activa");
            lblAviso.setText("¿Necesitas más saldo? Las recargas las realiza el administrador del cine.");
        } else {
            lblEstado.setText("BLOQUEADA");
            lblEstado.getStyleClass().add("estado-bloqueada");
            lblAviso.setText("Tu tarjeta está bloqueada: no puedes pagar compras con saldo. "
                    + "Comunícate con el administrador del cine.");
        }

        lblPuntos.setText(String.valueOf(puntos.getPuntos()));
        lblVencimiento.setText(puntos.getFechaVencimiento() == null
                ? "Sin puntos por vencer"
                : "Los próximos vencen el " + puntos.getFechaVencimiento().format(FORMATO_FECHA));

        tblMovimientos.getItems().setAll(tarjetaController.obtenerMovimientosRecientes(cliente));
        tblPuntos.getItems().setAll(tarjetaController.obtenerHistorialPuntos(cliente));
    }

    private String estadoPuntos(RegistroPuntos registro) {
        if (registro.estaVencido(LocalDate.now())) {
            return registro.getPuntosDisponibles() > 0 ? "Vencidos" : "Usados";
        }
        if (registro.getPuntosDisponibles() == 0) {
            return "Usados";
        }
        return registro.getPuntosDisponibles() < registro.getPuntosObtenidos() ? "Parcial" : "Vigentes";
    }
}
