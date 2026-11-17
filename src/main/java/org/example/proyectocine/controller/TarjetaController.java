package org.example.proyectocine.controller;

import org.example.proyectocine.model.AcumulacionPunto;
import org.example.proyectocine.model.CineFacade;
import org.example.proyectocine.model.Cliente;
import org.example.proyectocine.model.MovimientoSaldo;
import org.example.proyectocine.model.RegistroPuntos;
import org.example.proyectocine.model.TarjetaVirtual;
import org.example.proyectocine.model.Usuario;

import java.util.ArrayList;
import java.util.Comparator;

public class TarjetaController {

    private final CineFacade facade;

    public TarjetaController() {
        this.facade = new CineFacade();
    }

    /**
     * Devuelve el cliente que tiene la sesión abierta.
     *
     * @return el cliente, o null si no hay sesión o quien la tiene no es cliente
     */
    public Cliente obtenerClienteEnSesion() {
        Usuario usuario = facade.obtenerUsuarioActual();
        return usuario instanceof Cliente ? (Cliente) usuario : null;
    }

    public TarjetaVirtual obtenerTarjeta(Cliente cliente) {
        return cliente.getTheTarjeta();
    }

    public AcumulacionPunto obtenerPuntos(Cliente cliente) {
        return cliente.getTheAcumulacion();
    }

    /**
     * Devuelve los movimientos de la tarjeta, del más reciente al más antiguo.
     *
     * @param cliente cliente dueño de la tarjeta
     * @return copia de la lista de movimientos en orden inverso
     */
    public ArrayList<MovimientoSaldo> obtenerMovimientosRecientes(Cliente cliente) {
        return facade.obtenerMovimientos(cliente.getCedula());
    }

    /**
     * Devuelve las acumulaciones de puntos, de la más reciente a la más antigua.
     *
     * @param cliente cliente dueño de los puntos
     * @return copia de las acumulaciones
     */
    public ArrayList<RegistroPuntos> obtenerHistorialPuntos(Cliente cliente) {
        ArrayList<RegistroPuntos> registros = facade.obtenerHistorialPuntos(cliente.getCedula());
        registros.sort(Comparator.comparing(RegistroPuntos::getFechaObtencion).reversed());
        return registros;
    }
}
