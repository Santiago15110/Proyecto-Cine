package org.example.proyectocine.viewController;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.example.proyectocine.controller.AdminPeliculasController;
import org.example.proyectocine.model.CinemaException;
import org.example.proyectocine.model.Genero;
import org.example.proyectocine.model.Pelicula;

public class AdminPeliculasViewController {

    @FXML private TableView<Pelicula> tblPeliculas;
    @FXML private TableColumn<Pelicula, String> colId;
    @FXML private TableColumn<Pelicula, String> colTitulo;
    @FXML private TableColumn<Pelicula, String> colGenero;
    @FXML private TableColumn<Pelicula, String> colClasificacion;
    @FXML private TableColumn<Pelicula, String> colDuracion;
    @FXML private TableColumn<Pelicula, String> colEstado;

    @FXML private TextField txtId;
    @FXML private TextField txtTitulo;
    @FXML private ComboBox<Genero> cmbGenero;
    @FXML private TextField txtClasificacion;
    @FXML private TextField txtDuracion;
    @FXML private TextField txtRutaPoster;
    @FXML private TextField txtDescripcion;
    @FXML private Label lblMensaje;

    private final AdminPeliculasController controller = new AdminPeliculasController();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().getId()));
        colTitulo.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().getTitulo()));
        colGenero.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().getGenero().name()));
        colClasificacion.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().getClasificacion()));
        colDuracion.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().getDuracionMin() + " min"));
        colEstado.setCellValueFactory(dato -> new SimpleStringProperty(
                dato.getValue().isEnCartelera() ? "En cartelera" : "Retirada"));

        cmbGenero.getItems().setAll(Genero.values());
        cargarTabla();
    }

    private void cargarTabla() {
        Pelicula seleccionada = tblPeliculas.getSelectionModel().getSelectedItem();
        tblPeliculas.getItems().setAll(controller.obtenerPeliculas());
        tblPeliculas.refresh();
        if (seleccionada != null) {
            tblPeliculas.getSelectionModel().select(seleccionada);
        }
    }

    @FXML
    private void onAgregarPelicula() {
        String id = txtId.getText().trim();
        String titulo = txtTitulo.getText().trim();
        Genero genero = cmbGenero.getValue();
        String clasificacion = txtClasificacion.getText().trim();
        String duracionTexto = txtDuracion.getText().trim();
        String rutaPoster = txtRutaPoster.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        if (id.isEmpty() || titulo.isEmpty() || genero == null || clasificacion.isEmpty() || duracionTexto.isEmpty()) {
            mostrarMensaje("Completa ID, título, género, clasificación y duración.", true);
            return;
        }

        try {
            int duracion = Integer.parseInt(duracionTexto);
            if (duracion <= 0) {
                mostrarMensaje("La duración debe ser mayor a 0 minutos.", true);
                return;
            }

            controller.agregarPelicula(id, titulo, genero, clasificacion, duracion, descripcion, rutaPoster);
            limpiarFormulario();
            cargarTabla();
            mostrarMensaje("Película \"" + titulo + "\" agregada a la cartelera.", false);
        } catch (NumberFormatException e) {
            mostrarMensaje("La duración debe ser un número entero en minutos.", true);
        } catch (CinemaException | IllegalArgumentException e) {
            mostrarMensaje(e.getMessage(), true);
        }
    }

    @FXML
    private void onRetirarCartelera() {
        Pelicula seleccionada = tblPeliculas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarMensaje("Selecciona una película de la tabla.", true);
            return;
        }
        try {
            controller.retirarDeCartelera(seleccionada.getId());
            cargarTabla();
            mostrarMensaje("Película retirada de cartelera.", false);
        } catch (CinemaException e) {
            mostrarMensaje(e.getMessage(), true);
        }
    }

    @FXML
    private void onPonerCartelera() {
        Pelicula seleccionada = tblPeliculas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarMensaje("Selecciona una película de la tabla.", true);
            return;
        }
        try {
            controller.ponerEnCartelera(seleccionada.getId());
            cargarTabla();
            mostrarMensaje("Película puesta en cartelera nuevamente.", false);
        } catch (CinemaException e) {
            mostrarMensaje(e.getMessage(), true);
        }
    }

    private void limpiarFormulario() {
        txtId.clear();
        txtTitulo.clear();
        cmbGenero.setValue(null);
        txtClasificacion.clear();
        txtDuracion.clear();
        txtRutaPoster.clear();
        txtDescripcion.clear();
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