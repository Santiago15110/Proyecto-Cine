package org.example.proyectocine.viewController;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.example.proyectocine.controller.AdminPromocionesController;
import org.example.proyectocine.model.CinemaException;
import org.example.proyectocine.model.Promocion;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AdminPromocionesViewController {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private TableView<Promocion> tblPromociones;
    @FXML private TableColumn<Promocion, String> colId;
    @FXML private TableColumn<Promocion, String> colNombre;
    @FXML private TableColumn<Promocion, String> colDescuento;
    @FXML private TableColumn<Promocion, String> colVigencia;
    @FXML private TableColumn<Promocion, String> colEstado;

    @FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPorcentaje;
    @FXML private DatePicker dpInicio;
    @FXML private DatePicker dpFin;
    @FXML private Label lblMensaje;

    private final AdminPromocionesController controller = new AdminPromocionesController();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().getId()));
        colNombre.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().getNombre()));
        colDescuento.setCellValueFactory(dato -> new SimpleStringProperty(
                String.format("%.0f %%", dato.getValue().getPorcentaje())));
        colVigencia.setCellValueFactory(dato -> new SimpleStringProperty(
                dato.getValue().getFechaInicio().format(FORMATO_FECHA) + " al "
                        + dato.getValue().getFechaFin().format(FORMATO_FECHA)));
        colEstado.setCellValueFactory(dato -> new SimpleStringProperty(
                dato.getValue().isActiva() ? "Activa" : "Inactiva"));

        dpInicio.setValue(LocalDate.now());
        dpFin.setValue(LocalDate.now().plusDays(15));
        cargarTabla();
    }

    private void cargarTabla() {
        Promocion seleccionada = tblPromociones.getSelectionModel().getSelectedItem();
        tblPromociones.getItems().setAll(controller.obtenerPromociones());
        tblPromociones.refresh();
        if (seleccionada != null) {
            tblPromociones.getSelectionModel().select(seleccionada);
        }
    }

    @FXML
    private void onCrearPromocion() {
        String id = txtId.getText().trim();
        String nombre = txtNombre.getText().trim();
        String porcTxt = txtPorcentaje.getText().trim();
        LocalDate inicio = dpInicio.getValue();
        LocalDate fin = dpFin.getValue();

        if (id.isEmpty() || nombre.isEmpty() || porcTxt.isEmpty() || inicio == null || fin == null) {
            mostrarMensaje("Completa todos los campos de la promoción.", true);
            return;
        }

        try {
            double porcentaje = Double.parseDouble(porcTxt);
            controller.crearPromocion(id, nombre, porcentaje, inicio, fin);

            txtId.clear();
            txtNombre.clear();
            txtPorcentaje.clear();
            cargarTabla();
            mostrarMensaje("Promoción \"" + nombre + "\" creada exitosamente.", false);
        } catch (NumberFormatException e) {
            mostrarMensaje("Ingresa un porcentaje numérico válido.", true);
        } catch (CinemaException | IllegalArgumentException e) {
            mostrarMensaje(e.getMessage(), true);
        }
    }

    @FXML
    private void onActivar() {
        Promocion seleccionada = tblPromociones.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarMensaje("Selecciona una promoción de la tabla.", true);
            return;
        }
        try {
            controller.activarPromocion(seleccionada.getId());
            cargarTabla();
            mostrarMensaje("Promoción activada.", false);
        } catch (CinemaException e) {
            mostrarMensaje(e.getMessage(), true);
        }
    }

    @FXML
    private void onDesactivar() {
        Promocion seleccionada = tblPromociones.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarMensaje("Selecciona una promoción de la tabla.", true);
            return;
        }
        try {
            controller.desactivarPromocion(seleccionada.getId());
            cargarTabla();
            mostrarMensaje("Promoción desactivada.", false);
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