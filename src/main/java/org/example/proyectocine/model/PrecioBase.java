package org.example.proyectocine.model;

public class PrecioBase implements ComponentePrecio{
    private final Compra compra;

    /**
     * Crea el precio base de una compra, que será el centro de las capas del Decorator
     * @param compra compra de la que se calcula el precio
     */
    public PrecioBase(Compra compra) {
        if (compra == null) {
            throw new IllegalArgumentException("La compra es obligatoria");
        }
        this.compra = compra;
    }

    /**
     * Suma el precio de las entradas y el subtotal de los productos, sin descuentos ni cargos
     * @return precio base de la compra
     */
    @Override
    public double calcular() {
        double total = 0;
        for (Entrada e : compra.getEntradas()) {
            total += e.getPrecio();
        }
        for (LineaProducto l : compra.getLineas()) {
            total += l.getSubtotal();
        }
        return total;
    }

    @Override
    public String getDescripcion() {
        return "Precio base";
    }
}
