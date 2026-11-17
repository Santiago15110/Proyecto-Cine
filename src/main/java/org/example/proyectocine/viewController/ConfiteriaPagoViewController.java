package org.example.proyectocine.viewController;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.example.proyectocine.controller.PagoController;
import org.example.proyectocine.model.Asiento;
import org.example.proyectocine.model.CinemaException;
import org.example.proyectocine.model.Cliente;
import org.example.proyectocine.model.Compra;
import org.example.proyectocine.model.Funcion;
import org.example.proyectocine.model.ItemConfiteria;
import org.example.proyectocine.model.Promocion;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfiteriaPagoViewController {

    private static final Locale COLOMBIA = Locale.of("es", "CO");
    private static final DecimalFormat FORMATO_PESOS =
            new DecimalFormat("$ #,##0", DecimalFormatSymbols.getInstance(COLOMBIA));
    private static final DateTimeFormatter FORMATO_FUNCION =
            DateTimeFormatter.ofPattern("EEEE d 'de' MMMM, h:mm a", COLOMBIA);

    @FXML private TableView<ItemConfiteria> tblConfiteria;
    @FXML private TableColumn<ItemConfiteria, String> colProducto;
    @FXML private TableColumn<ItemConfiteria, String> colPrecio;
    @FXML private TableColumn<ItemConfiteria, String> colPuntosProducto;
    @FXML private Spinner<Integer> spnCantidad;

    @FXML private TableView<LineaPedido> tblCarrito;
    @FXML private TableColumn<LineaPedido, String> colCarritoProducto;
    @FXML private TableColumn<LineaPedido, String> colCarritoCantidad;
    @FXML private TableColumn<LineaPedido, String> colCarritoSubtotal;

    @FXML private Label lblPelicula;
    @FXML private Label lblFuncion;
    @FXML private Label lblAsientos;
    @FXML private Label lblEntradasTexto;
    @FXML private Label lblEntradas;
    @FXML private Label lblConfiteria;
    @FXML private ComboBox<OpcionPromocion> cmbPromocion;
    @FXML private Label lblTotal;
    @FXML private Label lblTotalPuntos;
    @FXML private Label lblDisponible;
    @FXML private Label lblMensaje;
    @FXML private Button btnPagarSaldo;
    @FXML private Button btnPagarPuntos;
    @FXML private Button btnIrPanel;
    @FXML private Button btnVolver;

    private final PagoController pagoController = new PagoController();

    private final Map<ItemConfiteria, Integer> carrito = new LinkedHashMap<>();

    private Cliente cliente;
    private Funcion funcion;
    private List<Asiento> asientos = new ArrayList<>();

    @FXML
    private void initialize() {
        colProducto.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colPrecio.setCellValueFactory(c -> new SimpleStringProperty(FORMATO_PESOS.format(c.getValue().getPrecio())));
        colPuntosProducto.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCostoPuntos() + " pts"));
        colCarritoProducto.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().item().getNombre()));
        colCarritoCantidad.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().cantidad())));
        colCarritoSubtotal.setCellValueFactory(c -> new SimpleStringProperty(
                FORMATO_PESOS.format(c.getValue().item().getPrecio() * c.getValue().cantidad())));

        spnCantidad.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1));
        tblConfiteria.getItems().setAll(pagoController.obtenerConfiteria());

        cmbPromocion.getItems().add(new OpcionPromocion(null));
        for (Promocion p : pagoController.obtenerPromocionesVigentes()) {
            cmbPromocion.getItems().add(new OpcionPromocion(p));
        }
        cmbPromocion.getSelectionModel().selectFirst();
        cmbPromocion.valueProperty().addListener((obs, anterior, nueva) -> actualizarResumen());

        cliente = pagoController.obtenerClienteEnSesion();
        if (cliente == null) {
            mostrarMensaje("Inicia sesión como cliente para comprar.", true);
            btnPagarSaldo.setDisable(true);
            btnPagarPuntos.setDisable(true);
        }
    }

    /**
     * Recibe la función y los asientos elegidos en la pantalla anterior.
     *
     * @param funcion  función elegida
     * @param asientos asientos elegidos
     */
    public void iniciar(Funcion funcion, List<Asiento> asientos) {
        this.funcion = funcion;
        this.asientos = new ArrayList<>(asientos);
        lblPelicula.setText(funcion.getPelicula().getTitulo());
        lblFuncion.setText(capitalizar(funcion.getFechaHora().format(FORMATO_FUNCION)) + " · " + funcion.getSala().getNombre());
        lblAsientos.setText("Asientos: " + this.asientos.stream().map(Asiento::getCodigo).collect(Collectors.joining(", ")));
        lblEntradasTexto.setText("Entradas (" + this.asientos.size() + " × " + FORMATO_PESOS.format(funcion.getPrecioBase()) + ")");
        actualizarResumen();
    }

    @FXML
    private void onAgregarProducto() {
        ItemConfiteria item = tblConfiteria.getSelectionModel().getSelectedItem();
        if (item == null) {
            mostrarMensaje("Selecciona un producto o combo de la lista.", true);
            return;
        }
        int cantidad = spnCantidad.getValue() == null ? 1 : spnCantidad.getValue();
        carrito.merge(item, cantidad, Integer::sum);
        lblMensaje.setText("");
        refrescarCarrito();
    }

    @FXML
    private void onQuitarProducto() {
        LineaPedido linea = tblCarrito.getSelectionModel().getSelectedItem();
        if (linea != null) {
            carrito.remove(linea.item());
            refrescarCarrito();
        }
    }

    @FXML
    private void onPagarConSaldo() {
        try {
            Compra compra = pagoController.pagarConSaldo(cliente, funcion, asientos, productosParaPagar(), promocionElegida());
            compraExitosa("¡Compra realizada! Código " + compra.getId() + ". Pagaste "
                    + FORMATO_PESOS.format(compra.getTotal()) + " con tu tarjeta.");
        } catch (CinemaException | IllegalArgumentException e) {
            mostrarMensaje(e.getMessage(), true);
        }
    }

    @FXML
    private void onPagarConPuntos() {
        try {
            Compra compra = pagoController.pagarConPuntos(cliente, funcion, asientos, productosParaPagar());
            compraExitosa("¡Compra realizada! Código " + compra.getId() + ". Redimiste "
                    + compra.getPuntosRedimidos() + " puntos.");
        } catch (CinemaException | IllegalArgumentException e) {
            mostrarMensaje(e.getMessage(), true);
        }
    }

    @FXML
    private void onVolver() {
        SalaAsientosViewController sala = SceneManager.cambiarA(Vista.SALA);
        sala.mostrarFuncion(funcion);
    }

    @FXML
    private void onIrPanel() {
        Navegacion.irAlPanel();
    }

    private void refrescarCarrito() {
        ArrayList<LineaPedido> lineas = new ArrayList<>();
        carrito.forEach((item, cantidad) -> lineas.add(new LineaPedido(item, cantidad)));
        tblCarrito.getItems().setAll(lineas);
        actualizarResumen();
    }

    private void actualizarResumen() {
        if (funcion == null) {
            return;
        }
        double entradas = funcion.getPrecioBase() * asientos.size();
        double confiteria = 0;
        for (Map.Entry<ItemConfiteria, Integer> e : carrito.entrySet()) {
            confiteria += e.getKey().getPrecio() * e.getValue();
        }
        lblEntradas.setText(FORMATO_PESOS.format(entradas));
        lblConfiteria.setText(FORMATO_PESOS.format(confiteria));

        if (cliente == null) {
            lblTotal.setText(FORMATO_PESOS.format(entradas + confiteria));
            return;
        }
        try {
            double total = pagoController.cotizar(cliente, funcion, asientos, productosParaPagar(), promocionElegida());
            lblTotal.setText(FORMATO_PESOS.format(total));
            lblTotalPuntos.setText(pagoController.costoEnPuntos(funcion, asientos, productosParaPagar()) + " pts");
            lblDisponible.setText("Tu saldo: " + FORMATO_PESOS.format(pagoController.consultarSaldo(cliente))
                    + " · Tus puntos: " + pagoController.consultarPuntos(cliente));
        } catch (CinemaException | IllegalArgumentException e) {
            mostrarMensaje(e.getMessage(), true);
        }
    }

    private Map<String, Integer> productosParaPagar() {
        Map<String, Integer> productos = new LinkedHashMap<>();
        carrito.forEach((item, cantidad) -> productos.put(item.getId(), cantidad));
        return productos;
    }

    private Promocion promocionElegida() {
        OpcionPromocion opcion = cmbPromocion.getValue();
        return opcion == null ? null : opcion.promocion();
    }

    private void compraExitosa(String mensaje) {
        mostrarMensaje(mensaje, false);
        btnPagarSaldo.setDisable(true);
        btnPagarPuntos.setDisable(true);
        cmbPromocion.setDisable(true);
        btnVolver.setVisible(false);
        btnIrPanel.setVisible(true);
        btnIrPanel.setManaged(true);
        btnIrPanel.setDefaultButton(true);
        lblDisponible.setText("Tu saldo: " + FORMATO_PESOS.format(pagoController.consultarSaldo(cliente))
                + " · Tus puntos: " + pagoController.consultarPuntos(cliente));
    }

    private void mostrarMensaje(String texto, boolean esError) {
        lblMensaje.getStyleClass().removeAll("mensaje-error", "mensaje-exito");
        lblMensaje.getStyleClass().add(esError ? "mensaje-error" : "mensaje-exito");
        lblMensaje.setText(texto);
    }

    private static String capitalizar(String texto) {
        return texto.isEmpty() ? texto : Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }

    public record LineaPedido(ItemConfiteria item, int cantidad) {
    }

    public record OpcionPromocion(Promocion promocion) {
        @Override
        public String toString() {
            return promocion == null ? "Sin promoción"
                    : promocion.getNombre() + " (" + (int) promocion.getPorcentaje() + " % de descuento)";
        }
    }
}
