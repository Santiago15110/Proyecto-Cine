package org.example.proyectocine.model;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Combo de la confitería (compuesto del patrón Composite).
 * Contiene productos u otros combos, y su precio se calcula sumando el de sus ítems
 * y aplicando un porcentaje de descuento.
 */
public class Combo implements ItemConfiteria {

    private final String id;
    private String nombre;
    private double descuento;
    private final ArrayList<ItemConfiteria> listItems = new ArrayList<>();

    /**
     * Crea un combo vacío; los ítems se agregan con {@link #agregar(ItemConfiteria)}.
     *
     * @param id        identificador del combo
     * @param nombre    nombre del combo
     * @param descuento porcentaje de descuento sobre la suma de sus ítems (0 a 100)
     */
    public Combo(String id, String nombre, double descuento) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id del combo es obligatorio");
        }
        validarNombre(nombre);
        validarDescuento(descuento);
        this.id = id;
        this.nombre = nombre;
        this.descuento = descuento;
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del combo es obligatorio");
        }
    }

    private void validarDescuento(double descuento) {
        if (descuento < 0 || descuento >= 100) {
            throw new IllegalArgumentException("El descuento del combo debe estar entre 0 y 100");
        }
    }

    /**
     * Agrega un producto u otro combo. Se puede agregar el mismo producto varias veces
     * (por ejemplo, 2 crispetas).
     *
     * @param item ítem a agregar
     * @throws IllegalArgumentException si el ítem es nulo o si generaría un ciclo
     *                                  (el combo dentro de sí mismo)
     */
    public void agregar(ItemConfiteria item) {
        if (item == null) {
            throw new IllegalArgumentException("El ítem es obligatorio");
        }
        if (item == this || (item instanceof Combo && ((Combo) item).contiene(this))) {
            throw new IllegalArgumentException("Un combo no puede contenerse a sí mismo");
        }
        listItems.add(item);
    }

    public void eliminar(ItemConfiteria item) {
        listItems.remove(item);
    }

    /**
     * Indica si el ítem está dentro de este combo, buscando también dentro de los combos internos.
     *
     * @param item ítem a buscar
     * @return true si el combo lo contiene directa o indirectamente
     */
    public boolean contiene(ItemConfiteria item) {
        for (ItemConfiteria i : listItems) {
            if (i == item) {
                return true;
            }
            if (i instanceof Combo && ((Combo) i).contiene(item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calcula el precio sumando el de cada ítem (si un ítem es otro combo,
     * él calcula el suyo) y aplicando el descuento.
     *
     * @return precio del combo
     */
    @Override
    public double getPrecio() {
        double suma = 0;
        for (ItemConfiteria item : listItems) {
            suma += item.getPrecio();
        }
        return suma - suma * descuento / 100;
    }

    /**
     * Calcula los puntos para redimir el combo con el mismo descuento que el precio.
     *
     * @return costo en puntos del combo
     */
    @Override
    public int getCostoPuntos() {
        int suma = 0;
        for (ItemConfiteria item : listItems) {
            suma += item.getCostoPuntos();
        }
        return (int) Math.round(suma - suma * descuento / 100);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        validarNombre(nombre);
        this.nombre = nombre;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        validarDescuento(descuento);
        this.descuento = descuento;
    }

    public ArrayList<ItemConfiteria> getListItems() {
        return new ArrayList<>(listItems);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Combo)) {
            return false;
        }
        return id.equals(((Combo) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return nombre;
    }
}
