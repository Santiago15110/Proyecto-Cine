package org.example.proyectocine.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.proyectocine.controller.RegistroUsuarioController;
import org.example.proyectocine.model.CinemaException;

public class RegistroUsuarioViewController {

    private static final int LONGITUD_MINIMA_CLAVE = 4;

    @FXML private Label lblTitulo;
    @FXML private Label lblDescripcion;
    @FXML private TextField txtNombre;
    @FXML private Label lblIdentificacion;
    @FXML private TextField txtIdentificacion;
    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtClave;
    @FXML private PasswordField txtConfirmarClave;
    @FXML private Label lblAutorizacion;
    @FXML private PasswordField txtAutorizacion;
    @FXML private Label lblMensaje;

    private final RegistroUsuarioController registroController = new RegistroUsuarioController();

    private Perfil perfil = Perfil.CLIENTE;

    @FXML
    private void initialize() {
        configurar(Perfil.CLIENTE);
    }

    /**
     * Ajusta los textos y campos al perfil elegido.
     *
     * @param perfil cliente o administrador
     */
    public void configurar(Perfil perfil) {
        this.perfil = perfil;
        boolean esAdmin = perfil == Perfil.ADMINISTRADOR;
        lblTitulo.setText(esAdmin ? "Crear cuenta de administrador" : "Crear cuenta de cliente");
        lblDescripcion.setText(esAdmin
                ? "Para registrarte como administrador necesitas el código de autorización que entrega el cine a su personal."
                : "Al crear tu cuenta recibes tu tarjeta virtual y empiezas a acumular puntos CinemaUQ.");
        lblIdentificacion.setText(esAdmin ? "Código de empleado:" : "Cédula:");
        txtIdentificacion.setPromptText(esAdmin ? "Ejemplo: AD-002" : "Solo números");
        lblAutorizacion.setVisible(esAdmin);
        lblAutorizacion.setManaged(esAdmin);
        txtAutorizacion.setVisible(esAdmin);
        txtAutorizacion.setManaged(esAdmin);
    }

    @FXML
    private void onRegistrar() {
        String nombre = txtNombre.getText().trim();
        String identificacion = txtIdentificacion.getText().trim();
        String correo = txtCorreo.getText().trim();
        String clave = txtClave.getText();
        String confirmar = txtConfirmarClave.getText();
        boolean esAdmin = perfil == Perfil.ADMINISTRADOR;

        if (nombre.isEmpty() || identificacion.isEmpty() || correo.isEmpty() || clave.isEmpty()
                || (esAdmin && txtAutorizacion.getText().isBlank())) {
            mostrarError("Todos los campos son obligatorios.");
            return;
        }
        if (!esAdmin && !identificacion.matches("\\d+")) {
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
            if (esAdmin) {
                registroController.registrarAdmin(nombre, correo, clave, identificacion, txtAutorizacion.getText().trim());
            } else {
                registroController.registrarCliente(nombre, correo, clave, identificacion);
            }
            LoginViewController login = SceneManager.cambiarA(Vista.LOGIN);
            login.configurar(perfil);
            login.mostrarCuentaCreada(correo);
        } catch (CinemaException | IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void onVolver() {
        LoginViewController login = SceneManager.cambiarA(Vista.LOGIN);
        login.configurar(perfil);
    }

    private void mostrarError(String mensaje) {
        lblMensaje.getStyleClass().removeAll("mensaje-error", "mensaje-exito");
        lblMensaje.getStyleClass().add("mensaje-error");
        lblMensaje.setText(mensaje);
    }
}
