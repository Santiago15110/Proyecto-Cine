package org.example.proyectocine;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sala {

    private static final int MAX_FILAS = 26;

    private final String id;
    private String nombre;
    private final int filas;
    private final int columnas;
    private final List<Asiento> asientos;

    /**
     * Crea una sala y genera todos sus asientos
     * @param id identificador de la sala
     * @param nombre nombre de la sala
     * @param filas cantidad de filas (1 a 26)
     * @param columnas asientos por fila
     */
    public Sala(String id, String nombre, int filas, int columnas) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id de la sala es obligatorio");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la sala es obligatorio");
        }
        if (filas < 1 || filas > MAX_FILAS) {
            throw new IllegalArgumentException("Las filas deben estar entre 1 y " + MAX_FILAS);
        }
        if (columnas < 1) {
            throw new IllegalArgumentException("Las columnas deben ser mayores que cero");
        }
        this.id = id;
        this.nombre = nombre;
        this.filas = filas;
        this.columnas = columnas;
        this.asientos = new ArrayList<>();
        generarAsientos();
    }

    private void generarAsientos() {
        for (int f = 0; f < filas; f++) {
            for (int c = 1; c <= columnas; c++) {
                asientos.add(new Asiento((char) ('A' + f), c));
            }
        }
    }

    /**
     * Calcula la capacidad de la sala
     * @return cantidad de asientos habilitados
     */
    public int getCapacidad() {
        int capacidad = 0;
        for (Asiento a : asientos) {
            if (a.isHabilitado()) {
                capacidad++;
            }
        }
        return capacidad;
    }

    /**
     * Busca un asiento por su fila y número
     * @param fila letra de la fila
     * @param num número del asiento
     * @return el asiento encontrado
     */
    public Asiento buscarAsiento(char fila, int num) {
        char filaBuscada = Character.toUpperCase(fila);
        for (Asiento a : asientos) {
            if (a.getFila() == filaBuscada && a.getNumero() == num) {
                return a;
            }
        }
        throw new IllegalArgumentException(
                "La sala " + nombre + " no tiene el asiento " + filaBuscada + num);
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la sala es obligatorio");
        }
        this.nombre = nombre;
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public List<Asiento> getAsientos() {
        return Collections.unmodifiableList(asientos);
    }

    @Override
    public String toString() {
        return nombre + " (capacidad: " + getCapacidad() + ")";
    }
}
