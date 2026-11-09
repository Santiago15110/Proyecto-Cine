package org.example.proyectocine.model;

/**
 * Cliente del cine.
 * Al crearse, se le asignan automáticamente su {@link TarjetaVirtual}
 * y su {@link AcumulacionPunto} (relación de composición).
 */
public class Cliente extends Usuario {

    /**
     * Cédula del cliente; es única entre los clientes.
     */
    private String cedula;

    /**
     * Tarjeta virtual del cliente, creada junto con él.
     */
    private TarjetaVirtual theTarjeta;

    /**
     * Acumulación de puntos del cliente, creada junto con él.
     */
    private AcumulacionPunto theAcumulacion;

    /**
     * Crea un cliente junto con su tarjeta virtual y su acumulación de puntos.
     *
     * @param nombre nombre completo del cliente
     * @param correo correo electrónico del cliente
     * @param clave  contraseña del cliente
     * @param cedula cédula del cliente
     */
    public Cliente(String nombre, String correo, String clave, String cedula) {
        super(nombre, correo, clave);
        this.cedula = cedula;
        this.theTarjeta = new TarjetaVirtual(this);
        this.theAcumulacion = new AcumulacionPunto(this);
    }

    /**
     * Obtiene la cédula del cliente.
     *
     * @return la cédula
     */
    public String getCedula() {
        return cedula;
    }

    /**
     * Obtiene la tarjeta virtual del cliente.
     *
     * @return la tarjeta virtual
     */
    public TarjetaVirtual getTheTarjeta() {
        return theTarjeta;
    }

    /**
     * Obtiene la acumulación de puntos del cliente.
     *
     * @return la acumulación de puntos
     */
    public AcumulacionPunto getTheAcumulacion() {
        return theAcumulacion;
    }
}
