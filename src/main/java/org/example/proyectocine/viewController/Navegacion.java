package org.example.proyectocine.viewController;

import org.example.proyectocine.controller.LoginController;
import org.example.proyectocine.model.Usuario;

public final class Navegacion {

    private static final LoginController loginController = new LoginController();

    private Navegacion() {
    }

    public static void irAlPanel() {
        Usuario usuario = loginController.obtenerUsuarioActual();
        if (usuario == null) {
            SceneManager.cambiarA(Vista.BIENVENIDA);
        } else if (loginController.esAdministrador(usuario)) {
            SceneManager.cambiarA(Vista.PANEL_ADMIN);
        } else {
            SceneManager.cambiarA(Vista.PANEL_CLIENTE);
        }
    }
}
