package org.example.proyectocine.model;

public class LineaProducto {
    private final ItemConfiteria item;
    private final int cantidad;

    /**
     * Crea una línea de compra con un producto o combo y su cantidad
     * @param item producto o combo comprado
     * @param cantidad unidades compradas, mayor que cero
     */
    public LineaProducto(ItemConfiteria item, int cantidad) {
        if (item == null) {
            throw new IllegalArgumentException("El producto o combo es obligatorio");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
        this.item = item;
        this.cantidad = cantidad;
    }

    /**
     * Calcula el subtotal de la línea
     * @return precio del item por la cantidad
     */
    public double getSubtotal() {
        return item.getPrecio() * cantidad;
    }

    public ItemConfiteria getItem() {
        return item;
    }

    public int getCantidad() {
        return cantidad;
    }

    @Override
    public String toString() {
        return cantidad + " x " + item.getNombre();
    }
}
