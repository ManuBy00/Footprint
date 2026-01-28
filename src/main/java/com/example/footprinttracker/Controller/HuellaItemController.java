package com.example.footprinttracker.Controller;

import com.example.footprinttracker.Model.Huella;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import java.util.function.Consumer;

public class HuellaItemController {
    @FXML private Label lblFecha;
    @FXML private Text iconCategoria;
    @FXML private Label lblCategoria;
    @FXML private Label lblActividad;
    @FXML private Label lblImpacto;

    private Huella huella; // Guardamos la referencia para saber cuál borrar

    private Consumer<Huella> onEliminarAction;

    /**
     * Rellena la fila visual con la información de la huella (fecha, categoría, impacto...).
     * Recibe también la lógica (Consumer) que se debe ejecutar si el usuario decide
     * borrar este registro, guardándola para usarla después.
     */
    public void setDatos(Huella huella, Consumer<Huella> accionEliminar) {
        this.huella = huella;

        lblFecha.setText(huella.getFecha().toString());
        lblCategoria.setText(huella.getIdActividad().getIdCategoria().getNombre());
        lblActividad.setText(huella.getIdActividad().getNombreActividad());
        lblImpacto.setText(huella.getValor().toString() + " " + huella.getUnidad());

        this.onEliminarAction = accionEliminar;

        // Configurar icono según categoría
        configurarIcono(huella.getIdActividad().getIdCategoria().getNombre());
    }

    /**
     * Cambia el icono o emoji de la fila basándose en el nombre de la categoría
     * (ej: un coche para Transporte, un rayo para Energía) para identificarlo rápido visualmente.
     */
    private void configurarIcono(String categoria) {
        // Tu lógica de iconos (puedes copiarla del código anterior)
        switch (categoria.toLowerCase()) {
            case "transporte": iconCategoria.setText("🚗"); break;
            case "energía": iconCategoria.setText("⚡"); break;
            case "alimentación": iconCategoria.setText("🥩"); break;
            default: iconCategoria.setText("🌍"); break;
        }
    }

    /**
     * Gestiona el clic en el botón de eliminar.
     * Ejecuta la acción que nos pasó el controlador principal (el Consumer)
     * para borrar esta huella concreta de la base de datos y de la lista.
     */
    @FXML
    public void eliminarHuella(ActionEvent event) {
        System.out.println("Eliminar huella ID: " + huella.getId());
        if (onEliminarAction != null) {
            onEliminarAction.accept(huella);
        }
    }

}
