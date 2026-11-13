package org.example.proyectocine.model;

import java.util.Objects;

public class Entrada implements Redimible {

    private static final int COSTO_PUNTOS = 150;

    private final String id;
    private final Funcion funcion;
    private final Asiento asiento;
    private boolean pagadaConPuntos;

    /**
     * Crea una entrada para un asiento de una función. No reserva el asiento:
     * la reserva se hace al confirmar la compra
     * @param id identificador de la entrada
     * @param funcion función a la que pertenece
     * @param asiento asiento de la entrada
     */
    public Entrada(String id, Funcion funcion, Asiento asiento) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id de la entrada es obligatorio");
        }
        if (funcion == null || asiento == null) {
            throw new IllegalArgumentException("La función y el asiento son obligatorios");
        }
        this.id = id;
        this.funcion = funcion;
        this.asiento = asiento;
        this.pagadaConPuntos = false;
    }

    /**
     * Obtiene el precio de la entrada
     * @return precio base de la función
     */
    public double getPrecio() {
        return funcion.getPrecioBase();
    }

    /**
     * Obtiene el costo en puntos para redimir la entrada
     * @return 150 puntos
     */
    @Override
    public int getCostoPuntos() {
        return COSTO_PUNTOS;
    }

    public String getId() {
        return id;
    }

    public Funcion getFuncion() {
        return funcion;
    }

    public Asiento getAsiento() {
        return asiento;
    }

    public boolean isPagadaConPuntos() {
        return pagadaConPuntos;
    }

    public void setPagadaConPuntos(boolean pagadaConPuntos) {
        this.pagadaConPuntos = pagadaConPuntos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Entrada)) {
            return false;
        }
        return id.equals(((Entrada) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return funcion.getPelicula().getTitulo() + " - " + asiento.getCodigo();
    }
}