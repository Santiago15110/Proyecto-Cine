package org.example.proyectocine.controller;

import org.example.proyectocine.model.Admin;
import org.example.proyectocine.model.CineFacade;
import org.example.proyectocine.model.Compra;
import org.example.proyectocine.model.EstadoFuncion;
import org.example.proyectocine.model.EstadoPagada;
import org.example.proyectocine.model.Funcion;
import org.example.proyectocine.model.Usuario;

/**
 * Intermediario entre el panel principal del administrador y el modelo.
 * Calcula las estadísticas básicas que se muestran al entrar.
 * No usa nada de JavaFX: trabaja con {@link CineFacade}.
 */
public class PanelAdminController {

    private final CineFacade facade;

    public PanelAdminController() {
        this.facade = new CineFacade();
    }

    /**
     * Devuelve el administrador que tiene la sesión abierta.
     *
     * @return el administrador, o null si quien tiene la sesión no es administrador
     */
    public Admin obtenerAdminEnSesion() {
        Usuario usuario = facade.obtenerUsuarioActual();
        return usuario instanceof Admin ? (Admin) usuario : null;
    }

    public int contarClientes() {
        return facade.obtenerClientes().size();
    }

    public int contarPeliculasEnCartelera() {
        return facade.obtenerPeliculasEnCartelera().size();
    }

    public int contarFuncionesProgramadas() {
        int total = 0;
        for (Funcion f : facade.obtenerFunciones()) {
            if (f.getEstado() == EstadoFuncion.PROGRAMADA) {
                total++;
            }
        }
        return total;
    }

    /**
     * Suma el dinero de las compras pagadas con saldo (sin las canceladas ni las pagadas con puntos).
     *
     * @return total vendido
     */
    public double calcularVentasTotales() {
        double total = 0;
        for (Compra c : facade.obtenerCompras()) {
            if (c.getEstado() instanceof EstadoPagada && !c.isPagadaConPuntos()) {
                total += c.getTotal();
            }
        }
        return total;
    }

    public void cerrarSesion() {
        facade.cerrarSesion();
    }
}
