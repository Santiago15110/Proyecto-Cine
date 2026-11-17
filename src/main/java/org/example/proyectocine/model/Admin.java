package org.example.proyectocine.model;

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
