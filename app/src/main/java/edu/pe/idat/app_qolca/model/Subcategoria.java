package edu.pe.idat.app_qolca.model;

public class Subcategoria {
    private int id;
    private String nombre;

    public Subcategoria(int id, String nombre, Categoria categoria) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
