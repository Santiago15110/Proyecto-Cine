package org.example.proyectocine;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Compra {

    private final String id;
    private final Cliente cliente;
    private final LocalDateTime fecha;
    private final List<Entrada> entradas;
    private final List<LineaProducto> lineas;
    private ComponentePrecio precio;
    private double total;
    private EstadoCompra estado;

    /**
     * Crea una compra en estado pendiente. Debe tener al menos una entrada o un producto
     * @param id identificador de la compra
     * @param cliente cliente que realiza la compra
     * @param fecha fecha y hora de la compra
     * @param entradas entradas de la compra (puede ser vacía si solo hay productos)
     * @param lineas productos o combos de la compra (puede ser vacía si solo hay entradas)
     */
    public Compra(String id, Cliente cliente, LocalDateTime fecha,
                  List<Entrada> entradas, List<LineaProducto> lineas) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id de la compra es obligatorio");
        }
        if (cliente == null || fecha == null) {
            throw new IllegalArgumentException("El cliente y la fecha son obligatorios");
        }
        this.entradas = (entradas == null) ? new ArrayList<>() : new ArrayList<>(entradas);
        this.lineas = (lineas == null) ? new ArrayList<>() : new ArrayList<>(lineas);
        if (this.entradas.isEmpty() && this.lineas.isEmpty()) {
            throw new IllegalArgumentException("La compra debe tener al menos una entrada o un producto");
        }
        this.id = id;
        this.cliente = cliente;
        this.fecha = fecha;
        this.estado = new EstadoPendiente();
    }

    /**
     * Obtiene la función de la compra a partir de sus entradas
     * @return la función de las entradas, o null si la compra solo tiene productos
     */
    public Funcion getFuncion() {
        return entradas.isEmpty() ? null : entradas.get(0).getFuncion();
    }

    /**
     * Calcula el total a pagar aplicando las promociones y cargos del precio
     * @return total de la compra
     */
    public double calcularTotal() {
        if (precio == null) {
            throw new IllegalStateException("La compra " + id + " no tiene precio asignado");
        }
        total = precio.calcular();
        return total;
    }

    /**
     * Confirma la compra; el comportamiento depende del estado actual
     */
    public void confirmar() {
        estado.confirmar(this);
    }

    /**
     * Cancela la compra; el comportamiento depende del estado actual
     */
    public void cancelar() {
        estado.cancelar(this);
    }

    /**
     * Cambia el estado de la compra (lo usan los propios estados al hacer la transición)
     * @param e nuevo estado
     */
    public void setEstado(EstadoCompra e) {
        if (e == null) {
            throw new IllegalArgumentException("El estado de la compra es obligatorio");
        }
        this.estado = e;
    }

    public String getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public List<Entrada> getEntradas() {
        return Collections.unmodifiableList(entradas);
    }

    public List<LineaProducto> getLineas() {
        return Collections.unmodifiableList(lineas);
    }

    public ComponentePrecio getPrecio() {
        return precio;
    }

    public void setPrecio(ComponentePrecio precio) {
        this.precio = precio;
    }

    public double getTotal() {
        return total;
    }

    public EstadoCompra getEstado() {
        return estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Compra)) {
            return false;
        }
        return id.equals(((Compra) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Compra " + id + " - " + estado.getNombre() + " - $" + total;
    }
}