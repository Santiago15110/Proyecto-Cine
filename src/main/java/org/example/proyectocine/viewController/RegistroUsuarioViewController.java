package org.example.proyectocine.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.proyectocine.controller.RegistroUsuarioController;
import org.example.proyectocine.model.CinemaException;

/**
 * Pantalla pública de registro. Solo crea clientes; al terminar lleva al login
 * con el correo ya escrito.
 */
public class RegistroUsuarioViewController {

    private static final int LONGITUD_MINIMA_CLAVE = 4;

    @FXML private TextField txtNombre;
    @FXML private TextField txtCedula;
    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtClave;
    @FXML private PasswordField txtConfirmarClave;
    @FXML private Label lblMensaje;

    private final RegistroUsuarioController registroController = new RegistroUsuarioController();

    /**
     * Valida los datos de la pantalla y pide al controller registrar al cliente.
     */
    @FXML
    private void onRegistrar() {
        String nombre = txtNombre.getText().trim();
        String cedula = txtCedula.getText().trim();
        String correo = txtCorreo.getText().trim();
        String clave = txtClave.getText();
        String confirmar = txtConfirmarClave.getText();

        // Validaciones de entrada (las de negocio, como correo o cédula repetidos, las hace el modelo)
        if (nombre.isEmpty() || cedula.isEmpty() || correo.isEmpty() || clave.isEmpty()) {
            mostrarError("Todos los campos son obligatorios.");
            return;
        }
        if (!cedula.matches("\\d+")) {
            mostrarError("La cédula solo debe tener números.");
            return;
        }
        if (!correo.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            mostrarError("El correo no tiene un formato válido.");
            return;
        }
        if (clave.length() < LONGITUD_MINIMA_CLAVE) {
            mostrarError("La contraseña debe tener al menos " + LONGITUD_MINIMA_CLAVE + " caracteres.");
            return;
        }
        if (!clave.equals(confirmar)) {
            mostrarError("Las contraseñas no coinciden.");
            return;
        }

        try {
            registroController.registrarCliente(nombre, correo, clave, cedula);
            LoginViewController login = SceneManager.cambiarA(Vista.LOGIN);
            login.mostrarCuentaCreada(correo);
        } catch (CinemaException | IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void onVolver() {
        SceneManager.cambiarA(Vista.LOGIN);
    }

    private void mostrarError(String mensaje) {
        lblMensaje.getStyleClass().removeAll("mensaje-error", "mensaje-exito");
        lblMensaje.getStyleClass().add("mensaje-error");
        lblMensaje.setText(mensaje);
    }
}
