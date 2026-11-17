package org.example.proyectocine.controller;

import org.example.proyectocine.model.CineFacade;
import org.example.proyectocine.model.Cliente;
import org.example.proyectocine.model.Usuario;

import java.util.ArrayList;
import java.util.Collections;

public class PanelClienteController {

    private final CineFacade facade;

    public PanelClienteController() {
        this.facade = new CineFacade();
    }

    /**
     * Devuelve el cliente que tiene la sesión abierta.
     *
     * @return el cliente, o null si quien tiene la sesión no es cliente
     */
    public Cliente obtenerClienteEnSesion() {
        Usuario usuario = facade.obtenerUsuarioActual();
        return usuario instanceof Cliente ? (Cliente) usuario : null;
    }

    public double consultarSaldo(Cliente cliente) {
        return facade.consultarSaldo(cliente.getCedula());
    }

    public int consultarPuntos(Cliente cliente) {
        return facade.consultarPuntos(cliente.getCedula());
    }

    public int contarCompras(Cliente cliente) {
        return facade.historialCliente(cliente.getCedula()).size();
    }

    /**
     * Notificaciones del cliente, de la más reciente a la más antigua.
     *
     * @param cliente cliente en sesión
     * @return notificaciones
     */
    public ArrayList<String> obtenerNotificaciones(Cliente cliente) {
        ArrayList<String> notificaciones = facade.obtenerNotificaciones(cliente.getCedula());
        Collections.reverse(notificaciones);
        return notificaciones;
    }

    public void cerrarSesion() {
        facade.cerrarSesion();
    }
}
