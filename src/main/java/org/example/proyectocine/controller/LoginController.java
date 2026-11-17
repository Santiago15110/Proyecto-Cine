package org.example.proyectocine.controller;

import org.example.proyectocine.model.Admin;
import org.example.proyectocine.model.CineFacade;
import org.example.proyectocine.model.Usuario;

/**
 * Intermediario entre la pantalla de inicio de sesión y el modelo.
 * No usa nada de JavaFX: recibe datos simples y trabaja con {@link CineFacade}.
 */
public class LoginController {

    private final CineFacade facade;

    public LoginController() {
        this.facade = new CineFacade();
    }

    /**
     * Inicia sesión y deja al usuario como sesión actual del sistema.
     *
     * @param correo correo del usuario
     * @param clave  contraseña del usuario
     * @return el usuario que inició sesión
     * @throws org.example.proyectocine.model.CredencialesInvalidasException si el correo o la clave no coinciden
     */
    public Usuario iniciarSesion(String correo, String clave) {
        return facade.iniciarSesion(correo, clave);
    }

    public void cerrarSesion() {
        facade.cerrarSesion();
    }

    public Usuario obtenerUsuarioActual() {
        return facade.obtenerUsuarioActual();
    }

    public boolean esAdministrador(Usuario usuario) {
        return usuario instanceof Admin;
    }
}
