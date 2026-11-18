package org.example.proyectocine.viewController;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.example.proyectocine.controller.AdminComprasController;
import org.example.proyectocine.model.Compra;
import org.example.proyectocine.model.EstadoCancelada;
import org.example.proyectocine.model.EstadoPagada;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

public class AdminComprasViewController {

    private static final DecimalFormat FORMATO_PESOS =
            new DecimalFormat("$ #,##0", DecimalFormatSymbols.getInstance(Locale.of("es", "CO")));

    @FXML private Label lblTotalCompras;
    @FXML private Label lblComprasPagadas;
    @FXML private Label lblComprasCanceladas;

    @FXML private TableView<Compra> tblCompras;
    @FXML private TableColumn<Compra, String> colId;
    @FXML private TableColumn<Compra, String> colCliente;
    @FXML private TableColumn<Compra, String> colDetalle;
    @FXML private TableColumn<Compra, String> colPago;
    @FXML private TableColumn<Compra, String> colEstado;

    private final AdminComprasController controller = new AdminComprasController();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().getId()));
        colCliente.setCellValueFactory(dato -> new SimpleStringProperty(
                dato.getValue().getCliente().getNombre() + " (" + dato.getValue().getCliente().getCedula() + ")"));
        colDetalle.setCellValueFactory(dato -> new SimpleStringProperty(resumirDetalle(dato.getValue())));
        colPago.setCellValueFactory(dato -> {
            Compra c = dato.getValue();
            String valor = c.isPagadaConPuntos()
                    ? c.getPuntosRedimidos() + " pts"
                    : FORMATO_PESOS.format(c.getTotal());
            return new SimpleStringProperty(valor);
        });
        colEstado.setCellValueFactory(dato -> new SimpleStringProperty(obtenerNombreEstado(dato.getValue())));

        cargarDatos();
    }

    private void cargarDatos() {
        ArrayList<Compra> compras = controller.obtenerCompras();
        tblCompras.getItems().setAll(compras);

        int pagadas = 0;
        int canceladas = 0;
        for (Compra c : compras) {
            if (c.getEstado() instanceof EstadoPagada) {
                pagadas++;
            } else if (c.getEstado() instanceof EstadoCancelada) {
                canceladas++;
            }
        }

        lblTotalCompras.setText(String.valueOf(compras.size()));
        lblComprasPagadas.setText(String.valueOf(pagadas));
        lblComprasCanceladas.setText(String.valueOf(canceladas));
    }

    private String resumirDetalle(Compra c) {
        StringBuilder sb = new StringBuilder();
        if (c.getFuncion() != null) {
            sb.append(c.getFuncion().getPelicula().getTitulo())
                    .append(" (").append(c.getEntradas().size()).append(" entr.)");
        }
        if (!c.getLineas().isEmpty()) {
            if (!sb.isEmpty()) {
                sb.append(" + ");
            }
            sb.append(c.getLineas().size()).append(" ítem(s) confitería");
        }
        return sb.isEmpty() ? "Sin detalle" : sb.toString();
    }

    private String obtenerNombreEstado(Compra c) {
        if (c.getEstado() instanceof EstadoPagada) {
            return "Pagada";
        }
        if (c.getEstado() instanceof EstadoCancelada) {
            return "Cancelada";
        }
        return "Pendiente";
    }

    @FXML
    private void onVolver() {
        SceneManager.cambiarA(Vista.PANEL_ADMIN);
    }
}