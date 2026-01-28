package com.example.footprinttracker.DTO;

public class CategoriaEstadistica {
    private String nombreCategoria;
    private Long cantidad;


    public CategoriaEstadistica(String nombreCategoria, Long cantidad) {
        this.nombreCategoria = nombreCategoria;
        this.cantidad = cantidad;
    }

    // Getters
    public String getNombreCategoria() { return nombreCategoria; }
    public Long getCantidad() { return cantidad; }
}