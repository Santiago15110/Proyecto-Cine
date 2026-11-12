package org.example.proyectocine.model;

public class PasarelaPagoAdapter implements IPasarelaPago {

    private PasarelaLegacy pasarelaExterna;

    public PasarelaPagoAdapter() {
        this.pasarelaExterna = new PasarelaLegacy();
    }

    @Override
    public boolean procesarPago(String referenciaCuenta, double monto) {
        return pasarelaExterna.realizarTransaccionBancaria(referenciaCuenta, monto);
    }
}
