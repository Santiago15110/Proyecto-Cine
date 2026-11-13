package org.example.proyectocine.model;

public class EstadoPagada implements EstadoCompra {

    /**
     * Una compra pagada no se puede confirmar de nuevo
     * @param c compra a confirmar
     * @throws IllegalStateException siempre
     */
    @Override
    public void confirmar(Compra c) {
        throw new IllegalStateException("La compra " + c.getId() + " ya está pagada");
    }

    /**
     * Libera los asientos de las entradas y pasa la compra a cancelada.
     * El cálculo y la devolución del reembolso los hace ServicioCompra
     * @param c compra a cancelar
     */
    @Override
    public void cancelar(Compra c) {
        for (Entrada e : c.getEntradas()) {
            e.getFuncion().liberarAsiento(e.getAsiento());
        }
        c.setEstado(new EstadoCancelada());
    }

    @Override
    public String getNombre() {
        return "Pagada";
    }
}