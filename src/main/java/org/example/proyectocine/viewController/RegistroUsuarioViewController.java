package org.example.proyectocine.viewController;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import org.example.proyectocine.controller.RegistroUsuarioController;
import org.example.proyectocine.model.CinemaException;
import org.example.proyectocine.model.Usuario;

/**
 * Controlador de la pantalla RegistroUsuario.fxml.
 * Solo maneja la interfaz; las operaciones las delega a {@link RegistroUsuarioController}.
 */
public class RegistroUsuarioViewController {

    @FXML private ToggleGroup tgTipo;
    @FXML private RadioButton rbCliente;
    @FXML private RadioButton rbAdmin;

    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtClave;
    @FXML private PasswordField txtConfirmarClave;
    @FXML private Label lblIdentificacion;
    @FXML private TextField txtIdentificacion;

    @FXML private Label lblMensaje;

    @FXML private TableView<Usuario> tblUsuarios;
    @FXML private TableColumn<Usuario, String> colTipo;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colCorreo;
    @FXML private TableColumn<Usuario, String> colIdentificacion;

    private final RegistroUsuarioController registroController = new RegistroUsuarioController();

    @FXML
    private void initialize() {
        // Cambia el último campo según el tipo de usuario elegido
        tgTipo.selectedToggleProperty().addListener((obs, anterior, nuevo) -> actualizarCampoIdentificacion());

        colTipo.setCellValueFactory(c -> new SimpleStringProperty(registroController.obtenerTipo(c.getValue())));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colCorreo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCorreo()));
        colIdentificacion.setCellValueFactory(c -> new SimpleStringProperty(registroController.obtenerIdentificacion(c.getValue())));

        refrescarTabla();
    }

    /**
     * Valida los datos de la pantalla y pide al controller registrar un cliente o un administrador.
     */
    @FXML
    private void onRegistrar() {
        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();
        String clave = txtClave.getText();
        String confirmar = txtConfirmarClave.getText();
        String identificacion = txtIdentificacion.getText().trim();

        // Validaciones de entrada (las de negocio, como correo o cédula repetidos, las hace el modelo)
        if (nombre.isEmpty() || correo.isEmpty() || clave.isEmpty() || identificacion.isEmpty()) {
            mostrarError("Todos los campos son obligatorios.");
            return;
        }
        if (!correo.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            mostrarError("El correo no tiene un formato válido.");
            return;
        }
        if (!clave.equals(confirmar)) {
            mostrarError("Las contraseñas no coinciden.");
            return;
        }
        if (esCliente() && !identificacion.matches("\\d+")) {
            mostrarError("La cédula solo debe tener números.");
            return;
        }

        try {
            if (esCliente()) {
                registroController.registrarCliente(nombre, correo, clave, identificacion);
                mostrarExito("Cliente " + nombre + " registrado correctamente.");
            } else {
                registroController.registrarAdmin(nombre, correo, clave, identificacion);
                mostrarExito("Administrador " + nombre + " registrado correctamente.");
            }
            limpiarCampos();
            refrescarTabla();
        } catch (CinemaException | IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void onLimpiar() {
        limpiarCampos();
        lblMensaje.setText("");
    }

    @FXML
    private void onVolver() {
        SceneManager.cambiarA(Vista.INICIO);
    }

    private boolean esCliente() {
        return rbCliente.isSelected();
    }

    private void actualizarCampoIdentificacion() {
        if (esCliente()) {
            lblIdentificacion.setText("Cédula:");
            txtIdentificacion.setPromptText("Número de cédula");
        } else {
            lblIdentificacion.setText("Código de admin:");
            txtIdentificacion.setPromptText("Código de administrador");
        }
        txtIdentificacion.clear();
    }

    private void refrescarTabla() {
        tblUsuarios.getItems().setAll(registroController.obtenerUsuarios());
    }

    private void limpiarCampos() {
        txtNombre.clear();
        txtCorreo.clear();
        txtClave.clear();
        txtConfirmarClave.clear();
        txtIdentificacion.clear();
        txtNombre.requestFocus();
    }

    private void mostrarError(String mensaje) {
        lblMensaje.getStyleClass().removeAll("mensaje-error", "mensaje-exito");
        lblMensaje.getStyleClass().add("mensaje-error");
        lblMensaje.setText(mensaje);
    }

    private void mostrarExito(String mensaje) {
        lblMensaje.getStyleClass().removeAll("mensaje-error", "mensaje-exito");
        lblMensaje.getStyleClass().add("mensaje-exito");
        lblMensaje.setText(mensaje);
    }
}
