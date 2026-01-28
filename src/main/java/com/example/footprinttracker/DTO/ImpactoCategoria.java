package com.example.footprinttracker.DTO;

public class ImpactoCategoria {
    private String categoria;
    private Double impactoTotal;

    public ImpactoCategoria(String categoria, Number impactoTotal) {
        this.categoria = categoria;
        // Si llega un 5 (Long), lo guardamos como 5.0.
        // Si llega un 0.3 (Double), lo guardamos como 0.3.
        this.impactoTotal = impactoTotal != null ? impactoTotal.doubleValue() : 0.0;
    }

    public String getCategoria() { return categoria; }
    public Double getImpactoTotal() { return impactoTotal; }
}