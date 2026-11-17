package org.example.proyectocine.viewController;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.proyectocine.controller.CarteleraController;
import org.example.proyectocine.model.Funcion;
import org.example.proyectocine.model.Genero;
import org.example.proyectocine.model.Pelicula;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CarteleraViewController {

    private static final Locale COLOMBIA = new Locale("es", "CO");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("EEE d MMM", COLOMBIA);
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("h:mm a", COLOMBIA);
    private static final String TODOS_LOS_GENEROS = "Todos los géneros";

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbGenero;
    @FXML private FlowPane flowPeliculas;

    @FXML private Label lblSinSeleccion;
    @FXML private VBox panelDetalle;
    @FXML private ImageView imgPoster;
    @FXML private VBox posterVacio;
    @FXML private Label lblPosterTexto;
    @FXML private Label lblTitulo;
    @FXML private Label lblInfo;
    @FXML private Label lblDescripcion;
    @FXML private FlowPane flowFechas;
    @FXML private Label lblHorarios;
    @FXML private FlowPane flowHorarios;
    @FXML private Label lblSinFunciones;
    @FXML private Button btnElegirAsientos;
    @FXML private Label lblMensaje;

    private final CarteleraController carteleraController = new CarteleraController();

    private Pelicula peliculaSeleccionada;
    private VBox tarjetaSeleccionada;
    private Funcion funcionSeleccionada;

    @FXML
    private void initialize() {
        cmbGenero.getItems().add(TODOS_LOS_GENEROS);
        for (Genero g : Genero.values()) {
            cmbGenero.getItems().add(nombreGenero(g));
        }
        cmbGenero.getSelectionModel().selectFirst();

        txtBuscar.textProperty().addListener((obs, anterior, nuevo) -> refrescarPeliculas());
        cmbGenero.valueProperty().addListener((obs, anterior, nuevo) -> refrescarPeliculas());

        mostrarDetalle(false);
        refrescarPeliculas();
    }

    private void refrescarPeliculas() {
        flowPeliculas.getChildren().clear();
        tarjetaSeleccionada = null;

        ArrayList<Pelicula> peliculas = carteleraController.buscarPeliculas(txtBuscar.getText(), generoElegido());
        if (peliculas.isEmpty()) {
            Label vacio = new Label("No hay películas que coincidan con la búsqueda");
            vacio.getStyleClass().add("texto-secundario");
            flowPeliculas.getChildren().add(vacio);
        }
        for (Pelicula p : peliculas) {
            VBox tarjeta = crearTarjeta(p);
            flowPeliculas.getChildren().add(tarjeta);
            if (p.equals(peliculaSeleccionada)) {
                marcarTarjeta(tarjeta);
            }
        }
    }

    private VBox crearTarjeta(Pelicula pelicula) {
        StackPane poster = crearPoster(pelicula, 130, 185);

        Label titulo = new Label(pelicula.getTitulo());
        titulo.getStyleClass().add("titulo-tarjeta");
        titulo.setWrapText(true);
        titulo.setMaxWidth(130);

        Label genero = new Label(nombreGenero(pelicula.getGenero()) + " · " + pelicula.getDuracionMin() + " min");
        genero.getStyleClass().add("texto-secundario");

        VBox tarjeta = new VBox(6, poster, titulo, genero);
        tarjeta.getStyleClass().add("tarjeta-pelicula");
        tarjeta.setOnMouseClicked(e -> {
            marcarTarjeta(tarjeta);
            seleccionarPelicula(pelicula);
        });
        return tarjeta;
    }

    private void marcarTarjeta(VBox tarjeta) {
        if (tarjetaSeleccionada != null) {
            tarjetaSeleccionada.getStyleClass().remove("tarjeta-pelicula-seleccionada");
        }
        tarjetaSeleccionada = tarjeta;
        tarjeta.getStyleClass().add("tarjeta-pelicula-seleccionada");
    }

    private void seleccionarPelicula(Pelicula pelicula) {
        peliculaSeleccionada = pelicula;
        funcionSeleccionada = null;
        lblMensaje.setText("");
        mostrarDetalle(true);

        lblTitulo.setText(pelicula.getTitulo());
        lblInfo.setText(nombreGenero(pelicula.getGenero())
                + textoSiExiste(pelicula.getClasificacion())
                + " · " + pelicula.getDuracionMin() + " min");
        lblDescripcion.setText(pelicula.getDescripcion() == null ? "" : pelicula.getDescripcion());
        cargarPoster(pelicula.getRutaPoster(), pelicula.getTitulo());

        cargarFechas(carteleraController.obtenerFunciones(pelicula));
        actualizarBotonAsientos();
    }

    private void cargarFechas(List<Funcion> funciones) {
        flowFechas.getChildren().clear();
        flowHorarios.getChildren().clear();

        Map<LocalDate, List<Funcion>> porFecha = new LinkedHashMap<>();
        for (Funcion f : funciones) {
            porFecha.computeIfAbsent(f.getFechaHora().toLocalDate(), d -> new ArrayList<>()).add(f);
        }

        boolean hayFunciones = !porFecha.isEmpty();
        lblSinFunciones.setVisible(!hayFunciones);
        lblSinFunciones.setManaged(!hayFunciones);
        lblHorarios.setVisible(hayFunciones);
        lblHorarios.setManaged(hayFunciones);

        ToggleGroup grupoFechas = new ToggleGroup();
        for (Map.Entry<LocalDate, List<Funcion>> dia : porFecha.entrySet()) {
            ToggleButton boton = new ToggleButton(textoFecha(dia.getKey()));
            boton.getStyleClass().add("boton-opcion");
            boton.setToggleGroup(grupoFechas);
            boton.setOnAction(e -> {
                if (boton.isSelected()) {
                    cargarHorarios(dia.getValue());
                } else {
                    boton.setSelected(true);
                }
            });
            flowFechas.getChildren().add(boton);
        }

        if (hayFunciones) {
            ToggleButton primera = (ToggleButton) flowFechas.getChildren().get(0);
            primera.setSelected(true);
            cargarHorarios(porFecha.values().iterator().next());
        }
    }

    private void cargarHorarios(List<Funcion> funcionesDelDia) {
        flowHorarios.getChildren().clear();
        funcionSeleccionada = null;
        actualizarBotonAsientos();

        ToggleGroup grupoHoras = new ToggleGroup();
        for (Funcion f : funcionesDelDia) {
            ToggleButton boton = new ToggleButton(
                    f.getFechaHora().format(FORMATO_HORA) + "\n" + f.getSala().getNombre());
            boton.getStyleClass().add("boton-opcion");
            boton.setToggleGroup(grupoHoras);
            boton.setOnAction(e -> {
                funcionSeleccionada = boton.isSelected() ? f : null;
                lblMensaje.setText("");
                actualizarBotonAsientos();
            });
            flowHorarios.getChildren().add(boton);
        }
    }

    private void actualizarBotonAsientos() {
        btnElegirAsientos.setDisable(funcionSeleccionada == null);
    }

    private void mostrarDetalle(boolean visible) {
        panelDetalle.setVisible(visible);
        panelDetalle.setManaged(visible);
        lblSinSeleccion.setVisible(!visible);
        lblSinSeleccion.setManaged(!visible);
    }

    @FXML
    private void onElegirAsientos() {
        if (funcionSeleccionada == null) {
            lblMensaje.setText("Elige la fecha y la hora de la función");
            return;
        }
        SalaAsientosViewController asientos = SceneManager.cambiarA(Vista.SALA);
        asientos.mostrarFuncion(funcionSeleccionada);
    }

    @FXML
    private void onLimpiarFiltros() {
        txtBuscar.clear();
        cmbGenero.getSelectionModel().selectFirst();
    }

    @FXML
    private void onVolver() {
        Navegacion.irAlPanel();
    }

    private Genero generoElegido() {
        String valor = cmbGenero.getValue();
        if (valor == null || valor.equals(TODOS_LOS_GENEROS)) {
            return null;
        }
        for (Genero g : Genero.values()) {
            if (nombreGenero(g).equals(valor)) {
                return g;
            }
        }
        return null;
    }

    private static String nombreGenero(Genero genero) {
        String texto = genero.name().replace('_', ' ').toLowerCase();
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }

    private static String textoSiExiste(String clasificacion) {
        return (clasificacion == null || clasificacion.isBlank()) ? "" : " · " + clasificacion;
    }

    private static String textoFecha(LocalDate fecha) {
        LocalDate hoy = LocalDate.now();
        if (fecha.equals(hoy)) {
            return "Hoy";
        }
        if (fecha.equals(hoy.plusDays(1))) {
            return "Mañana";
        }
        String texto = fecha.format(FORMATO_FECHA);
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }

    private StackPane crearPoster(Pelicula pelicula, double ancho, double alto) {
        StackPane contenedor = new StackPane();
        contenedor.setPrefSize(ancho, alto);
        contenedor.setMinSize(ancho, alto);
        contenedor.setMaxSize(ancho, alto);

        Image imagen = buscarImagen(pelicula.getRutaPoster());
        if (imagen != null) {
            ImageView vista = new ImageView(imagen);
            vista.setFitWidth(ancho);
            vista.setFitHeight(alto);
            vista.setPreserveRatio(true);
            contenedor.getChildren().add(vista);
        } else {
            Label texto = new Label(pelicula.getTitulo());
            texto.setWrapText(true);
            texto.setAlignment(Pos.CENTER);
            VBox vacio = new VBox(texto);
            vacio.setAlignment(Pos.CENTER);
            vacio.getStyleClass().add("poster-vacio");
            vacio.setStyle("-fx-padding: 8;");
            contenedor.getChildren().add(vacio);
        }
        return contenedor;
    }

    private void cargarPoster(String ruta, String titulo) {
        Image imagen = buscarImagen(ruta);
        imgPoster.setImage(imagen);
        imgPoster.setVisible(imagen != null);
        posterVacio.setVisible(imagen == null);
        lblPosterTexto.setText(titulo);
    }

    private Image buscarImagen(String ruta) {
        if (ruta == null || ruta.isBlank()) {
            return null;
        }
        try {
            URL recurso = getClass().getResource(ruta);
            Image imagen = null;
            if (recurso != null) {
                imagen = new Image(recurso.toExternalForm());
            } else if (new File(ruta).exists()) {
                imagen = new Image(new File(ruta).toURI().toString());
            }
            return (imagen == null || imagen.isError()) ? null : imagen;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
