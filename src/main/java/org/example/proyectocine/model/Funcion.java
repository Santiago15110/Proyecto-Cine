package org.example.proyectocine.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Funcion implements Sujeto {

    private final String id;
    private final Pelicula pelicula;
    private final Sala sala;
    private final LocalDateTime fechaHora;
    private final int duracionMin;
    private final double precioBase;
    private boolean cancelada;
    private final Set<Asiento> asientosOcupados;
    private final ArrayList<Observador> listObservadores = new ArrayList<>();

    /**
     * Crea una función activa, sin asientos ocupados
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
        this.cancelada = false;
        this.asientosOcupados = new HashSet<>();
    }

    /**
     * Verifica si un asiento se puede vender en esta función
     * @param a asiento a consultar
     * @return true si está disponible
     */
    public boolean estaDisponible(Asiento a) {
        return a != null
                && !cancelada
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
     * Cancela la función
     * @throws CancelacionNoPermitidaException si ya estaba cancelada
     */
    public void cancelar() {
        if (cancelada) {
            throw new CancelacionNoPermitidaException("La función " + id + " ya está cancelada");
        }
        cancelada = true;
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

    public boolean isCancelada() {
        return cancelada;
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



    @Override
    public void agregarObservador(Observador o){
        if(!listObservadores.contains(o)){
            listObservadores.add(o);
        }
    }


    @Override
    public void eliminarObservador(Observador o){
        listObservadores.remove(o);
    }


    @Override
    public void notificarObservador(TipoEvento evento, Object dato){
        for(Observador o: listObservadores){
            o.actualizar(evento,dato);
        }
    }

}
