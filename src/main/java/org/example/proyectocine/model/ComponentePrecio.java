package org.example.proyectocine.model;

public interface ComponentePrecio {
    /**
     * Calcula el precio, incluyendo el efecto de las capas que envuelve
     * @return precio calculado
     */
    double calcular();

    /**
     * Describe el precio y las capas aplicadas (por ejemplo "Base + Descuento 10%")
     * @return descripción del precio
     */
    String getDescripcion();
}
