package org.example.proyectocine.controller;

import org.example.proyectocine.model.Admin;
import org.example.proyectocine.model.CineFacade;
import org.example.proyectocine.model.Cliente;
import org.example.proyectocine.model.Usuario;

import java.util.ArrayList;

/**
 * Intermediario entre la pantalla de registro y el modelo.
 * No usa nada de JavaFX: recibe datos simples y llama a {@link CineFacade}.
 */
public class RegistroUsuarioController {

    private final CineFacade facade;

    public RegistroUsuarioController() {
        this.facade = new CineFacade();
    }

    /**
     * Registra un cliente en el cine.
     *
     * @param nombre nombre completo del cliente
     * @param correo correo electrónico del cliente
     * @param clave  contraseña del cliente
     * @param cedula cédula del cliente
     * @return el cliente registrado
     * @throws org.example.proyectocine.model.CinemaException si el correo o la cédula ya están registrados
     */
    public Cliente registrarCliente(String nombre, String correo, String clave, String cedula) {
        return facade.registrarCliente(nombre, correo, clave, cedula);
    }

    /**
     * Registra un administrador en el cine.
     *
     * @param nombre nombre completo del administrador
     * @param correo correo electrónico del administrador
     * @param clave  contraseña del administrador
     * @param codigo código de administrador
     * @return el administrador registrado
     * @throws org.example.proyectocine.model.CinemaException si el correo ya está registrado
     */
    public Admin registrarAdmin(String nombre, String correo, String clave, String codigo) {
        return facade.registrarAdmin(nombre, correo, clave, codigo);
    }

    public ArrayList<Usuario> obtenerUsuarios() {
        return facade.obtenerUsuarios();
    }

    /**
     * Devuelve el tipo de usuario como texto para mostrarlo en pantalla.
     *
     * @param usuario usuario a consultar
     * @return "Administrador" o "Cliente"
     */
    public String obtenerTipo(Usuario usuario) {
        return usuario instanceof Admin ? "Administrador" : "Cliente";
    }

    /**
     * Devuelve la identificación del usuario: la cédula si es cliente o el código si es administrador.
     *
     * @param usuario usuario a consultar
     * @return la cédula o el código
     */
    public String obtenerIdentificacion(Usuario usuario) {
        if (usuario instanceof Cliente c) {
            return c.getCedula();
        }
        if (usuario instanceof Admin a) {
            return a.getCodigo();
        }
        return "";
    }
}
