package org.example.proyectocine.model;

import java.util.Objects;

/**
 * Producto suelto de la confitería (hoja del patrón Composite).
 * Ejemplos: crispetas, gaseosa, perro caliente.
 */
public class Producto implements ItemConfiteria {

    private final String id;
    private String nombre;
    private double precio;
    private int costoPuntos;

    /**
     * Crea un producto de confitería.
     *
     * @param id          identificador del producto
     * @param nombre      nombre del producto
     * @param precio      precio en pesos
     * @param costoPuntos puntos necesarios para redimirlo
     */
    public Producto(String id, String nombre, double precio, int costoPuntos) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id del producto es obligatorio");
        }
        validarNombre(nombre);
        validarPrecio(precio);
        validarCostoPuntos(costoPuntos);
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.costoPuntos = costoPuntos;
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio");
        }
    }

    private void validarPrecio(double precio) {
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio del producto debe ser mayor que cero");
        }
    }

    private void validarCostoPuntos(int costoPuntos) {
        if (costoPuntos < 0) {
            throw new IllegalArgumentException("El costo en puntos no puede ser negativo");
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        validarNombre(nombre);
        this.nombre = nombre;
    }

    @Override
    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        validarPrecio(precio);
        this.precio = precio;
    }

    @Override
    public int getCostoPuntos() {
        return costoPuntos;
    }

    public void setCostoPuntos(int costoPuntos) {
        validarCostoPuntos(costoPuntos);
        this.costoPuntos = costoPuntos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Producto)) {
            return false;
        }
        return id.equals(((Producto) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return nombre;
    }
}
