package org.example.proyectocine.model;

public class EstadoCancelada implements EstadoCompra{
    /**
     * Una compra cancelada no se puede confirmar
     * @param c compra a confirmar
     * @throws IllegalStateException siempre
     */
    @Override
    public void confirmar(Compra c) {
        throw new IllegalStateException("La compra " + c.getId() + " está cancelada");
    }

    /**
     * Una compra cancelada no se puede cancelar otra vez; esto evita reembolsos duplicados
     * @param c compra a cancelar
     * @throws CancelacionNoPermitidaException siempre
     */
    @Override
    public void cancelar(Compra c) {
        throw new CancelacionNoPermitidaException("La compra " + c.getId() + " ya está cancelada");
    }

    @Override
    public String getNombre() {
        return "Cancelada";
    }
}
