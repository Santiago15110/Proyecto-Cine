package org.example.proyectocine.model;

/**
 * Clase base de todos los usuarios del sistema del cine.
 * Contiene los datos comunes que se usan para identificarse e iniciar sesión.
 *
 * @see Cliente
 * @see Admin
 */
public class Usuario {

    /**
     * Nombre completo del usuario.
     */
    protected String nombre;

    /**
     * Correo electrónico; es único en el sistema y se usa para iniciar sesión.
     */
    protected String correo;

    /**
     * Contraseña del usuario.
     */
    protected String clave;

    /**
     * Crea un usuario con sus datos básicos.
     *
     * @param nombre nombre completo del usuario
     * @param correo correo electrónico del usuario
     * @param clave  contraseña del usuario
     */
    public Usuario(String nombre, String correo, String clave) {
        this.nombre = nombre;
        this.correo = correo;
        this.clave = clave;
    }

    /**
     * Obtiene el nombre del usuario.
     *
     * @return el nombre completo
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene el correo del usuario.
     *
     * @return el correo electrónico
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * Obtiene la contraseña del usuario.
     *
     * @return la contraseña
     */
    public String getClave() {
        return clave;
    }
}
