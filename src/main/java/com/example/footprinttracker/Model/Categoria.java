package com.example.footprinttracker.Model;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria", nullable = false)
    private Integer id;

    @Column(name = "nombre", length = 40)
    private String nombre;

    @Column(name = "factor_emision", length = 90)
    private String factorEmision;

    @Column(name = "unidad", length = 15)
    private String unidad;

    @OneToMany
    @JoinColumn(name = "id_categoria")
    private Set<Actividad> actividads = new LinkedHashSet<>();

    @OneToMany
    @JoinColumn(name = "id_categoria")
    private Set<Recomendacion> recomendacions = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFactorEmision() {
        return factorEmision;
    }

    public void setFactorEmision(String factorEmision) {
        this.factorEmision = factorEmision;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public Set<Actividad> getActividads() {
        return actividads;
    }

    public void setActividads(Set<Actividad> actividads) {
        this.actividads = actividads;
    }

    public Set<Recomendacion> getRecomendacions() {
        return recomendacions;
    }

    public void setRecomendacions(Set<Recomendacion> recomendacions) {
        this.recomendacions = recomendacions;
    }

}