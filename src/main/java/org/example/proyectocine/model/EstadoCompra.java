package org.example.proyectocine.model;

public interface EstadoCompra {
    /**
     * Confirma el pago de la compra según lo que permita el estado actual
     * @param c compra sobre la que se actúa
     */
    void confirmar(Compra c);

    /**
     * Cancela la compra según lo que permita el estado actual
     * @param c compra sobre la que se actúa
     */
    void cancelar(Compra c);

    String getNombre();
}
