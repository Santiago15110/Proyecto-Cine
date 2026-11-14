package org.example.proyectocine.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Cuenta de puntos CinemaUQ de un cliente.
 * Cada acumulación se guarda como un {@link RegistroPuntos} con su propia fecha y vence un año
 * después de obtenida. Al redimir se usan primero los puntos más próximos a vencer.
 */
public class AcumulacionPunto {

    private final ArrayList<RegistroPuntos> listRegistros = new ArrayList<>();

    private int totalAcumulados;

    private int totalRedimidos;

    private final Cliente theCliente;

    public AcumulacionPunto(Cliente theCliente) {
        this.theCliente = theCliente;
    }

    /**
     * Suma puntos obtenidos hoy.
     *
     * @param cantidad   puntos a sumar
     * @param referencia de dónde salieron (por ejemplo el id de la compra)
     * @throws IllegalArgumentException si la cantidad es menor o igual a 0
     */
    public void sumarPuntos(int cantidad, String referencia) {
        sumarPuntos(cantidad, referencia, LocalDate.now());
    }

    public void sumarPuntos(int cantidad) {
        sumarPuntos(cantidad, "", LocalDate.now());
    }

    /**
     * Suma puntos indicando la fecha de obtención (sirve para cargar datos iniciales).
     *
     * @param cantidad       puntos a sumar
     * @param referencia     de dónde salieron
     * @param fechaObtencion fecha en que se obtuvieron
     */
    public void sumarPuntos(int cantidad, String referencia, LocalDate fechaObtencion) {
        listRegistros.add(new RegistroPuntos(cantidad, fechaObtencion, referencia));
        totalAcumulados += cantidad;
    }

    /**
     * Redime puntos usando primero los que están más cerca de vencer.
     * Los puntos vencidos no se pueden usar.
     *
     * @param cantidad puntos a redimir
     * @throws IllegalArgumentException     si la cantidad es menor o igual a 0
     * @throws PuntosInsuficientesException si los puntos vigentes no alcanzan
     */
    public void redimirPuntos(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de puntos debe ser mayor a 0");
        }
        int disponibles = getPuntos();
        if (cantidad > disponibles) {
            throw new PuntosInsuficientesException("Puntos insuficientes: tienes " + disponibles
                    + " puntos vigentes y se necesitan " + cantidad);
        }
        int pendiente = cantidad;
        for (RegistroPuntos r : registrosVigentesPorVencimiento()) {
            int usar = Math.min(pendiente, r.getPuntosDisponibles());
            r.usar(usar);
            pendiente -= usar;
            if (pendiente == 0) {
                break;
            }
        }
        totalRedimidos += cantidad;
    }

    /**
     * Anula los puntos que dio una compra que se canceló. Si el cliente ya gastó parte
     * de esos puntos, solo se anulan los que quedan.
     *
     * @param referencia id de la compra
     * @return los puntos anulados
     */
    public int anularPuntosDe(String referencia) {
        int anulados = 0;
        for (RegistroPuntos r : listRegistros) {
            if (referencia != null && referencia.equals(r.getReferencia())) {
                anulados += r.anular();
            }
        }
        totalAcumulados -= anulados;
        return anulados;
    }

    /**
     * Devuelve puntos redimidos (por ejemplo, al cancelar una compra pagada con puntos).
     * Quedan como una acumulación nueva con fecha de hoy.
     *
     * @param cantidad   puntos a devolver
     * @param referencia id de la compra
     */
    public void devolverPuntos(int cantidad, String referencia) {
        if (cantidad <= 0) {
            return;
        }
        listRegistros.add(new RegistroPuntos(cantidad, LocalDate.now(), referencia));
        totalRedimidos -= cantidad;
    }

    /**
     * Obtiene los puntos vigentes (no vencidos) disponibles para redimir.
     *
     * @return puntos disponibles
     */
    public int getPuntos() {
        int total = 0;
        for (RegistroPuntos r : listRegistros) {
            if (!r.estaVencido(LocalDate.now())) {
                total += r.getPuntosDisponibles();
            }
        }
        return total;
    }

    /**
     * Fecha en que vencen los próximos puntos disponibles.
     *
     * @return la fecha, o null si no hay puntos vigentes
     */
    public LocalDate getFechaVencimiento() {
        ArrayList<RegistroPuntos> vigentes = registrosVigentesPorVencimiento();
        return vigentes.isEmpty() ? null : vigentes.get(0).getFechaVencimiento();
    }

    private ArrayList<RegistroPuntos> registrosVigentesPorVencimiento() {
        ArrayList<RegistroPuntos> vigentes = new ArrayList<>();
        for (RegistroPuntos r : listRegistros) {
            if (!r.estaVencido(LocalDate.now()) && r.getPuntosDisponibles() > 0) {
                vigentes.add(r);
            }
        }
        vigentes.sort(Comparator.comparing(RegistroPuntos::getFechaVencimiento));
        return vigentes;
    }

    public ArrayList<RegistroPuntos> getListRegistros() {
        return new ArrayList<>(listRegistros);
    }

    public int getTotalAcumulados() {
        return totalAcumulados;
    }

    public int getTotalRedimidos() {
        return totalRedimidos;
    }

    public Cliente getTheCliente() {
        return theCliente;
    }
}
