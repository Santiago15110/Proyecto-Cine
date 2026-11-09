package org.example.proyectocine.model;

import java.util.ArrayList;

/**
 * Clase central del sistema del cine.
 * Contiene las listas principales y se encarga de crear los objetos
 * (registrar, buscar, iniciar sesión). Usa el patrón Singleton para que
 * todas las pantallas compartan los mismos datos.
 */
public class Cine {

    /**
     * Única instancia del cine (Singleton).
     */
    private static Cine instancia;

    /**
     * Usuarios registrados en el sistema (clientes y administradores).
     */
    private final ArrayList<Usuario> usuarios = new ArrayList<>();

    /**
     * Constructor privado para que no se puedan crear otras instancias.
     * Usar {@link #getInstancia()}.
     */
    private Cine() {
    }

    /**
     * Obtiene la única instancia del cine; la crea la primera vez que se llama.
     *
     * @return la instancia del cine
     */
    public static Cine getInstancia() {
        if (instancia == null) {
            instancia = new Cine();
        }
        return instancia;
    }

    /**
     * Registra un cliente nuevo en el sistema.
     *
     * @param nombre nombre completo del cliente
     * @param correo correo electrónico del cliente
     * @param clave  contraseña del cliente
     * @param cedula cédula del cliente
     * @return el cliente registrado
     * @throws IllegalArgumentException si algún campo está vacío, o si el correo
     *                                  o la cédula ya están registrados
     */
    public Cliente registrarCliente(String nombre, String correo, String clave, String cedula) {
        validarCampos(nombre, correo, clave, cedula);
        validarCorreoDisponible(correo);
        validarCedulaDisponible(cedula);
        Cliente cliente = new Cliente(nombre, correo, clave, cedula);
        usuarios.add(cliente);
        return cliente;
    }


    /**
     * Registra un administrador nuevo en el sistema.
     *
     * @param nombre nombre completo del administrador
     * @param correo correo electrónico del administrador
     * @param clave  contraseña del administrador
     * @param codigo código de administrador
     * @return el administrador registrado
     * @throws IllegalArgumentException si algún campo está vacío o si el correo ya está registrado
     */
    public Admin registrarAdmin(String nombre, String correo, String clave, String codigo) {
        validarCampos(nombre, correo, clave, codigo);
        validarCorreoDisponible(correo);
        Admin admin = new Admin(nombre, correo, clave, codigo);
        usuarios.add(admin);
        return admin;
    }

    /**
     * Inicia sesión con correo y contraseña.
     *
     * @param correo correo del usuario
     * @param clave  contraseña del usuario
     * @return el usuario si el correo y la clave coinciden, o {@code null} si no
     */
    public Usuario login(String correo, String clave) {
        Usuario usuario = buscarUsuarioPorCorreo(correo);
        if (usuario != null && usuario.getClave().equals(clave)) {
            return usuario;
        }
        return null;
    }

    /**
     * Busca un usuario (cliente o administrador) por su correo, sin distinguir mayúsculas.
     *
     * @param correo correo a buscar
     * @return el usuario encontrado, o {@code null} si no existe
     */
    public Usuario buscarUsuarioPorCorreo(String correo) {
        for (Usuario u : usuarios) {
            if (u.getCorreo().equalsIgnoreCase(correo)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Busca un cliente por su cédula.
     *
     * @param cedula cédula a buscar
     * @return el cliente encontrado, o {@code null} si no existe
     */
    public Cliente buscarClientePorCedula(String cedula) {
        for (Usuario u : usuarios) {
            if (u instanceof Cliente) {
                Cliente c = (Cliente) u;
                if (c.getCedula().equals(cedula)) {
                    return c;
                }
            }
        }
        return null;
    }

    /**
     * Obtiene la lista de usuarios registrados.
     *
     * @return la lista de usuarios
     */
    public ArrayList<Usuario> getUsuarios() {
        return usuarios;
    }


    /**
     * Verifica que el correo no esté registrado.
     *
     * @param correo correo a verificar
     * @throws IllegalArgumentException si ya existe un usuario con ese correo
     */
    private void validarCorreoDisponible(String correo) {
        if (buscarUsuarioPorCorreo(correo) != null) {
            throw new IllegalArgumentException("Ya existe un usuario con el correo " + correo);
        }
    }

    /**
     * Verifica que la cédula no esté registrada.
     *
     * @param cedula cédula a verificar
     * @throws IllegalArgumentException si ya existe un cliente con esa cédula
     */
    private void validarCedulaDisponible(String cedula) {
        if (buscarClientePorCedula(cedula) != null) {
            throw new IllegalArgumentException("Ya existe un cliente con la cedula " + cedula);
        }
    }

    /**
     * Verifica que ningún campo esté vacío.
     *
     * @param campos campos a verificar
     * @throws IllegalArgumentException si algún campo es {@code null} o está vacío
     */
    private void validarCampos(String... campos) {
        for (String campo : campos) {
            if (campo == null || campo.isBlank()) {
                throw new IllegalArgumentException("Todos los campos son obligatorios");
            }
        }
    }
}
