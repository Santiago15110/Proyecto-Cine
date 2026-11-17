package org.example.proyectocine.controller;

import org.example.proyectocine.model.CineFacade;
import org.example.proyectocine.model.Compra;

import java.util.ArrayList;
import java.util.Collections;

public class AdminComprasController {

    private final CineFacade facade;

    public AdminComprasController() {
        this.facade = new CineFacade();
    }

    public ArrayList<Compra> obtenerCompras() {
        ArrayList<Compra> compras = facade.obtenerCompras();
        Collections.reverse(compras);
        return compras;
    }
}