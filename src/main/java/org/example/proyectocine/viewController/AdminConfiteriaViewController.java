package org.example.proyectocine.viewController;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.example.proyectocine.controller.AdminConfiteriaController;
import org.example.proyectocine.model.CinemaException;
import org.example.proyectocine.model.Combo;
import org.example.proyectocine.model.ItemConfiteria;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

public class AdminConfiteriaViewController {

    private static final DecimalFormat FORMATO_PESOS =
            new DecimalFormat("$ #,##0", DecimalFormatSymbols.getInstance(Locale.of("es", "CO")));

    @FXML private TableView<ItemConfiteria> tblConfiteria;
    @FXML private TableColumn<ItemConfiteria, String> colId;
    @FXML private TableColumn<ItemConfiteria, String> colNombre;
    @FXML private TableColumn<ItemConfiteria, String> colTipo;
    @FXML private TableColumn<ItemConfiteria, String> colPrecio;
    @FXML private TableColumn<ItemConfiteria, String> colPuntos;

    @FXML private TextField txtIdProd;
    @FXML private TextField txtNombreProd;
    @FXML private TextField txtPrecioProd;
    @FXML private TextField txtPuntosProd;

    @FXML private TextField txtIdCombo;
    @FXML private TextField txtNombreCombo;
    @FXML private TextField txtDescuentoCombo;
    @FXML private ComboBox<ItemConfiteria> cmbItemAgregar;
    @FXML private ListView<String> lstItemsCombo;

    @FXML private Label lblMensaje;

    private final AdminConfiteriaController controller = new AdminConfiteriaController();
    private final ArrayList<String> idsSeleccionadosCombo = new ArrayList<>();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().getId()));
        colNombre.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().getNombre()));
        colTipo.setCellValueFactory(dato -> new SimpleStringProperty(
                dato.getValue() instanceof Combo ? "Combo" : "Producto"));
        colPrecio.setCellValueFactory(dato -> new SimpleStringProperty(
                FORMATO_PESOS.format(dato.getValue().getPrecio())));
        colPuntos.setCellValueFactory(dato -> new SimpleStringProperty(
                dato.getValue().getCostoPuntos() + " pts"));

        cmbItemAgregar.setConverter(new StringConverter<>() {
            @Override
            public String toString(ItemConfiteria item) {
                return item == null ? "" : item.getNombre() + " (" + item.getId() + ")";
            }

            @Override
            public ItemConfiteria fromString(String s) {
                return null;
            }
        });

        cargarDatos();
    }

    private void cargarDatos() {
        ArrayList<ItemConfiteria> lista = controller.obtenerConfiteria();
        tblConfiteria.getItems().setAll(lista);
        tblConfiteria.refresh();
        cmbItemAgregar.getItems().setAll(lista);
    }

    @FXML
    private void onCrearProducto() {
        String id = txtIdProd.getText().trim();
        String nombre = txtNombreProd.getText().trim();
        String precioTxt = txtPrecioProd.getText().trim();
        String puntosTxt = txtPuntosProd.getText().trim();

        if (id.isEmpty() || nombre.isEmpty() || precioTxt.isEmpty() || puntosTxt.isEmpty()) {
            mostrarMensaje("Completa todos los campos del producto.", true);
            return;
        }

        try {
            double precio = Double.parseDouble(precioTxt);
            int puntos = Integer.parseInt(puntosTxt);
            controller.crearProducto(id, nombre, precio, puntos);

            txtIdProd.clear();
            txtNombreProd.clear();
            txtPrecioProd.clear();
            txtPuntosProd.clear();
            cargarDatos();
            mostrarMensaje("Producto \"" + nombre + "\" creado exitosamente.", false);
        } catch (NumberFormatException e) {
            mostrarMensaje("Precio y puntos deben ser valores numéricos válidos.", true);
        } catch (CinemaException | IllegalArgumentException e) {
            mostrarMensaje(e.getMessage(), true);
        }
    }

    @FXML
    private void onAgregarItemACombo() {
        ItemConfiteria item = cmbItemAgregar.getValue();
        if (item == null) {
            mostrarMensaje("Selecciona un producto o combo para añadir.", true);
            return;
        }
        idsSeleccionadosCombo.add(item.getId());
        lstItemsCombo.getItems().add(item.getNombre() + " (" + item.getId() + ")");
        lblMensaje.setText("");
    }

    @FXML
    private void onLimpiarListaCombo() {
        idsSeleccionadosCombo.clear();
        lstItemsCombo.getItems().clear();
    }

    @FXML
    private void onCrearCombo() {
        String id = txtIdCombo.getText().trim();
        String nombre = txtNombreCombo.getText().trim();
        String descTxt = txtDescuentoCombo.getText().trim();

        if (id.isEmpty() || nombre.isEmpty() || descTxt.isEmpty()) {
            mostrarMensaje("Completa ID, nombre y descuento del combo.", true);
            return;
        }

        try {
            double descuento = Double.parseDouble(descTxt);
            controller.crearCombo(id, nombre, descuento, new ArrayList<>(idsSeleccionadosCombo));

            txtIdCombo.clear();
            txtNombreCombo.clear();
            txtDescuentoCombo.clear();
            onLimpiarListaCombo();
            cargarDatos();
            mostrarMensaje("Combo \"" + nombre + "\" creado exitosamente.", false);
        } catch (NumberFormatException e) {
            mostrarMensaje("El descuento debe ser un número válido (ej: 15).", true);
        } catch (CinemaException | IllegalArgumentException e) {
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