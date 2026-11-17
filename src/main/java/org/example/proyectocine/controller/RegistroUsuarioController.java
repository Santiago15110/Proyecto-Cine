package org.example.proyectocine.controller;

import org.example.proyectocine.model.Admin;
import org.example.proyectocine.model.CineFacade;
import org.example.proyectocine.model.Cliente;

public class RegistroUsuarioController {

    private final CineFacade facade;

    public RegistroUsuarioController() {
        this.facade = new CineFacade();
    }

    /**
     * Registra un cliente. Al crearse recibe su tarjeta virtual y su cuenta de puntos.
     *
     * @throws org.example.proyectocine.model.CinemaException si el correo o la cédula ya están registrados
     */
    public Cliente registrarCliente(String nombre, String correo, String clave, String cedula) {
        return facade.registrarCliente(nombre, correo, clave, cedula);
    }

    /**
     * Registra un administrador validando el código de autorización del cine.
     *
     * @throws org.example.proyectocine.model.AccesoDenegadoException si el código de autorización no es válido
     * @throws org.example.proyectocine.model.CinemaException         si el correo ya está registrado
     */
    public Admin registrarAdmin(String nombre, String correo, String clave, String codigo, String autorizacion) {
        return facade.registrarAdminConAutorizacion(nombre, correo, clave, codigo, autorizacion);
    }
}
