package org.example.proyectocine.model;

public class Cliente extends Usuario {


    private String cedula;


    private TarjetaVirtual theTarjeta;


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


    public String getCedula() {
        return cedula;
    }


    public TarjetaVirtual getTheTarjeta() {
        return theTarjeta;
    }


    public AcumulacionPunto getTheAcumulacion() {
        return theAcumulacion;
    }
}
