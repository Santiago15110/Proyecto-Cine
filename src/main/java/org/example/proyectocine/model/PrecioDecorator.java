package org.example.proyectocine.model;

public abstract class PrecioDecorator implements ComponentePrecio {
    protected final ComponentePrecio envuelto;

    /**
     * Crea una capa que envuelve otro precio
     * @param envuelto precio al que se le agrega esta capa
     */
    protected PrecioDecorator(ComponentePrecio envuelto) {
        if (envuelto == null) {
            throw new IllegalArgumentException("El precio a envolver es obligatorio");
        }
        this.envuelto = envuelto;
    }

    /**
     * Por defecto delega en el precio envuelto; las subclases le suman su efecto
     * @return precio del componente envuelto
     */
    @Override
    public double calcular() {
        return envuelto.calcular();
    }

    @Override
    public String getDescripcion() {
        return envuelto.getDescripcion();
    }
}
