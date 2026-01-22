package com.example.footprinttracker.Controller;

import com.example.footprinttracker.Model.Habito;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

public class HabitoItemController {

    @FXML private CheckBox checkTitulo;
    @FXML private Label lblImpacto;
    @FXML private Label lblDescripcion;

    private Habito habito;
    private Runnable alCambiarEstado; // Callback para avisar al padre

    public void setDatos(Habito habito, Runnable alCambiarEstado) {
        this.habito = habito;
        this.alCambiarEstado = alCambiarEstado;

        // 1. Rellenar textos
        checkTitulo.setText(habito.getIdActividad().getNombreActividad());

        lblDescripcion.setText(habito.getFrecuencia());
        lblImpacto.setText(habito.getImpactoTexto());

        // 2. Aplicar estilo CSS a la etiqueta según el tipo
        aplicarEstiloEtiqueta(habito.getTipoImpacto());
    }

    @FXML
    private void onCheckAction() {
        // Actualizamos el modelo
        habito.setCompletado(checkTitulo.isSelected());

        // Avisamos al padre para que recalcule la barra de progreso
        if (alCambiarEstado != null) {
            alCambiarEstado.run();
        }
    }

    private void aplicarEstiloEtiqueta(String tipo) {
        lblImpacto.getStyleClass().removeAll("tag-water", "tag-co2", "tag-circular");

        switch (tipo.toLowerCase()) {
            case "water": lblImpacto.getStyleClass().add("tag-water"); break;
            case "circular": lblImpacto.getStyleClass().add("tag-circular"); break;
            default: lblImpacto.getStyleClass().add("tag-co2"); break;
        }
    }
}