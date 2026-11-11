package org.example.proyectocine.model;

import javafx.beans.binding.ObjectExpression;

import java.util.ArrayList;

public class Cliente extends Usuario implements Observador {


    private String cedula;


    private TarjetaVirtual theTarjeta;


    private AcumulacionPunto theAcumulacion;
    private final ArrayList<String> listNotificaciones = new ArrayList<>();

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


    @Override
    public void actualizar(TipoEvento evento, Object dato){
        if(evento == TipoEvento.FUNCION_CANCELADA){
            Funcion f = (Funcion) dato;
            listNotificaciones.add("La funcion de " + f.getPelicula().getTitulo() + " del " + f.getFechaHora() + " fue cancelada. se reembolso tu dinero");
        }
    }

    public ArrayList<String> getListNotificaciones() {
        return listNotificaciones;
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
