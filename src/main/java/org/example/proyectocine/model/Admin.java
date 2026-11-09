package org.example.proyectocine.model;

/**
 * Usuario administrador del cine.
 * Se identifica con un código de administrador además de los datos de {@link Usuario}.
 */
public class Admin extends Usuario {

    /**
     * Código que identifica al administrador.
     */
    private String codigo;

    /**
     * Crea un administrador.
     *
     * @param nombre nombre completo del administrador
     * @param correo correo electrónico del administrador
     * @param clave  contraseña del administrador
     * @param codigo código de administrador
     */
    public Admin(String nombre, String correo, String clave, String codigo) {
        super(nombre, correo, clave);
        this.codigo = codigo;
    }

    /**
     * Obtiene el código del administrador.
     *
     * @return el código de administrador
     */
    public String getCodigo() {
        return codigo;
    }
}
