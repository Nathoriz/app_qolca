package edu.pe.idat.app_qolca.model;

public class Pedido {
    private int id;
    private Usuario usuario;
    private String persona;
    private String direccion;
    private String numero;
    private double total;
    private String fecha;
    private String estado;

    public Pedido(int id, Usuario usuario, String persona, String direccion, String numero, double total, String fecha, String estado) {
        this.id = id;
        this.usuario = usuario;
        this.persona = persona;
        this.direccion = direccion;
        this.numero = numero;
        this.total = total;
        this.fecha = fecha;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
