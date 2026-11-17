package org.example.proyectocine.viewController;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.example.proyectocine.controller.AdminSalasController;
import org.example.proyectocine.model.Asiento;
import org.example.proyectocine.model.CinemaException;
import org.example.proyectocine.model.Sala;

public class AdminSalasViewController {

    @FXML private TableView<Sala> tblSalas;
    @FXML private TableColumn<Sala, String> colId;
    @FXML private TableColumn<Sala, String> colNombre;
    @FXML private TableColumn<Sala, String> colDimensiones;
    @FXML private TableColumn<Sala, String> colCapacidad;

    @FXML private Label lblSalaMapa;
    @FXML private GridPane gridAsientos;

    @FXML private TextField txtIdSala;
    @FXML private TextField txtNombreSala;
    @FXML private TextField txtFilas;
    @FXML private TextField txtColumnas;
    @FXML private Label lblMensaje;

    private final AdminSalasController controller = new AdminSalasController();

    @FXML
    private void initialize() {
        colId.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().getId()));
        colNombre.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().getNombre()));
        colDimensiones.setCellValueFactory(dato -> new SimpleStringProperty(
                dato.getValue().getFilas() + " filas × " + dato.getValue().getColumnas() + " cols"));
        colCapacidad.setCellValueFactory(dato -> new SimpleStringProperty(
                dato.getValue().getCapacidad() + " asientos"));

        tblSalas.getSelectionModel().selectedItemProperty().addListener((obs, anterior, sala) -> {
            if (sala != null) {
                lblSalaMapa.setText("Mapa de asientos - " + sala.getNombre() + " (clic en un asiento para habilitar/deshabilitar)");
                dibujarMapaAsientos(sala);
            } else {
                lblSalaMapa.setText("Selecciona una sala para ver su mapa de asientos");
                gridAsientos.getChildren().clear();
            }
        });

        cargarTabla();
        if (!tblSalas.getItems().isEmpty()) {
            tblSalas.getSelectionModel().selectFirst();
        }
    }

    private void cargarTabla() {
        Sala seleccionada = tblSalas.getSelectionModel().getSelectedItem();
        tblSalas.getItems().setAll(controller.obtenerSalas());
        tblSalas.refresh();
        if (seleccionada != null) {
            tblSalas.getSelectionModel().select(seleccionada);
            dibujarMapaAsientos(seleccionada);
        }
    }

    private void dibujarMapaAsientos(Sala sala) {
        gridAsientos.getChildren().clear();
        gridAsientos.setAlignment(Pos.CENTER);

        for (Asiento asiento : sala.getAsientos()) {
            int filaIdx = asiento.getFila() - 'A';
            int colIdx = asiento.getNumero() - 1;

            Button btnAsiento = new Button(asiento.getCodigo());
            btnAsiento.setPrefWidth(48);
            btnAsiento.setPrefHeight(32);

            if (asiento.isHabilitado()) {
                btnAsiento.setStyle("-fx-background-color: #e8f8f5; -fx-border-color: #1abc9c; -fx-text-fill: #117864; -fx-font-weight: bold;");
            } else {
                btnAsiento.setStyle("-fx-background-color: #fadbd8; -fx-border-color: #c0392b; -fx-text-fill: #922b21; -fx-font-weight: bold;");
            }

            btnAsiento.setOnAction(e -> alternarEstadoAsiento(sala, asiento));
            gridAsientos.add(btnAsiento, colIdx, filaIdx);
        }
    }

    private void alternarEstadoAsiento(Sala sala, Asiento asiento) {
        try {
            if (asiento.isHabilitado()) {
                controller.deshabilitarAsiento(sala.getId(), asiento.getCodigo());
                mostrarMensaje("Asiento " + asiento.getCodigo() + " deshabilitado.", false);
            } else {
                controller.habilitarAsiento(sala.getId(), asiento.getCodigo());
                mostrarMensaje("Asiento " + asiento.getCodigo() + " habilitado nuevamente.", false);
            }
            dibujarMapaAsientos(sala);
        } catch (CinemaException e) {
            mostrarMensaje(e.getMessage(), true);
        }
    }

    @FXML
    private void onCrearSala() {
        String id = txtIdSala.getText().trim();
        String nombre = txtNombreSala.getText().trim();
        String filasTexto = txtFilas.getText().trim();
        String columnasTexto = txtColumnas.getText().trim();

        if (id.isEmpty() || nombre.isEmpty() || filasTexto.isEmpty() || columnasTexto.isEmpty()) {
            mostrarMensaje("Completa todos los campos para crear la sala.", true);
            return;
        }

        try {
            int filas = Integer.parseInt(filasTexto);
            int columnas = Integer.parseInt(columnasTexto);
            Sala nueva = controller.crearSala(id, nombre, filas, columnas);

            txtIdSala.clear();
            txtNombreSala.clear();
            txtFilas.clear();
            txtColumnas.clear();

            cargarTabla();
            tblSalas.getSelectionModel().select(nueva);
            mostrarMensaje("Sala \"" + nombre + "\" creada con " + nueva.getCapacidad() + " asientos.", false);
        } catch (NumberFormatException e) {
            mostrarMensaje("Filas y columnas deben ser números enteros.", true);
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