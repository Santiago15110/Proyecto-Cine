package org.example.proyectocine.controller;

import org.example.proyectocine.model.AcumulacionPunto;
import org.example.proyectocine.model.Cine;
import org.example.proyectocine.model.Cliente;
import org.example.proyectocine.model.MovimientoSaldo;
import org.example.proyectocine.model.TarjetaVirtual;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Intermediario entre la pantalla de la tarjeta virtual y el modelo.
 * No usa nada de JavaFX: recibe datos simples y trabaja con {@link Cine}.
 */
public class TarjetaController {

    private final Cine cine;

    public TarjetaController() {
        this.cine = Cine.getInstancia();
    }

    /**
     * Busca el cliente dueño de la tarjeta.
     *
     * @param cedula cédula del cliente
     * @return el cliente encontrado
     * @throws IllegalArgumentException si no existe un cliente con esa cédula
     */
    public Cliente buscarCliente(String cedula) {
        Cliente cliente = cine.buscarClientePorCedula(cedula);
        if (cliente == null) {
            throw new IllegalArgumentException("No existe un cliente con la cédula " + cedula);
        }
        return cliente;
    }

    /**
     * Recarga la tarjeta virtual de un cliente. La tarjeta registra el movimiento.
     *
     * @param cliente cliente dueño de la tarjeta
     * @param monto   valor a recargar
     * @throws IllegalArgumentException si el monto es menor o igual a 0
     */
    public void recargar(Cliente cliente, double monto) {
        cliente.getTheTarjeta().recargar(monto);
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
        ArrayList<MovimientoSaldo> movimientos = new ArrayList<>(cliente.getTheTarjeta().getListMovimientos());
        Collections.reverse(movimientos);
        return movimientos;
    }
}
