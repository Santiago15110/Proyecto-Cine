package org.example.proyectocine.model;

/**
 * Tarjeta virtual de un cliente, con un saldo que se puede recargar y usar para pagar.
 * Se crea automáticamente al crear el {@link Cliente} y su número es la cédula del cliente.
 */
public class TarjetaVirtual {

    /**
     * Número de la tarjeta (la cédula del cliente).
     */
    private String numero;

    /**
     * Saldo disponible en la tarjeta.
     */
    private double saldo;

    /**
     * Cliente dueño de la tarjeta.
     */
    private Cliente theCliente;

    /**
     * Crea la tarjeta de un cliente con saldo en 0.
     *
     * @param theCliente cliente dueño de la tarjeta
     */
    public TarjetaVirtual(Cliente theCliente) {
        this.numero = theCliente.getCedula();
        this.theCliente = theCliente;
        this.saldo = 0;
    }

    /**
     * Agrega dinero al saldo de la tarjeta.
     * @param monto valor a recargar
     * @throws IllegalArgumentException si el monto es menor o igual a 0
     */
    public void recargar(double monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto a recargar debe ser mayor a 0");
        }
        saldo += monto;
    }

    /**
     * Descuenta dinero del saldo de la tarjeta, por ejemplo al pagar una compra.
     * @param monto valor a descontar
     * @throws IllegalArgumentException si el monto es menor o igual a 0
     *                                  o si es mayor que el saldo disponible
     */
    public void descontar(double monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto a descontar debe ser mayor a 0");
        }
        if (monto > saldo) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }
        saldo -= monto;
    }

    /**
     * Obtiene el número de la tarjeta.
     *
     * @return el número de la tarjeta
     */
    public String getNumero() {
        return numero;
    }

    /**
     * Obtiene el saldo disponible.
     *
     * @return el saldo actual
     */
    public double getSaldo() {
        return saldo;
    }

    /**
     * Obtiene el cliente dueño de la tarjeta.
     *
     * @return el cliente
     */
    public Cliente getTheCliente() {
        return theCliente;
    }
}
