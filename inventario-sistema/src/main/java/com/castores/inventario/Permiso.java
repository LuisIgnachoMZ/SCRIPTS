package com.castores.inventario;

public class Permiso {
    private int idPermiso;
    private String nombre;
    private int estatus;

    // Constructor de Permiso
    public Permiso(int idPermiso, String nombre, int estatus) {
        this.idPermiso = idPermiso;
        this.nombre = nombre;
        this.estatus = estatus;
    }

    // Getters para Permiso
    public int getIdPermiso() {
        return idPermiso;
    }

    public String getNombre() {
        return nombre;
    }

    public int getEstatus() {
        return estatus;
    }
}
