package org.example.proyectocine.controller;

import org.example.proyectocine.model.CineFacade;
import org.example.proyectocine.model.Cliente;

import java.util.ArrayList;

public class AdminClientesController {

    private final CineFacade facade;

    public AdminClientesController() {
        this.facade = new CineFacade();
    }

    public ArrayList<Cliente> obtenerClientes() {
        return facade.obtenerClientes();
    }

    public void recargarTarjeta(String cedula, double monto) {
        facade.recargarTarjeta(cedula, monto);
    }

    public void bloquearTarjeta(String cedula) {
        facade.bloquearTarjeta(cedula);
    }

    public void activarTarjeta(String cedula) {
        facade.activarTarjeta(cedula);
    }
}