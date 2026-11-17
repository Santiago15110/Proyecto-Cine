package org.example.proyectocine.controller;

import org.example.proyectocine.model.Asiento;
import org.example.proyectocine.model.CineFacade;
import org.example.proyectocine.model.Cliente;
import org.example.proyectocine.model.Compra;
import org.example.proyectocine.model.Funcion;
import org.example.proyectocine.model.ItemConfiteria;
import org.example.proyectocine.model.Promocion;
import org.example.proyectocine.model.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PagoController {

    private final CineFacade facade;

    public PagoController() {
        this.facade = new CineFacade();
    }

    public Cliente obtenerClienteEnSesion() {
        Usuario usuario = facade.obtenerUsuarioActual();
        return usuario instanceof Cliente ? (Cliente) usuario : null;
    }

    public ArrayList<ItemConfiteria> obtenerConfiteria() {
        return facade.obtenerConfiteria();
    }

    public ArrayList<Promocion> obtenerPromocionesVigentes() {
        return facade.obtenerPromocionesVigentes();
    }

    public double consultarSaldo(Cliente cliente) {
        return facade.consultarSaldo(cliente.getCedula());
    }

    public int consultarPuntos(Cliente cliente) {
        return facade.consultarPuntos(cliente.getCedula());
    }

    public double cotizar(Cliente cliente, Funcion funcion, List<Asiento> asientos,
                          Map<String, Integer> productos, Promocion promocion) {
        return facade.cotizarCompra(cliente.getCedula(), idFuncion(funcion), codigos(asientos),
                productos, promociones(promocion));
    }

    public int costoEnPuntos(Funcion funcion, List<Asiento> asientos, Map<String, Integer> productos) {
        return facade.consultarCostoEnPuntos(idFuncion(funcion), codigos(asientos), productos);
    }

    public Compra pagarConSaldo(Cliente cliente, Funcion funcion, List<Asiento> asientos,
                                Map<String, Integer> productos, Promocion promocion) {
        return facade.realizarCompra(cliente.getCedula(), idFuncion(funcion), codigos(asientos),
                productos, promociones(promocion));
    }

    public Compra pagarConPuntos(Cliente cliente, Funcion funcion, List<Asiento> asientos,
                                 Map<String, Integer> productos) {
        return facade.realizarCompraConPuntos(cliente.getCedula(), idFuncion(funcion), codigos(asientos), productos);
    }

    private static String idFuncion(Funcion funcion) {
        return funcion == null ? null : funcion.getId();
    }

    private static ArrayList<String> codigos(List<Asiento> asientos) {
        ArrayList<String> codigos = new ArrayList<>();
        for (Asiento a : asientos) {
            codigos.add(a.getCodigo());
        }
        return codigos;
    }

    private static ArrayList<Promocion> promociones(Promocion promocion) {
        ArrayList<Promocion> lista = new ArrayList<>();
        if (promocion != null) {
            lista.add(promocion);
        }
        return lista;
    }
}
