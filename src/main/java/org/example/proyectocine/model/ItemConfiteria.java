package org.example.proyectocine.model;

/**
 * Componente del patrón Composite de la confitería.
 * Lo implementan {@link Producto} (hoja) y {@link Combo} (compuesto),
 * así el resto del sistema los trata igual.
 */
public interface ItemConfiteria extends Redimible {
    String getId();

    String getNombre();

    double getPrecio();
}
