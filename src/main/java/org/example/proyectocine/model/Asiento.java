package org.example.proyectocine.model;
import java.util.Objects;

public class Asiento {

    private final char fila;
    private final int numero;
    private boolean habilitado;

    /**
     * Crea un asiento habilitado
     * @param fila letra de la fila
     * @param numero número dentro de la fila
     */
    public Asiento(char fila, int numero) {
        if (!Character.isLetter(fila)) {
            throw new IllegalArgumentException("La fila debe ser una letra");
        }
        if (numero <= 0) {
            throw new IllegalArgumentException("El número del asiento debe ser mayor que cero");
        }
        this.fila = Character.toUpperCase(fila);
        this.numero = numero;
        this.habilitado = true;
    }

    public String getCodigo() {
        return fila + String.valueOf(numero);
    }

    public char getFila() {
        return fila;
    }

    public int getNumero() {
        return numero;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Asiento)) {
            return false;
        }
        Asiento otro = (Asiento) o;
        return fila == otro.fila && numero == otro.numero;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fila, numero);
    }

    @Override
    public String toString() {
        return getCodigo();
    }
}
