package org.example.proyectocine.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.proyectocine.controller.LoginController;
import org.example.proyectocine.model.CinemaException;

/**
 * Pantalla de inicio de sesión. Valida los campos, pide al controller iniciar sesión
 * y lleva al usuario a la pantalla principal.
 */
public class LoginViewController {

    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtClave;
    @FXML private Label lblMensaje;

    private final LoginController loginController = new LoginController();

    @FXML
    private void initialize() {
        // Si se llega al login, cualquier sesión anterior se cierra
        loginController.cerrarSesion();
        lblMensaje.setVisible(false);
        lblMensaje.setManaged(false);
    }

    @FXML
    private void onIngresar() {
        String correo = txtCorreo.getText().trim();
        String clave = txtClave.getText();

        if (correo.isEmpty() || clave.isEmpty()) {
            mostrarError("Escribe tu correo y tu contraseña.");
            return;
        }
        if (!correo.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            mostrarError("El correo no tiene un formato válido.");
            return;
        }

        try {
            loginController.iniciarSesion(correo, clave);
            // El administrador va a su panel y el cliente al suyo
            Navegacion.irAlPanel();
        } catch (CinemaException e) {
            mostrarError(e.getMessage());
            txtClave.clear();
            txtClave.requestFocus();
        }
    }

    @FXML
    private void onRegistrarse() {
        SceneManager.cambiarA(Vista.REGISTRO);
    }

    /**
     * Se llama al volver del registro: deja el correo escrito y avisa que la cuenta se creó.
     *
     * @param correo correo de la cuenta nueva
     */
    public void mostrarCuentaCreada(String correo) {
        txtCorreo.setText(correo);
        mostrarMensaje("Cuenta creada. Ahora inicia sesión.", false);
        txtClave.requestFocus();
    }

    private void mostrarError(String mensaje) {
        mostrarMensaje(mensaje, true);
    }

    private void mostrarMensaje(String mensaje, boolean esError) {
        lblMensaje.getStyleClass().removeAll("mensaje-error", "mensaje-exito");
        lblMensaje.getStyleClass().add(esError ? "mensaje-error" : "mensaje-exito");
        lblMensaje.setText(mensaje);
        lblMensaje.setVisible(true);
        lblMensaje.setManaged(true);
    }
}
