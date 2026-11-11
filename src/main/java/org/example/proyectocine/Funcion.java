package org.example.proyectocine;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Funcion {

    private final String id;
    private final Pelicula pelicula;
    private final Sala sala;
    private final LocalDateTime fechaHora;
    private final int duracionMin;
    private final double precioBase;
    private EstadoFuncion estado;
    private final Set<Asiento> asientosOcupados;

    /**
     * Crea una función programada, sin asientos ocupados
     * @param id identificador de la función
     * @param pelicula película que se proyecta
     * @param sala sala de proyección
     * @param fechaHora fecha y hora de inicio
     * @param duracionMin duración de la función en minutos
     * @param precioBase precio base de la entrada
     */
    public Funcion(String id, Pelicula pelicula, Sala sala, LocalDateTime fechaHora,
                   int duracionMin, double precioBase) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id de la función es obligatorio");
        }
        if (pelicula == null || sala == null || fechaHora == null) {
            throw new IllegalArgumentException("La película, la sala y la fecha y hora son obligatorias");
        }
        if (duracionMin <= 0) {
            throw new IllegalArgumentException("La duración debe ser mayor que cero");
        }
        if (precioBase < 0) {
            throw new IllegalArgumentException("El precio base no puede ser negativo");
        }
        this.id = id;
        this.pelicula = pelicula;
        this.sala = sala;
        this.fechaHora = fechaHora;
        this.duracionMin = duracionMin;
        this.precioBase = precioBase;
        this.estado = EstadoFuncion.PROGRAMADA;
        this.asientosOcupados = new HashSet<>();
    }

    /**
     * Verifica si un asiento se puede vender en esta función
     * @param a asiento a consultar
     * @return true si está disponible
     */
    public boolean estaDisponible(Asiento a) {
        return a != null
                && estado == EstadoFuncion.PROGRAMADA
                && a.isHabilitado()
                && sala.getAsientos().contains(a)
                && !asientosOcupados.contains(a);
    }

    /**
     * Reserva un asiento para esta función
     * @param a asiento a reservar
     * @throws AsientoNoDisponibleException si el asiento no está disponible
     */
    public void reservarAsiento(Asiento a) {
        if (!estaDisponible(a)) {
            String codigo = (a == null) ? "nulo" : a.getCodigo();
            throw new AsientoNoDisponibleException(
                    "El asiento " + codigo + " no está disponible para la función " + id);
        }
        asientosOcupados.add(a);
    }

    /**
     * Libera un asiento reservado (por ejemplo, al cancelar una compra)
     * @param a asiento a liberar
     */
    public void liberarAsiento(Asiento a) {
        asientosOcupados.remove(a);
    }

    /**
     * Calcula las horas completas que faltan para la función
     * @param ahora momento de referencia
     * @return horas restantes, truncadas
     */
    public long horasRestantes(LocalDateTime ahora) {
        return Duration.between(ahora, fechaHora).toHours();
    }

    /**
     * Calcula el porcentaje de ocupación de la función
     * @return porcentaje de 0 a 100
     */
    public double getOcupacion() {
        int capacidad = sala.getCapacidad();
        if (capacidad == 0) {
            return 0;
        }
        return asientosOcupados.size() * 100.0 / capacidad;
    }

    /**
     * Cancela la función si está programada
     * @throws CancelacionNoPermitidaException si ya está cancelada o finalizada
     */
    public void cancelar() {
        if (estado != EstadoFuncion.PROGRAMADA) {
            throw new CancelacionNoPermitidaException(
                    "La función " + id + " no se puede cancelar porque está " + estado);
        }
        estado = EstadoFuncion.CANCELADA;
    }

    public String getId() {
        return id;
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    public Sala getSala() {
        return sala;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public int getDuracionMin() {
        return duracionMin;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public EstadoFuncion getEstado() {
        return estado;
    }

    public Set<Asiento> getAsientosOcupados() {
        return Collections.unmodifiableSet(asientosOcupados);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Funcion)) {
            return false;
        }
        return id.equals(((Funcion) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return pelicula.getTitulo() + " - " + sala.getNombre() + " - " + fechaHora;
    }
}