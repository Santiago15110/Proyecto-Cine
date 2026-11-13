package org.example.proyectocine.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CompraBuilder {

    private Cliente cliente;
    private final List<Entrada> entradas = new ArrayList<>();
    private final List<LineaProducto> lineas = new ArrayList<>();
    private final List<Promocion> promociones = new ArrayList<>();

    /**
     * Define el cliente que realiza la compra
     * @param c cliente de la compra
     * @return el mismo builder, para encadenar llamadas
     */
    public CompraBuilder paraCliente(Cliente c) {
        if (c == null) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
        this.cliente = c;
        return this;
    }

    /**
     * Agrega una entrada a la compra. Todas las entradas deben ser de la misma función
     * y su asiento debe estar disponible
     * @param e entrada a agregar
     * @return el mismo builder, para encadenar llamadas
     * @throws AsientoNoDisponibleException si el asiento está ocupado o ya está en esta compra
     */
    public CompraBuilder agregarEntrada(Entrada e) {
        if (e == null) {
            throw new IllegalArgumentException("La entrada es obligatoria");
        }
        if (!entradas.isEmpty() && !entradas.get(0).getFuncion().equals(e.getFuncion())) {
            throw new IllegalArgumentException("Todas las entradas de una compra deben ser de la misma función");
        }
        if (!e.getFuncion().estaDisponible(e.getAsiento())) {
            throw new AsientoNoDisponibleException(
                    "El asiento " + e.getAsiento().getCodigo() + " no está disponible");
        }
        for (Entrada existente : entradas) {
            if (existente.getAsiento().equals(e.getAsiento())) {
                throw new AsientoNoDisponibleException(
                        "El asiento " + e.getAsiento().getCodigo() + " ya está en la compra");
            }
        }
        entradas.add(e);
        return this;
    }

    /**
     * Agrega un producto o combo de confitería a la compra
     * @param i producto o combo
     * @param cant cantidad de unidades, mayor que cero
     * @return el mismo builder, para encadenar llamadas
     */
    public CompraBuilder agregarProducto(ItemConfiteria i, int cant) {
        lineas.add(new LineaProducto(i, cant));
        return this;
    }

    /**
     * Agrega una promoción que se aplicará al precio de la compra
     * @param p promoción a aplicar
     * @return el mismo builder, para encadenar llamadas
     */
    public CompraBuilder aplicarPromocion(Promocion p) {
        if (p == null) {
            throw new IllegalArgumentException("La promoción es obligatoria");
        }
        if (promociones.contains(p)) {
            throw new IllegalArgumentException("La promoción ya fue aplicada a esta compra");
        }
        promociones.add(p);
        return this;
    }

    /**
     * Construye la compra en estado pendiente. Arma el precio base y lo envuelve
     * con cada promoción (Decorator), y deja calculado el total
     * @return la compra lista para pagar
     * @throws IllegalArgumentException si falta el cliente o la compra no tiene entradas ni productos
     */
    public Compra build() {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente es obligatorio para crear la compra");
        }
        String id = "C-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Compra compra = new Compra(id, cliente, LocalDateTime.now(), entradas, lineas);

        ComponentePrecio precio = new PrecioBase(compra);
        for (Promocion p : promociones) {
            precio = p.aplicar(precio);
        }
        compra.setPrecio(precio);
        compra.calcularTotal();
        return compra;
    }
}
