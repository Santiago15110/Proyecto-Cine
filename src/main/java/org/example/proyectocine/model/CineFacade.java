package org.example.proyectocine.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CineFacade {

    /** Regla de puntos del grupo: 1 punto CinemaUQ por cada $1.000 pagados con saldo. */
    private static final double PESOS_POR_PUNTO = 1000;

    private final Cine cine;
    private final IPasarelaPago pasarela;
    private final PoliticaReembolso politicaReembolso;

    public CineFacade() {
        this(new PasarelaPagoAdapter());
    }

    /**
     * Permite cambiar la pasarela de pago sin modificar el Facade (depende de la interfaz, no del banco).
     *
     * @param pasarela pasarela usada para las recargas
     */
    public CineFacade(IPasarelaPago pasarela) {
        this.cine = Cine.getInstancia();
        this.pasarela = pasarela;
        this.politicaReembolso = new PoliticaReembolso();
    }

    // ===================== USUARIOS Y SESIÓN =====================

    public Cliente registrarCliente(String nombre, String correo, String clave, String cedula) {
        return cine.registrarCliente(nombre, correo, clave, cedula);
    }

    public Admin registrarAdmin(String nombre, String correo, String clave, String codigo) {
        return cine.registrarAdmin(nombre, correo, clave, codigo);
    }

    /**
     * Inicia sesión con correo y contraseña y deja al usuario como sesión actual.
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
        cine.setUsuarioActual(usuario);
        return usuario;
    }

    public void cerrarSesion() {
        cine.setUsuarioActual(null);
    }

    public Usuario obtenerUsuarioActual() {
        return cine.getUsuarioActual();
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

    // ===================== TARJETA =====================

    /**
     * Recarga la tarjeta virtual de un cliente. Solo el administrador puede hacerlo:
     * el pago se procesa con la pasarela externa (Adapter) y, si se aprueba, se suma al saldo.
     *
     * @param cedula cédula del cliente
     * @param monto  valor a recargar
     * @throws AccesoDenegadoException      si quien tiene la sesión no es administrador
     * @throws UsuarioNoEncontradoException si el cliente no existe
     * @throws IllegalArgumentException     si el monto es menor o igual a 0
     * @throws CinemaException              si la pasarela rechaza el pago
     */
    public void recargarTarjeta(String cedula, double monto) {
        validarAdministrador("recargar tarjetas");
        TarjetaVirtual tarjeta = buscarCliente(cedula).getTheTarjeta();
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto a recargar debe ser mayor a 0");
        }
        if (!pasarela.procesarPago(tarjeta.getNumero(), monto)) {
            throw new CinemaException("La pasarela de pago rechazó la recarga");
        }
        tarjeta.recargar(monto, "Recarga realizada por el administrador");
    }

    public void bloquearTarjeta(String cedula) {
        validarAdministrador("bloquear tarjetas");
        buscarCliente(cedula).getTheTarjeta().bloquear();
    }

    public void activarTarjeta(String cedula) {
        validarAdministrador("activar tarjetas");
        buscarCliente(cedula).getTheTarjeta().activar();
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

    // ===================== PUNTOS =====================

    public int consultarPuntos(String cedula) {
        return buscarCliente(cedula).getTheAcumulacion().getPuntos();
    }

    public ArrayList<RegistroPuntos> obtenerHistorialPuntos(String cedula) {
        return buscarCliente(cedula).getTheAcumulacion().getListRegistros();
    }

    // ===================== CARTELERA (ADMIN) =====================

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

    /**
     * El cine cancela una función: se devuelve el 100 % de cada compra y se avisa a los clientes.
     *
     * @param idFuncion id de la función
     */
    public void cancelarFuncion(String idFuncion) {
        Funcion funcion = buscarFuncion(idFuncion);
        if (funcion.isCancelada()) {
            throw new CancelacionNoPermitidaException("La función " + idFuncion + " ya está cancelada");
        }
        // Primero se devuelve el dinero (o los puntos) de cada compra pagada...
        for (Compra compra : cine.comprasDeFuncion(funcion)) {
            if (compra.getEstado() instanceof EstadoPagada) {
                cancelarYReembolsar(compra, 100);
            }
        }
        // ...y después se cancela la función, que avisa a los clientes suscritos (Observer)
        funcion.cancelar();
    }

    // ===================== COMPRAS =====================

    public Compra comprarEntradas(String cedula, String idFuncion, ArrayList<String> codigosAsientos) {
        return comprarEntradas(cedula, idFuncion, codigosAsientos, new ArrayList<>());
    }

    public Compra comprarEntradas(String cedula, String idFuncion, ArrayList<String> codigosAsientos,
                                  ArrayList<Promocion> promociones) {
        if (codigosAsientos == null || codigosAsientos.isEmpty()) {
            throw new CinemaException("Debes elegir al menos un asiento");
        }
        return realizarCompra(cedula, idFuncion, codigosAsientos, new LinkedHashMap<>(), promociones);
    }

    /**
     * Compra solo productos de confitería, sin entradas.
     *
     * @param cedula    cédula del cliente
     * @param productos id del producto o combo → cantidad
     * @return la compra realizada
     */
    public Compra comprarConfiteria(String cedula, Map<String, Integer> productos) {
        return realizarCompra(cedula, null, new ArrayList<>(), productos, new ArrayList<>());
    }

    /**
     * Compra entradas y/o confitería pagando con el saldo de la tarjeta virtual.
     *
     * @param cedula          cédula del cliente
     * @param idFuncion       id de la función, o null si solo compra confitería
     * @param codigosAsientos asientos elegidos (ejemplo: "A5"); puede estar vacía si solo compra confitería
     * @param productos       id del producto o combo → cantidad; puede estar vacío
     * @param promociones     promociones a aplicar; puede estar vacía
     * @return la compra realizada
     */
    public Compra realizarCompra(String cedula, String idFuncion, ArrayList<String> codigosAsientos,
                                 Map<String, Integer> productos, ArrayList<Promocion> promociones) {
        Cliente cliente = buscarCliente(cedula);

        // 1. Armar la compra con el Builder (valida que los asientos estén libres y no se repitan)
        Compra compra = armarCompra(cliente, idFuncion, codigosAsientos, productos, promociones);

        // 2. Confirmar: el estado Pendiente reserva los asientos y pasa la compra a Pagada (State)
        compra.confirmar();

        // 3. Cobrar (la tarjeta verifica que esté activa y tenga saldo).
        //    Si falla, se cancela la compra para liberar los asientos
        if (compra.getTotal() > 0) {
            try {
                cliente.getTheTarjeta().descontar(compra.getTotal(), "Compra " + compra.getId());
            } catch (CinemaException e) {
                compra.cancelar();
                throw e;
            }
        }

        // 4. Puntos (quedan ligados a la compra), suscripción a la función (Observer) y guardar
        int puntos = calcularPuntos(compra.getTotal());
        if (puntos > 0) {
            cliente.getTheAcumulacion().sumarPuntos(puntos, compra.getId());
        }
        suscribirYGuardar(cliente, compra);
        return compra;
    }

    /**
     * Compra entradas y/o confitería redimiendo puntos CinemaUQ en vez de saldo.
     * Se usan primero los puntos más próximos a vencer; los vencidos no cuentan.
     * Una compra con puntos no genera puntos nuevos.
     *
     * @param cedula          cédula del cliente
     * @param idFuncion       id de la función, o null si solo redime confitería
     * @param codigosAsientos asientos elegidos; puede estar vacía
     * @param productos       id del producto o combo → cantidad; puede estar vacío
     * @return la compra realizada
     * @throws PuntosInsuficientesException si los puntos vigentes no alcanzan
     */
    public Compra realizarCompraConPuntos(String cedula, String idFuncion, ArrayList<String> codigosAsientos,
                                          Map<String, Integer> productos) {
        Cliente cliente = buscarCliente(cedula);
        Compra compra = armarCompra(cliente, idFuncion, codigosAsientos, productos, new ArrayList<>());
        int costo = calcularCostoPuntos(compra);
        int disponibles = cliente.getTheAcumulacion().getPuntos();
        if (costo > disponibles) {
            throw new PuntosInsuficientesException("Puntos insuficientes: tienes " + disponibles
                    + " puntos vigentes y se necesitan " + costo);
        }

        compra.confirmar();
        cliente.getTheAcumulacion().redimirPuntos(costo);
        compra.setPuntosRedimidos(costo);
        for (Entrada e : compra.getEntradas()) {
            e.setPagadaConPuntos(true);
        }
        suscribirYGuardar(cliente, compra);
        return compra;
    }

    /**
     * Calcula cuántos puntos cuesta una selección, para mostrarlo antes de redimir.
     *
     * @param idFuncion       id de la función, o null
     * @param codigosAsientos cantidad de entradas que se cuentan
     * @param productos       id del producto o combo → cantidad
     * @return puntos necesarios
     */
    public int consultarCostoEnPuntos(String idFuncion, ArrayList<String> codigosAsientos,
                                      Map<String, Integer> productos) {
        int costo = 0;
        if (codigosAsientos != null && !codigosAsientos.isEmpty()) {
            buscarFuncion(idFuncion);
            costo += codigosAsientos.size() * Entrada.COSTO_PUNTOS;
        }
        if (productos != null) {
            for (Map.Entry<String, Integer> p : productos.entrySet()) {
                costo += buscarItemConfiteria(p.getKey()).getCostoPuntos() * p.getValue();
            }
        }
        return costo;
    }

    /**
     * El cliente cancela su compra. Se devuelve según el tiempo que falta para la función:
     * más de 24 h el 100 %, entre 2 y 24 h el 80 %, y con menos de 2 h no se permite.
     * Los puntos que dio la compra se anulan.
     *
     * @param idCompra id de la compra
     * @throws CancelacionNoPermitidaException si faltan menos de 2 horas o la compra ya estaba cancelada
     */
    public void cancelarCompra(String idCompra) {
        Compra compra = buscarCompra(idCompra);
        if (!(compra.getEstado() instanceof EstadoPagada)) {
            // El estado decide: Pendiente pasa a Cancelada, Cancelada lanza la excepción
            compra.cancelar();
            return;
        }
        double porcentaje = politicaReembolso.porcentajeReembolso(compra.getFuncion(), LocalDateTime.now());
        cancelarYReembolsar(compra, porcentaje);

        // Si el cliente ya no tiene entradas para esa función, deja de recibir sus avisos
        Funcion funcion = compra.getFuncion();
        if (funcion != null && !tieneComprasPagadas(compra.getCliente(), funcion)) {
            funcion.eliminarObservador(compra.getCliente());
        }
    }

    public ArrayList<Compra> historialCliente(String cedula) {
        return cine.comprasDeCliente(buscarCliente(cedula));
    }

    public ArrayList<String> obtenerNotificaciones(String cedula) {
        return new ArrayList<>(buscarCliente(cedula).getListNotificaciones());
    }

    // ===================== CONFITERÍA (COMPOSITE) =====================

    public Producto crearProducto(String id, String nombre, double precio, int costoPuntos) {
        Producto producto = new Producto(id, nombre, precio, costoPuntos);
        cine.agregarItemConfiteria(producto);
        return producto;
    }

    /**
     * Crea un combo a partir de productos u otros combos ya creados.
     * Un id repetido en la lista significa más de una unidad (ejemplo: ["CRISP", "CRISP", "GAS"]).
     *
     * @param id        id del combo
     * @param nombre    nombre del combo
     * @param descuento porcentaje de descuento sobre la suma de sus ítems
     * @param idsItems  ids de los productos o combos que lo forman
     * @return el combo creado
     */
    public Combo crearCombo(String id, String nombre, double descuento, ArrayList<String> idsItems) {
        if (idsItems == null || idsItems.isEmpty()) {
            throw new CinemaException("El combo debe tener al menos un producto");
        }
        Combo combo = new Combo(id, nombre, descuento);
        for (String idItem : idsItems) {
            combo.agregar(buscarItemConfiteria(idItem));
        }
        cine.agregarItemConfiteria(combo);
        return combo;
    }

    public ArrayList<ItemConfiteria> obtenerConfiteria() {
        return new ArrayList<>(cine.getConfiteria());
    }

    // ===================== CONSULTAS =====================

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

    // ===================== AUXILIARES =====================

    private Compra armarCompra(Cliente cliente, String idFuncion, ArrayList<String> codigosAsientos,
                               Map<String, Integer> productos, ArrayList<Promocion> promociones) {
        boolean hayEntradas = codigosAsientos != null && !codigosAsientos.isEmpty();
        boolean hayProductos = productos != null && !productos.isEmpty();
        if (!hayEntradas && !hayProductos) {
            throw new CinemaException("La compra debe tener al menos una entrada o un producto");
        }
        CompraBuilder builder = new CompraBuilder().paraCliente(cliente);
        if (hayEntradas) {
            Funcion funcion = buscarFuncion(idFuncion);
            if (funcion.isCancelada()) {
                throw new CinemaException("La función " + idFuncion + " está cancelada");
            }
            for (String codigo : codigosAsientos) {
                Asiento asiento = buscarAsiento(funcion, codigo);
                builder.agregarEntrada(new Entrada(generarId("E"), funcion, asiento));
            }
        }
        if (hayProductos) {
            for (Map.Entry<String, Integer> p : productos.entrySet()) {
                if (p.getValue() == null || p.getValue() <= 0) {
                    throw new CinemaException("La cantidad de " + p.getKey() + " debe ser mayor que cero");
                }
                builder.agregarProducto(buscarItemConfiteria(p.getKey()), p.getValue());
            }
        }
        if (promociones != null) {
            for (Promocion promocion : promociones) {
                if (!promocion.estaVigente(LocalDate.now())) {
                    throw new CinemaException("La promoción " + promocion.getNombre() + " no está vigente");
                }
                builder.aplicarPromocion(promocion);
            }
        }
        return builder.build();
    }

    private void suscribirYGuardar(Cliente cliente, Compra compra) {
        Funcion funcion = compra.getFuncion();
        if (funcion != null) {
            funcion.agregarObservador(cliente);
        }
        cine.agregarCompra(compra);
    }

    private int calcularPuntos(double total) {
        return (int) (total / PESOS_POR_PUNTO);
    }

    private int calcularCostoPuntos(Compra compra) {
        int costo = 0;
        for (Entrada e : compra.getEntradas()) {
            costo += e.getCostoPuntos();
        }
        for (LineaProducto l : compra.getLineas()) {
            costo += l.getItem().getCostoPuntos() * l.getCantidad();
        }
        return costo;
    }

    private String generarId(String prefijo) {
        return prefijo + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Cancela una compra pagada y devuelve el porcentaje indicado a la tarjeta (o los puntos,
     * si se pagó con puntos). También anula los puntos que había dado la compra.
     */
    private void cancelarYReembolsar(Compra compra, double porcentaje) {
        compra.cancelar();   // EstadoPagada libera los asientos y pasa a Cancelada
        AcumulacionPunto acumulacion = compra.getCliente().getTheAcumulacion();
        // Primero se anulan los puntos que había dado la compra (antes de devolver puntos con la misma referencia)
        acumulacion.anularPuntosDe(compra.getId());
        String etiqueta = porcentaje == 100 ? "" : " (" + (int) porcentaje + " %)";
        if (compra.isPagadaConPuntos()) {
            int puntos = (int) Math.round(compra.getPuntosRedimidos() * porcentaje / 100);
            acumulacion.devolverPuntos(puntos, compra.getId());
        } else if (compra.getTotal() > 0) {
            double monto = compra.getTotal() * porcentaje / 100;
            compra.getCliente().getTheTarjeta()
                    .reembolsar(monto, "Reembolso compra " + compra.getId() + etiqueta);
        }
    }

    private void validarAdministrador(String accion) {
        if (!(cine.getUsuarioActual() instanceof Admin)) {
            throw new AccesoDenegadoException("Solo el administrador puede " + accion);
        }
    }

    private boolean tieneComprasPagadas(Cliente cliente, Funcion funcion) {
        for (Compra c : cine.comprasDeCliente(cliente)) {
            if (funcion.equals(c.getFuncion()) && c.getEstado() instanceof EstadoPagada) {
                return true;
            }
        }
        return false;
    }

    private Asiento buscarAsiento(Funcion funcion, String codigo) {
        if (codigo == null || !codigo.trim().matches("[A-Za-z]\\d+")) {
            throw new CinemaException("El asiento " + codigo + " no tiene un formato válido (ejemplo: A5)");
        }
        String limpio = codigo.trim();
        try {
            return funcion.getSala().buscarAsiento(limpio.charAt(0), Integer.parseInt(limpio.substring(1)));
        } catch (IllegalArgumentException e) {
            throw new CinemaException(e.getMessage());
        }
    }

    private ItemConfiteria buscarItemConfiteria(String id) {
        ItemConfiteria item = cine.buscarItemConfiteria(id);
        if (item == null) {
            throw new CinemaException("No existe un producto o combo con el id " + id);
        }
        return item;
    }

    private Compra buscarCompra(String id) {
        Compra compra = cine.buscarCompra(id);
        if (compra == null) {
            throw new CinemaException("No existe una compra con el id " + id);
        }
        return compra;
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

    // ===================== REPORTES (STRATEGY) =====================

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
