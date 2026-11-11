package org.example.proyectocine.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class TarjetaVirtual {

    private String numero;
    private double saldo;
    private Cliente theCliente;
    private ArrayList<MovimientoSaldo> listMovimientos;


    public TarjetaVirtual(Cliente theCliente) {
        this.numero = theCliente.getCedula();
        this.theCliente = theCliente;
        this.saldo = 0;
        listMovimientos = new ArrayList<>();
    }

    /**
     * Agrega dinero al saldo de la tarjeta.
     * @param monto valor a recargar
     * @throws IllegalArgumentException si el monto es menor o igual a 0
     */
    public void recargar(double monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto a recargar debe ser mayor a 0");
        }
        saldo += monto;
        listMovimientos.add(new MovimientoSaldo(TipoMovimiento.RECARGA, monto, LocalDate.now(), LocalTime.now(), saldo, "Se realizo una recarga"));
    }




    /**
     * Descuenta dinero del saldo de la tarjeta, por ejemplo al pagar una compra.
     * @param monto valor a descontar
     * @throws IllegalArgumentException si el monto es menor o igual a 0
     * @throws SaldoInsuficienteException si el monto es mayor que el saldo disponible
     */
    public void descontar(double monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto a descontar debe ser mayor a 0");
        }
        if (monto > saldo) {
            throw new SaldoInsuficienteException("Saldo insuficiente");
        }
        saldo -= monto;
        listMovimientos.add(new MovimientoSaldo(TipoMovimiento.PAGO, monto, LocalDate.now(),LocalTime.now(), saldo, "Se realizo un pago"));
    }


    /**
     * Metodo para el reembolso
     * @param monto
     * @param descripcion
     */
    public void reembolsar(double monto, String descripcion){

        if(monto<=0){
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        }

        saldo += monto;
        listMovimientos.add(new MovimientoSaldo(TipoMovimiento.REEMBOLSO, monto, LocalDate.now(), LocalTime.now(), saldo, descripcion));
    }



    public String getNumero() {
        return numero;
    }

    public double getSaldo() {
        return saldo;
    }

    public Cliente getTheCliente() {
        return theCliente;
    }

    public ArrayList<MovimientoSaldo> getListMovimientos() {
        return listMovimientos;
    }
}
