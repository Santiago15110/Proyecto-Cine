package org.example.proyectocine.viewController;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.proyectocine.controller.LoginController;
import org.example.proyectocine.model.CinemaException;
import org.example.proyectocine.model.Usuario;

public class LoginViewController {

    @FXML private Label lblPerfil;
    @FXML private Label lblLema;
    @FXML private Label lblTitulo;
    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtClave;
    @FXML private Label lblMensaje;
    @FXML private Label lblSinCuenta;
    @FXML private Hyperlink lnkRegistrarse;

    private final LoginController loginController = new LoginController();

    private Perfil perfil = Perfil.CLIENTE;

    @FXML
    private void initialize() {
        loginController.cerrarSesion();
        ocultarMensaje();
        configurar(Perfil.CLIENTE);
    }

    /**
     * Ajusta la pantalla al perfil elegido en la bienvenida.
     *
     * @param perfil cliente o administrador
     */
    public void configurar(Perfil perfil) {
        this.perfil = perfil;
        lblPerfil.setText(perfil.getTitulo());
        lblLema.setText(perfil.getDescripcion());
        lblTitulo.setText(perfil == Perfil.CLIENTE ? "Iniciar sesión" : "Acceso de administradores");
        lblSinCuenta.setText(perfil == Perfil.CLIENTE ? "¿No tienes cuenta?" : "¿Eres nuevo en el equipo?");
        lnkRegistrarse.setText(perfil == Perfil.CLIENTE ? "Regístrate" : "Regístrate con tu código");
    }

    @FXML
    private void onIngresar() {
        String correo = txtCorreo.getText().trim();
        String clave = txtClave.getText();

        if (correo.isEmpty() || clave.isEmpty()) {
            mostrarMensaje("Escribe tu correo y tu contraseña.", true);
            return;
        }
        if (!correo.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            mostrarMensaje("El correo no tiene un formato válido.", true);
            return;
        }

        try {
            Usuario usuario = loginController.iniciarSesion(correo, clave);
            boolean esAdmin = loginController.esAdministrador(usuario);
            if (esAdmin != (perfil == Perfil.ADMINISTRADOR)) {
                loginController.cerrarSesion();
                mostrarMensaje(esAdmin
                        ? "Esta cuenta es de administrador. Vuelve y elige \"Soy administrador\"."
                        : "Esta cuenta es de cliente. Vuelve y elige \"Soy cliente\".", true);
                txtClave.clear();
                return;
            }
            Navegacion.irAlPanel();
        } catch (CinemaException e) {
            mostrarMensaje(e.getMessage(), true);
            txtClave.clear();
            txtClave.requestFocus();
        }
    }

    @FXML
    private void onRegistrarse() {
        RegistroUsuarioViewController registro = SceneManager.cambiarA(Vista.REGISTRO);
        registro.configurar(perfil);
    }

    @FXML
    private void onCambiarPerfil() {
        SceneManager.cambiarA(Vista.BIENVENIDA);
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

    private void ocultarMensaje() {
        lblMensaje.setVisible(false);
        lblMensaje.setManaged(false);
    }

    private void mostrarMensaje(String mensaje, boolean esError) {
        lblMensaje.getStyleClass().removeAll("mensaje-error", "mensaje-exito");
        lblMensaje.getStyleClass().add(esError ? "mensaje-error" : "mensaje-exito");
        lblMensaje.setText(mensaje);
        lblMensaje.setVisible(true);
        lblMensaje.setManaged(true);
    }
}
