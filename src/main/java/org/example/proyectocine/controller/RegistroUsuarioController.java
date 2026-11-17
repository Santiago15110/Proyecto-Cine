package org.example.proyectocine.controller;

import org.example.proyectocine.model.CineFacade;
import org.example.proyectocine.model.Cliente;

/**
 * Intermediario entre la pantalla pública de registro y el modelo.
 * Solo registra clientes: los administradores los crea otro administrador desde su panel.
 * No usa nada de JavaFX: recibe datos simples y llama a {@link CineFacade}.
 */
public class RegistroUsuarioController {

    private final CineFacade facade;

    public RegistroUsuarioController() {
        this.facade = new CineFacade();
    }

    /**
     * Registra un cliente en el cine. Al crearse, el cliente recibe su tarjeta virtual
     * y su cuenta de puntos.
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
}
