package org.example.proyectocine.viewController;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.example.proyectocine.controller.AdminFuncionesController;
import org.example.proyectocine.model.CinemaException;
import org.example.proyectocine.model.Funcion;
import org.example.proyectocine.model.Pelicula;
import org.example.proyectocine.model.Sala;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class AdminFuncionesViewController {

    private static final DecimalFormat FORMATO_PESOS =
            new DecimalFormat("$ #,##0", DecimalFormatSymbols.getInstance(Locale.of("es", "CO")));
    private static final DateTimeFormatter FORMATO_FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML private TableView<Funcion> tblFunciones;
    @FXML private TableColumn<Funcion, String> colId;
    @FXML private TableColumn<Funcion, String> colPelicula;
    @FXML private TableColumn<Funcion, String> colSala;
    @FXML private TableColumn<Funcion, String> colFechaHora;
    @FXML private TableColumn<Funcion, String> colPrecio;
    @FXML private TableColumn<Funcion, String> colOcupacion;
    @FXML private TableColumn<Funcion, String> colEstado;

    @FXML private TextField txtIdFuncion;
    @FXML private ComboBox<Pelicula> cmbPelicula;
    @FXML private ComboBox<Sala> cmbSala;
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtHora;
    @FXML private TextField txtPrecio;
    @FXML private Label lblMensaje;

    private final AdminFuncionesController controller = new AdminFuncionesController();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().getId()));
        colPelicula.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().getPelicula().getTitulo()));
        colSala.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().getSala().getNombre()));
        colFechaHora.setCellValueFactory(dato -> new SimpleStringProperty(
                dato.getValue().getFechaHora().format(FORMATO_FECHA_HORA)));
        colPrecio.setCellValueFactory(dato -> new SimpleStringProperty(
                FORMATO_PESOS.format(dato.getValue().getPrecioBase())));
        colOcupacion.setCellValueFactory(dato -> new SimpleStringProperty(
                String.format("%.0f %%", dato.getValue().getOcupacion())));
        colEstado.setCellValueFactory(dato -> new SimpleStringProperty(
                dato.getValue().isCancelada() ? "Cancelada" : dato.getValue().getEstado().name()));

        configurarCombos();
        dpFecha.setValue(LocalDate.now().plusDays(1));
        cargarTabla();
    }

    private void configurarCombos() {
        cmbPelicula.getItems().setAll(controller.obtenerPeliculas());
        cmbPelicula.setConverter(new StringConverter<>() {
            @Override
            public String toString(Pelicula p) {
                return p == null ? "" : p.getTitulo() + " (" + p.getId() + ")";
            }

            @Override
            public Pelicula fromString(String s) {
                return null;
            }
        });

        cmbSala.getItems().setAll(controller.obtenerSalas());
        cmbSala.setConverter(new StringConverter<>() {
            @Override
            public String toString(Sala s) {
                return s == null ? "" : s.getNombre() + " (" + s.getCapacidad() + " sillas)";
            }

            @Override
            public Sala fromString(String s) {
                return null;
            }
        });
    }

    private void cargarTabla() {
        tblFunciones.getItems().setAll(controller.obtenerFunciones());
        tblFunciones.refresh();
    }

    @FXML
    private void onProgramarFuncion() {
        String id = txtIdFuncion.getText().trim();
        Pelicula pelicula = cmbPelicula.getValue();
        Sala sala = cmbSala.getValue();
        LocalDate fecha = dpFecha.getValue();
        String horaTexto = txtHora.getText().trim();
        String precioTexto = txtPrecio.getText().trim();

        if (id.isEmpty() || pelicula == null || sala == null || fecha == null || horaTexto.isEmpty() || precioTexto.isEmpty()) {
            mostrarMensaje("Completa todos los campos para programar la función.", true);
            return;
        }

        try {
            LocalTime hora = LocalTime.parse(horaTexto, DateTimeFormatter.ofPattern("HH:mm"));
            double precio = Double.parseDouble(precioTexto);
            if (precio <= 0) {
                mostrarMensaje("El precio base debe ser mayor a 0.", true);
                return;
            }

            LocalDateTime fechaHora = LocalDateTime.of(fecha, hora);
            controller.programarFuncion(id, pelicula.getId(), sala.getId(), fechaHora, precio);

            txtIdFuncion.clear();
            txtHora.clear();
            txtPrecio.clear();
            cargarTabla();
            mostrarMensaje("Función " + id + " programada exitosamente.", false);
        } catch (DateTimeParseException e) {
            mostrarMensaje("Formato de hora inválido. Usa HH:mm (ejemplo: 18:30).", true);
        } catch (NumberFormatException e) {
            mostrarMensaje("Ingresa un precio numérico válido.", true);
        } catch (CinemaException | IllegalArgumentException e) {
            mostrarMensaje(e.getMessage(), true);
        }
    }

    @FXML
    private void onCancelarFuncion() {
        Funcion seleccionada = tblFunciones.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarMensaje("Selecciona una función de la tabla para cancelar.", true);
            return;
        }

        try {
            controller.cancelarFuncion(seleccionada.getId());
            cargarTabla();
            mostrarMensaje("Función " + seleccionada.getId() + " cancelada. Se reembolsó y notificó a los clientes.", false);
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