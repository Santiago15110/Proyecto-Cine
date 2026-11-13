package org.example.proyectocine.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CineFacade {

    private final Cine cine;

    public CineFacade() {
        this.cine = Cine.getInstancia();
    }


    public Cliente registrarCliente(String nombre, String correo, String clave, String cedula) {
        return cine.registrarCliente(nombre, correo, clave, cedula);
    }

    public Admin registrarAdmin(String nombre, String correo, String clave, String codigo) {
        return cine.registrarAdmin(nombre, correo, clave, codigo);
    }

    /**
     * Inicia sesión con correo y contraseña.
     *
     * @param correo correo del usuario
     * @param clave  contraseña del usuario
     * @return el usuario que inició sesión (cliente o administrador)
     * @throws CredencialesInvalidasException si el correo o la contraseña no coinciden
     */
    public Usuario iniciarSesion(String correo, String clave) {
        Usuario usuario = cine.login(correo, clave);
        if (usuario == null) {
            throw new CredencialesInvalidasException("Correo o contraseña incorrectos");
        }
        return usuario;
    }

    /**
     * Busca un cliente por su cédula.
     *
     * @param cedula cédula del cliente
     * @return el cliente encontrado
     * @throws UsuarioNoEncontradoException si no existe un cliente con esa cédula
     */
    public Cliente buscarCliente(String cedula) {
        Cliente cliente = cine.buscarClientePorCedula(cedula);
        if (cliente == null) {
            throw new UsuarioNoEncontradoException("No existe un cliente con la cédula " + cedula);
        }
        return cliente;
    }

    public ArrayList<Usuario> obtenerUsuarios() {
        return new ArrayList<>(cine.getUsuarios());
    }


    /**
     * Recarga la tarjeta virtual de un cliente; la tarjeta registra el movimiento.
     *
     * @param cedula cédula del cliente
     * @param monto  valor a recargar
     * @throws UsuarioNoEncontradoException si el cliente no existe
     * @throws IllegalArgumentException     si el monto es menor o igual a 0     */
    public void recargarTarjeta(String cedula, double monto) {
        buscarCliente(cedula).getTheTarjeta().recargar(monto);
    }

    public double consultarSaldo(String cedula) {
        return buscarCliente(cedula).getTheTarjeta().getSaldo();
    }

    /**
     * Devuelve los movimientos de la tarjeta del más reciente al más antiguo.
     *
     * @param cedula cédula del cliente
     * @return copia de la lista de movimientos en orden inverso
     */
    public ArrayList<MovimientoSaldo> obtenerMovimientos(String cedula) {
        ArrayList<MovimientoSaldo> movimientos =
                new ArrayList<>(buscarCliente(cedula).getTheTarjeta().getListMovimientos());
        Collections.reverse(movimientos);
        return movimientos;
    }

    public int consultarPuntos(String cedula) {
        return buscarCliente(cedula).getTheAcumulacion().getPuntos();
    }


    public Pelicula agregarPelicula(String id, String titulo, Genero genero, String clasificacion,
                                    String descripcion, String rutaPoster) {
        Pelicula pelicula = new Pelicula(id, titulo, genero, clasificacion, descripcion, rutaPoster);
        cine.agregarPelicula(pelicula);
        return pelicula;
    }

    public Sala crearSala(String id, String nombre, int filas, int columnas) {
        Sala sala = new Sala(id, nombre, filas, columnas);
        cine.agregarSala(sala);
        return sala;
    }

    /**
     * Programa una función de una película en una sala.
     *
     * @param id          identificador de la función
     * @param idPelicula  id de la película
     * @param idSala      id de la sala
     * @param fechaHora   fecha y hora de inicio
     * @param duracionMin duración en minutos
     * @param precio      precio base de la entrada
     * @return la función programada
     * @throws CinemaException si la película o la sala no existen, o si el ID de la función ya existe
     */
    public Funcion programarFuncion(String id, String idPelicula, String idSala,
                                    LocalDateTime fechaHora, int duracionMin, double precio) {
        Pelicula pelicula = buscarPelicula(idPelicula);
        Sala sala = buscarSala(idSala);
        Funcion funcion = new Funcion(id, pelicula, sala, fechaHora, duracionMin, precio);
        cine.agregarFuncion(funcion);
        return funcion;
    }

    public void cancelarFuncion(String idFuncion) {
        buscarFuncion(idFuncion).cancelar();
    }

    public ArrayList<Pelicula> obtenerPeliculasEnCartelera() {
        ArrayList<Pelicula> enCartelera = new ArrayList<>();
        for (Pelicula p : cine.getPeliculas()) {
            if (p.isEnCartelera()) {
                enCartelera.add(p);
            }
        }
        return enCartelera;
    }

    public ArrayList<Sala> obtenerSalas() {
        return new ArrayList<>(cine.getSalas());
    }

    public ArrayList<Funcion> obtenerFunciones() {
        return new ArrayList<>(cine.getFunciones());
    }

    /**
     * Devuelve los asientos que todavía se pueden vender en una función.
     *
     * @param idFuncion id de la función
     * @return asientos disponibles
     * @throws CinemaException si la función no existe
     */
    public ArrayList<Asiento> consultarAsientosDisponibles(String idFuncion) {
        Funcion funcion = buscarFuncion(idFuncion);
        ArrayList<Asiento> disponibles = new ArrayList<>();
        for (Asiento a : funcion.getSala().getAsientos()) {
            if (funcion.estaDisponible(a)) {
                disponibles.add(a);
            }
        }
        return disponibles;
    }


    private Pelicula buscarPelicula(String id) {
        Pelicula pelicula = cine.buscarPelicula(id);
        if (pelicula == null) {
            throw new CinemaException("No existe una película con el id " + id);
        }
        return pelicula;
    }

    private Sala buscarSala(String id) {
        Sala sala = cine.buscarSala(id);
        if (sala == null) {
            throw new CinemaException("No existe una sala con el id " + id);
        }
        return sala;
    }

    private Funcion buscarFuncion(String id) {
        Funcion funcion = cine.buscarFuncion(id);
        if (funcion == null) {
            throw new CinemaException("No existe una función con el id " + id);
        }
        return funcion;
    }

    /**
     * Genera el Reporte de Ventas en un rango de fechas.
     */
    public ReporteStrategy crearReporteVentas(LocalDate inicio, LocalDate fin) {
        return new ReporteVentas(inicio, fin, cine.getCompras());
    }

    /**
     * Genera el Reporte de Entradas por Función.
     */
    public ReporteStrategy crearReporteEntradasFuncion(LocalDate inicio, LocalDate fin) {
        return new ReporteEntradasFuncion(inicio, fin, cine.getFunciones(), cine.getCompras());
    }

    /**
     * Genera el Reporte de Ocupación de Salas.
     */
    public ReporteStrategy crearReporteOcupacionSalas() {
        return new ReporteOcupacionSalas(cine.getFunciones());
    }

    /**
     * Genera el Reporte de Confitería.
     */
    public ReporteStrategy crearReporteConfiteria(LocalDate inicio, LocalDate fin) {
        return new ReporteConfiteria(inicio, fin, cine.getCompras());
    }

    /**
     * Genera el Reporte de Recargas obteniendo las tarjetas virtuales de los clientes registrados.
     */
    public ReporteStrategy crearReporteRecargas(LocalDate inicio, LocalDate fin) {
        List<TarjetaVirtual> tarjetas = new ArrayList<>();
        for (Usuario u : cine.getUsuarios()) {
            if (u instanceof Cliente) {
                tarjetas.add(((Cliente) u).getTheTarjeta());
            }
        }
        return new ReporteRecargas(inicio, fin, tarjetas);
    }

    /**
     * Genera el Reporte de Cancelaciones.
     */
    public ReporteStrategy crearReporteCancelaciones(LocalDate inicio, LocalDate fin) {
        List<TarjetaVirtual> tarjetas = new ArrayList<>();
        for (Usuario u : cine.getUsuarios()) {
            if (u instanceof Cliente) {
                tarjetas.add(((Cliente) u).getTheTarjeta());
            }
        }
        return new ReporteCancelaciones(inicio, fin, cine.getCompras(), tarjetas);
    }

    /**
     * Genera el Reporte de Puntos de los clientes.
     */
    public ReporteStrategy crearReportePuntos(LocalDate inicio, LocalDate fin) {
        List<Cliente> clientes = new ArrayList<>();
        for (Usuario u : cine.getUsuarios()) {
            if (u instanceof Cliente) {
                clientes.add((Cliente) u);
            }
        }
        return new ReportePuntos(inicio, fin, clientes);
    }

    /**
     * Genera el contenido en texto plano de cualquier estrategia de reporte seleccionada.
     */
    public String generarReporteTexto(ReporteStrategy estrategia) {
        if (estrategia == null) {
            throw new IllegalArgumentException("La estrategia de reporte no puede ser nula.");
        }
        return estrategia.generarContenidoTexto();
    }

    /**
     * Exporta el reporte actual a un archivo de texto (.txt).
     */
    public void exportarReporteTXT(ReporteStrategy estrategia, String ruta) {
        try {
            ExportadorTXT.exportar(estrategia, ruta);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el archivo TXT: " + e.getMessage());
        }
    }

    /**
     * Exporta el reporte actual a un documento PDF.
     */
    public void exportarReportePDF(ReporteStrategy estrategia, String ruta) {
        AdaptadorPDF.exportarPDF(estrategia, ruta);
    }
}
