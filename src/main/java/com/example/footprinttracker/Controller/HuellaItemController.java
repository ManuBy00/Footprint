package com.example.footprinttracker.Controller;

import com.example.footprinttracker.Model.Huella;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class HuellaItemController {
    @FXML private Label lblFecha;
    @FXML private Text iconCategoria;
    @FXML private Label lblCategoria;
    @FXML private Label lblActividad;
    @FXML private Label lblImpacto;

    private Huella huella; // Guardamos la referencia para saber cuál borrar

    // Este método lo llamaremos desde el controlador principal para "rellenar" la fila
    public void setDatos(Huella huella) {
        this.huella = huella;

        lblFecha.setText(huella.getFecha().toString());
        lblCategoria.setText(huella.getIdActividad().getIdCategoria().getNombre());
        lblActividad.setText(huella.getIdActividad().getNombreActividad());
        lblImpacto.setText(huella.getValor().toString() + " " + huella.getUnidad());

        // Configurar icono según categoría
        configurarIcono(huella.getIdActividad().getIdCategoria().getNombre());
    }

    private void configurarIcono(String categoria) {
        // Tu lógica de iconos (puedes copiarla del código anterior)
        switch (categoria.toLowerCase()) {
            case "transporte": iconCategoria.setText("🚗"); break;
            case "energía": iconCategoria.setText("⚡"); break;
            case "alimentación": iconCategoria.setText("🥩"); break;
            default: iconCategoria.setText("🌍"); break;
        }
    }

    @FXML
    public void eliminarHuella(ActionEvent event) {
        System.out.println("Eliminar huella ID: " + huella.getId());
        // Aquí podrías llamar a un listener o callback para avisar al padre que borre
    }

}
