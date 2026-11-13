package org.example.proyectocine.model;

public class PasarelaLegacy {

    public boolean realizarTransaccionBancaria(String IDCuenta, double monto) {
        System.out.println("Conectando con Banco Externo...");
        System.out.println("Realizando transaccion Bancaria: $"+ monto + " a la cuenta: " + IDCuenta);
        return true;
    }
}
