package com.example.footprinttracker.Model;

import jakarta.persistence.*;

@Entity
public class Recomendacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recomendacion", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria idCategoria;

    @Column(name = "descripcion", length = 400)
    private String descripcion;

    @Column(name = "impacto_estimado", nullable = false)
    private Integer impactoEstimado;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Categoria getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Categoria idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getImpactoEstimado() {
        return impactoEstimado;
    }

    public void setImpactoEstimado(Integer impactoEstimado) {
        this.impactoEstimado = impactoEstimado;
    }

}