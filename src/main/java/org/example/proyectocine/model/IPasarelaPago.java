package org.example.proyectocine.model;

public interface IPasarelaPago {
    boolean procesarPago(String referenciaPago, double monto);
}
