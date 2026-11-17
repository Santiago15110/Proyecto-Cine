package org.example.proyectocine.controller;

import org.example.proyectocine.model.CineFacade;
import org.example.proyectocine.model.Combo;
import org.example.proyectocine.model.ItemConfiteria;
import org.example.proyectocine.model.Producto;

import java.util.ArrayList;

public class AdminConfiteriaController {

    private final CineFacade facade;

    public AdminConfiteriaController() {
        this.facade = new CineFacade();
    }

    public ArrayList<ItemConfiteria> obtenerConfiteria() {
        return facade.obtenerConfiteria();
    }

    public Producto crearProducto(String id, String nombre, double precio, int costoPuntos) {
        return facade.crearProducto(id, nombre, precio, costoPuntos);
    }

    public Combo crearCombo(String id, String nombre, double descuento, ArrayList<String> idsItems) {
        return facade.crearCombo(id, nombre, descuento, idsItems);
    }
}