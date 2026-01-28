package com.example.footprinttracker.DTO;

public class ImpactoMensual {
    private Integer mes;
    private Double totalImpacto;

    // El constructor debe coincidir con el orden del SELECT
    public ImpactoMensual(Integer mes, Double totalImpacto) {
        this.mes = mes;
        this.totalImpacto = totalImpacto;
    }

    public Integer getMes() { return mes; }
    public Double getTotalImpacto() { return totalImpacto; }
}