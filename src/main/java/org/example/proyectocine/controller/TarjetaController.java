package org.example.proyectocine.controller;

import org.example.proyectocine.model.AcumulacionPunto;
import org.example.proyectocine.model.CineFacade;
import org.example.proyectocine.model.Cliente;
import org.example.proyectocine.model.MovimientoSaldo;
import org.example.proyectocine.model.TarjetaVirtual;

import java.util.ArrayList;

/**
 * Intermediario entre la pantalla de la tarjeta virtual y el modelo.
 * No usa nada de JavaFX: recibe datos simples y trabaja con {@link CineFacade}.
 */
public class TarjetaController {

    private final CineFacade facade;

    public TarjetaController() {
        this.facade = new CineFacade();
    }

    /**
     * Busca el cliente dueño de la tarjeta.
     *
     * @param cedula cédula del cliente
     * @return el cliente encontrado
     * @throws org.example.proyectocine.model.UsuarioNoEncontradoException si no existe un cliente con esa cédula
     */
    public Cliente buscarCliente(String cedula) {
        return facade.buscarCliente(cedula);
    }

    /**
     * Recarga la tarjeta virtual de un cliente. La tarjeta registra el movimiento.
     *
     * @param cliente cliente dueño de la tarjeta
     * @param monto   valor a recargar
     * @throws IllegalArgumentException si el monto es menor o igual a 0
     */
    public void recargar(Cliente cliente, double monto) {
        facade.recargarTarjeta(cliente.getCedula(), monto);
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
}
