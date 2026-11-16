package org.example.proyectocine.controller;

import org.example.proyectocine.model.CineFacade;
import org.example.proyectocine.model.Funcion;
import org.example.proyectocine.model.Genero;
import org.example.proyectocine.model.Pelicula;

import java.util.ArrayList;

/**
 * Puente entre la pantalla de cartelera y el modelo.
 */
public class CarteleraController {

    private final CineFacade facade;

    public CarteleraController() {
        this.facade = new CineFacade();
    }

    /**
     * Películas en cartelera que coinciden con el texto buscado y el género.
     *
     * @param texto  parte del título; vacío o null para no filtrar
     * @param genero género buscado; null para todos
     * @return películas que cumplen los filtros
     */
    public ArrayList<Pelicula> buscarPeliculas(String texto, Genero genero) {
        String buscado = (texto == null) ? "" : texto.trim().toLowerCase();
        ArrayList<Pelicula> resultado = new ArrayList<>();
        for (Pelicula p : facade.obtenerPeliculasEnCartelera()) {
            boolean coincideTitulo = buscado.isEmpty() || p.getTitulo().toLowerCase().contains(buscado);
            boolean coincideGenero = genero == null || p.getGenero() == genero;
            if (coincideTitulo && coincideGenero) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    /**
     * Funciones que todavía se pueden comprar de una película, ordenadas por fecha.
     */
    public ArrayList<Funcion> obtenerFunciones(Pelicula pelicula) {
        return facade.obtenerFuncionesDisponibles(pelicula.getId());
    }
}
