package com.cafeteria.model;

public class Producto {
    private int id_producto;
    private String nombre;
    private String descripcion;
    private double costo;
    private String area;
    private int tiempo; // <--- este existía en el SQL
    private String src;
    private int estado;

    public Producto() {}

    public int getId() { return id_producto; }
    public void setId(int id_producto) { this.id_producto = id_producto; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getCosto() { return costo; }
    public void setCosto(double costo) { this.costo = costo; }

    public int getTiempo() { return tiempo; }
    public void setTiempo(int tiempo) { this.tiempo = tiempo; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getSrc() { return src; }
    public void setSrc(String src) { this.src = src; }

    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }
}

