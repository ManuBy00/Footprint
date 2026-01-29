package com.example.footprinttracker.DTO;

public class ImpactoCategoria {
    private String categoria;
    private Double impactoTotal;

    public ImpactoCategoria(String categoria, Number impactoTotal) {
        this.categoria = categoria;
        this.impactoTotal = impactoTotal != null ? impactoTotal.doubleValue() : 0.0;
    }

    public String getCategoria() { return categoria; }
    public Double getImpactoTotal() { return impactoTotal; }
}