package org.example.proyectocine.controller;

import org.example.proyectocine.model.CineFacade;
import org.example.proyectocine.model.Cliente;
import org.example.proyectocine.model.Compra;
import org.example.proyectocine.model.Usuario;

import java.util.ArrayList;

/**
 * Puente entre la pantalla "Mis compras" y el modelo.
 */
public class MisComprasController {

    private final CineFacade facade;

    public MisComprasController() {
        this.facade = new CineFacade();
    }

    /**
     * @return el cliente con sesión abierta, o null si no hay sesión de cliente
     */
    public Cliente obtenerClienteEnSesion() {
        Usuario usuario = facade.obtenerUsuarioActual();
        return usuario instanceof Cliente ? (Cliente) usuario : null;
    }

    /**
     * Historial del cliente, de la compra más reciente a la más antigua.
     *
     * @param cliente cliente en sesión
     * @return compras del cliente
     */
    public ArrayList<Compra> obtenerCompras(Cliente cliente) {
        ArrayList<Compra> compras = facade.historialCliente(cliente.getCedula());
        compras.sort((a, b) -> b.getFecha().compareTo(a.getFecha()));
        return compras;
    }

    /**
     * Porcentaje que se devolvería si se cancela ahora.
     *
     * @throws org.example.proyectocine.model.CancelacionNoPermitidaException si no se puede cancelar
     */
    public double consultarPorcentajeReembolso(Compra compra) {
        return facade.consultarPorcentajeReembolso(compra.getId());
    }

    /**
     * Dinero que se devolvería con ese porcentaje (para compras pagadas con saldo).
     */
    public double calcularReembolsoDinero(Compra compra, double porcentaje) {
        return compra.getTotal() * porcentaje / 100;
    }

    /**
     * Puntos que se devolverían con ese porcentaje (para compras pagadas con puntos).
     */
    public int calcularReembolsoPuntos(Compra compra, double porcentaje) {
        return (int) Math.round(compra.getPuntosRedimidos() * porcentaje / 100);
    }

    public void cancelarCompra(Cliente cliente, Compra compra) {
        facade.cancelarCompraCliente(cliente.getCedula(), compra.getId());
    }

    public double consultarSaldo(Cliente cliente) {
        return facade.consultarSaldo(cliente.getCedula());
    }

    public int consultarPuntos(Cliente cliente) {
        return facade.consultarPuntos(cliente.getCedula());
    }
}
