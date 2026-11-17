package org.example.proyectocine.controller;

import org.example.proyectocine.model.AcumulacionPunto;
import org.example.proyectocine.model.CineFacade;
import org.example.proyectocine.model.Cliente;
import org.example.proyectocine.model.MovimientoSaldo;
import org.example.proyectocine.model.RegistroPuntos;
import org.example.proyectocine.model.SolicitudRecarga;
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

    /**
     * Convierte lo que escribió el cliente en un monto. Acepta "50000", "50.000" o "$ 50.000".
     *
     * @param texto monto escrito
     * @return el monto
     * @throws IllegalArgumentException si está vacío, no es un número o no es mayor que cero
     */
    public double convertirMonto(String texto) {
        String limpio = texto == null ? "" : texto.replace("$", "").replace(".", "")
                .replace(",", "").replace(" ", "").trim();
        if (limpio.isEmpty()) {
            throw new IllegalArgumentException("Escribe el monto que quieres recargar");
        }
        if (!limpio.matches("\\d+")) {
            throw new IllegalArgumentException("El monto debe ser un número entero, por ejemplo 50000");
        }
        double monto = Double.parseDouble(limpio);
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto a recargar debe ser mayor a 0");
        }
        return monto;
    }

    public SolicitudRecarga solicitarRecarga(Cliente cliente, double monto) {
        return facade.solicitarRecarga(cliente.getCedula(), monto);
    }

    /**
     * Solicitudes de recarga del cliente, de la más reciente a la más antigua.
     */
    public ArrayList<SolicitudRecarga> obtenerSolicitudes(Cliente cliente) {
        return facade.obtenerSolicitudesCliente(cliente.getCedula());
    }
}
