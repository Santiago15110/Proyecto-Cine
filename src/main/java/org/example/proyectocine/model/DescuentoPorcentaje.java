package org.example.proyectocine.model;

public class DescuentoPorcentaje extends PrecioDecorator{
    private final double porcentaje;

    /**
     * Crea una capa de descuento sobre un precio
     * @param envuelto precio al que se le aplica el descuento
     * @param porcentaje porcentaje de descuento, mayor que 0 y hasta 100
     */
    public DescuentoPorcentaje(ComponentePrecio envuelto, double porcentaje) {
        super(envuelto);
        if (porcentaje <= 0 || porcentaje > 100) {
            throw new IllegalArgumentException("El porcentaje debe ser mayor que 0 y máximo 100");
        }
        this.porcentaje = porcentaje;
    }

    /**
     * Calcula el precio envuelto menos el porcentaje de descuento
     * @return precio con el descuento aplicado
     */
    @Override
    public double calcular() {
        double base = envuelto.calcular();
        return base - base * porcentaje / 100;
    }

    @Override
    public String getDescripcion() {
        return envuelto.getDescripcion() + " - Descuento " + porcentaje + "%";
    }

    public double getPorcentaje() {
        return porcentaje;
    }
}
