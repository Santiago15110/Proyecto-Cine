package org.example.proyectocine.model;

import java.util.ArrayList;
import java.util.List;

public class EstadoPendiente implements EstadoCompra {

    /**
     * Reserva los asientos de todas las entradas y pasa la compra a pagada.
     * Si algún asiento no está disponible, libera los ya reservados y la compra sigue pendiente
     * @param c compra a confirmar
     * @throws AsientoNoDisponibleException si algún asiento ya no está disponible
     */
    @Override
    public void confirmar(Compra c) {
        List<Entrada> reservadas = new ArrayList<>();
        try {
            for (Entrada e : c.getEntradas()) {
                e.getFuncion().reservarAsiento(e.getAsiento());
                reservadas.add(e);
            }
        } catch (AsientoNoDisponibleException ex) {
            for (Entrada e : reservadas) {
                e.getFuncion().liberarAsiento(e.getAsiento());
            }
            throw ex;
        }
        c.setEstado(new EstadoPagada());
    }

    /**
     * Cancela la compra pendiente. No hay asientos reservados ni dinero que devolver
     * @param c compra a cancelar
     */
    @Override
    public void cancelar(Compra c) {
        c.setEstado(new EstadoCancelada());
    }

    @Override
    public String getNombre() {
        return "Pendiente";
    }
}