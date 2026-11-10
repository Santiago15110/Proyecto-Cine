package org.example.proyectocine.model;

/**
 * Usuario administrador del cine.
 * Se identifica con un código de administrador además de los datos de {@link Usuario}.
 */
public class Admin extends Usuario {

    private String codigo;

    public Admin(String nombre, String correo, String clave, String codigo) {
        super(nombre, correo, clave);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
