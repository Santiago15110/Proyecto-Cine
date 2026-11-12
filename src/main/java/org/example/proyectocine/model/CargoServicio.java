package org.example.proyectocine.model;

public class CargoServicio extends PrecioDecorator{

    private final double valor;

    /**
     * Crea una capa que suma un cargo fijo por servicio
     * @param envuelto precio al que se le suma el cargo
     * @param valor valor del cargo, no negativo
     */
    public CargoServicio(ComponentePrecio envuelto, double valor) {
        super(envuelto);
        if (valor < 0) {
            throw new IllegalArgumentException("El cargo por servicio no puede ser negativo");
        }
        this.valor = valor;
    }

    /**
     * Calcula el precio envuelto más el cargo por servicio
     * @return precio con el cargo sumado
     */
    @Override
    public double calcular() {
        return envuelto.calcular() + valor;
    }

    @Override
    public String getDescripcion() {
        return envuelto.getDescripcion() + " + Cargo por servicio";
    }

    public double getValor() {
        return valor;
    }
}
