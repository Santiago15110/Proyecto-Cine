package org.example.proyectocine.viewController;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.example.proyectocine.controller.SalaAsientosController;
import org.example.proyectocine.model.Asiento;
import org.example.proyectocine.model.Funcion;
import org.example.proyectocine.model.Sala;

import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Pantalla de selección de asientos de una función.
 * La cuadrícula se arma con las filas y columnas de la sala, así que sirve para cualquier sala.
 * Quien abra esta pantalla debe llamar a {@link #mostrarFuncion(Funcion)}.
 */
public class SalaAsientosViewController {

    private static final Locale COLOMBIA = new Locale("es", "CO");
    private static final DecimalFormat FORMATO_PESOS =
            new DecimalFormat("$ #,##0", DecimalFormatSymbols.getInstance(COLOMBIA));
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("EEE d 'de' MMM", COLOMBIA);
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("h:mm a", COLOMBIA);

    // Barra superior
    @FXML private Label lblPeliculaBarra;
    @FXML private Label lblFechaBarra;
    @FXML private Label lblHoraBarra;
    @FXML private Label lblSalaBarra;

    // Mapa de asientos
    @FXML private GridPane gridAsientos;

    // Resumen lateral
    @FXML private ImageView imgPoster;
    @FXML private VBox posterVacio;
    @FXML private Label lblTitulo;
    @FXML private Label lblFecha;
    @FXML private Label lblHora;
    @FXML private Label lblSala;
    @FXML private FlowPane flowSeleccionados;
    @FXML private Label lblDetalleEntradas;
    @FXML private Label lblValorEntradas;
    @FXML private Label lblSubtotal;
    @FXML private Label lblTotal;
    @FXML private Label lblPuntos;
    @FXML private Label lblMensaje;

    private final SalaAsientosController salaController = new SalaAsientosController();

    /** Asientos elegidos, en el orden en que se hizo clic. */
    private final Set<Asiento> seleccionados = new LinkedHashSet<>();
    private Funcion funcion;

    @FXML
    private void initialize() {
        actualizarResumen();
    }

    /**
     * Carga la función en la pantalla: datos de la película y mapa de asientos de su sala.
     *
     * @param funcion función a la que se le eligen asientos
     */
    public void mostrarFuncion(Funcion funcion) {
        this.funcion = funcion;
        seleccionados.clear();
        lblMensaje.setText("");

        String titulo = funcion.getPelicula().getTitulo();
        String fecha = capitalizar(funcion.getFechaHora().format(FORMATO_FECHA));
        String hora = funcion.getFechaHora().format(FORMATO_HORA);
        String sala = funcion.getSala().getNombre();

        lblPeliculaBarra.setText(titulo);
        lblFechaBarra.setText(fecha);
        lblHoraBarra.setText(hora);
        lblSalaBarra.setText(sala);

        lblTitulo.setText(titulo);
        lblFecha.setText(fecha);
        lblHora.setText(hora);
        lblSala.setText(sala);
        cargarPoster(funcion.getPelicula().getRutaPoster());

        dibujarAsientos();
        actualizarResumen();
    }

    /**
     * Devuelve los asientos elegidos, para pasarlos al siguiente paso de la compra.
     */
    public ArrayList<Asiento> getAsientosSeleccionados() {
        return new ArrayList<>(seleccionados);
    }

    public Funcion getFuncion() {
        return funcion;
    }

    // ===================== MAPA DE ASIENTOS =====================

    private void dibujarAsientos() {
        gridAsientos.getChildren().clear();
        Sala sala = funcion.getSala();

        for (int f = 0; f < sala.getFilas(); f++) {
            char fila = (char) ('A' + f);

            Label letra = new Label(String.valueOf(fila));
            letra.getStyleClass().add("letra-fila");
            gridAsientos.add(letra, 0, f);
            GridPane.setHalignment(letra, HPos.CENTER);

            for (int c = 1; c <= sala.getColumnas(); c++) {
                gridAsientos.add(crearBotonAsiento(sala.buscarAsiento(fila, c)), c, f);
            }
        }
    }

    private Button crearBotonAsiento(Asiento asiento) {
        Button boton = new Button();
        boton.setFocusTraversable(false);
        boton.getStyleClass().add("asiento");

        if (!asiento.isHabilitado()) {
            boton.setText("×");
            boton.getStyleClass().add("asiento-deshabilitado");
            boton.setMouseTransparent(true);
        } else if (salaController.estaOcupado(funcion, asiento)
                || !salaController.estaDisponible(funcion, asiento)) {
            boton.setText(String.valueOf(asiento.getNumero()));
            boton.getStyleClass().add("asiento-ocupado");
            boton.setMouseTransparent(true);
        } else {
            boton.setText(String.valueOf(asiento.getNumero()));
            boton.getStyleClass().add("asiento-disponible");
            boton.setOnAction(e -> alternarSeleccion(asiento, boton));
        }
        return boton;
    }

    private void alternarSeleccion(Asiento asiento, Button boton) {
        if (seleccionados.remove(asiento)) {
            boton.getStyleClass().remove("asiento-seleccionado");
            boton.getStyleClass().add("asiento-disponible");
        } else {
            seleccionados.add(asiento);
            boton.getStyleClass().remove("asiento-disponible");
            boton.getStyleClass().add("asiento-seleccionado");
        }
        lblMensaje.setText("");
        actualizarResumen();
    }

    // ===================== RESUMEN =====================

    private void actualizarResumen() {
        flowSeleccionados.getChildren().clear();
        if (seleccionados.isEmpty()) {
            Label vacio = new Label("Ninguno todavía");
            vacio.getStyleClass().add("texto-secundario");
            flowSeleccionados.getChildren().add(vacio);
        } else {
            for (Asiento a : seleccionados) {
                Label chip = new Label(a.getCodigo());
                chip.getStyleClass().add("chip-asiento");
                flowSeleccionados.getChildren().add(chip);
            }
        }

        int cantidad = seleccionados.size();
        double precio = (funcion == null) ? 0 : funcion.getPrecioBase();
        double subtotal = (funcion == null) ? 0 : salaController.calcularSubtotal(funcion, cantidad);

        lblDetalleEntradas.setText(cantidad + " × " + FORMATO_PESOS.format(precio));
        lblValorEntradas.setText(FORMATO_PESOS.format(subtotal));
        lblSubtotal.setText(FORMATO_PESOS.format(subtotal));
        lblTotal.setText(FORMATO_PESOS.format(subtotal));
        lblPuntos.setText(salaController.calcularPuntos(subtotal) + " pts");
    }

    private void cargarPoster(String ruta) {
        Image imagen = null;
        if (ruta != null && !ruta.isBlank()) {
            try {
                URL recurso = getClass().getResource(ruta);
                if (recurso != null) {
                    imagen = new Image(recurso.toExternalForm());
                } else if (new File(ruta).exists()) {
                    imagen = new Image(new File(ruta).toURI().toString());
                }
            } catch (IllegalArgumentException e) {
                imagen = null;
            }
        }
        boolean hayImagen = imagen != null && !imagen.isError();
        imgPoster.setImage(hayImagen ? imagen : null);
        imgPoster.setVisible(hayImagen);
        posterVacio.setVisible(!hayImagen);
        posterVacio.getChildren().stream()
                .filter(n -> n instanceof Label)
                .forEach(n -> ((Label) n).setText(lblTitulo.getText()));
    }

    private static String capitalizar(String texto) {
        return texto.isEmpty() ? texto : Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }

    // ===================== BOTONES =====================

    @FXML
    private void onContinuar() {
        if (seleccionados.isEmpty()) {
            mostrarMensaje("Elige al menos un asiento para continuar", true);
            return;
        }
        // Pendiente: aquí se abrirá la pantalla de confitería y descuentos, pasándole
        // la función y getAsientosSeleccionados()
        mostrarMensaje("Asientos elegidos: " + seleccionados.size()
                + ". El paso de confitería y descuentos todavía no está disponible.", false);
    }

    @FXML
    private void onVolver() {
        // Cuando exista la pantalla de película, se volverá a ella
        SceneManager.cambiarA(Vista.INICIO);
    }

    private void mostrarMensaje(String texto, boolean esError) {
        lblMensaje.getStyleClass().removeAll("mensaje-error", "mensaje-exito");
        lblMensaje.getStyleClass().add(esError ? "mensaje-error" : "mensaje-exito");
        lblMensaje.setText(texto);
    }
}
