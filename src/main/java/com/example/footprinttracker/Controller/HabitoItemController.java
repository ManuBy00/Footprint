package com.example.footprinttracker.Controller;

import com.example.footprinttracker.Model.Habito;
import com.example.footprinttracker.Model.Categoria;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.function.Consumer;


public class HabitoItemController {

    @FXML private CheckBox checkTitulo;
    @FXML private Label lblImpacto;
    @FXML private Label lblDescripcion;

    private Consumer<Habito> alEliminar;
    private Habito habito;
    private Runnable alCambiarEstado; // Callback para avisar al padre


    /**
     * Rellena los elementos visuales del item con los datos del hábito recibido.
     * Configura el título, la descripción de frecuencia, el color de la etiqueta
     * y marca el checkbox si la última fecha de realización coincide con hoy.
     * También guarda las funciones (callbacks) que debe ejecutar al marcar o eliminar.
     */
    public void setDatos(Habito habito, Runnable alCambiarEstado, Consumer<Habito> alEliminar) {
        this.habito = habito;
        this.alCambiarEstado = alCambiarEstado;
        this.alEliminar = alEliminar;

        //  Título (Nombre de la Actividad)
        String nombreActividad = habito.getIdActividad().getNombreActividad();
        checkTitulo.setText(nombreActividad);

        //  Frecuencia y Tipo (Usamos el espacio de descripción)
        // Ejemplo resultado: "📅 Meta: 1 vez / Diario"
        String infoFrecuencia = generarTextoFrecuencia(habito.getFrecuencia(), habito.getTipo());
        lblDescripcion.setText(infoFrecuencia);

        //  Etiqueta de Impacto (Colores)
        Categoria categoria = habito.getIdActividad().getIdCategoria();
        String textoImpacto = "-" + categoria.getFactorEmision() + " " + categoria.getUnidad(); // Ej: -0.5 kg CO2
        lblImpacto.setText(textoImpacto);

        //  Estado (Check si se hizo hoy)
        boolean completadoHoy = esFechaDeHoy(habito.getUltimaFecha());
        checkTitulo.setSelected(completadoHoy);

        // Estilo CSS
        aplicarEstiloEtiqueta(categoria.getNombre());
    }

    /**
     * Se ejecuta al hacer clic en el checkbox.
     * Si se marca, actualiza la fecha del hábito a "ahora mismo". Si se desmarca, pone la fecha a null.
     * Finalmente, avisa al controlador padre (alCambiarEstado) para que recalcule la barra de progreso.
     */
    @FXML
    private void onCheckAction() {
        if (checkTitulo.isSelected()) {
            habito.setUltimaFecha(Instant.now());
        } else {
            habito.setUltimaFecha(null);
        }
        if (alCambiarEstado != null) alCambiarEstado.run();
    }

    /**
     * Comprueba si una fecha guardada (Instant) corresponde al día de hoy según el sistema.
     * Sirve para saber si el hábito debe aparecer marcado o no al abrir la app.
     */
    private boolean esFechaDeHoy(Instant fecha) {
        if (fecha == null) return false;
        LocalDate hoy = LocalDate.now();
        LocalDate fechaHabito = fecha.atZone(ZoneId.systemDefault()).toLocalDate();
        return hoy.isEqual(fechaHabito);
    }

    /**
     * Cambia la clase CSS de la etiqueta de impacto según la categoría.
     * Por ejemplo, pone la etiqueta azul si es Agua o verde si es Reciclaje.
     */
    private void aplicarEstiloEtiqueta(String nombreCategoria) {
        lblImpacto.getStyleClass().removeAll("tag-water", "tag-co2", "tag-circular");
        if (nombreCategoria == null) return;
        switch (nombreCategoria.toLowerCase()) {
            case "agua": case "water": lblImpacto.getStyleClass().add("tag-water"); break;
            case "reciclaje": lblImpacto.getStyleClass().add("tag-circular"); break;
            default: lblImpacto.getStyleClass().add("tag-co2"); break;
        }
    }

    // Método auxiliar para formatear el texto
    private String generarTextoFrecuencia(Integer frecuencia, String tipo) {
        if (frecuencia == null) frecuencia = 1;
        if (tipo == null) tipo = "Diario";

        String veces = (frecuencia == 1) ? "vez" : "veces";

        return "📅 Frecuencia: " + frecuencia + " " + veces + " / " + tipo;
    }

    /**
     * Se ejecuta al pulsar el botón de borrar.
     * Utiliza la función recibida desde el padre (Consumer) para enviarle este hábito
     * concreto y que el controlador principal gestione su eliminación de la base de datos.
     */
    public void onEliminar() {
        if (alEliminar != null) {
            // Le pasamos ESTE hábito al padre para que él lo borre
            alEliminar.accept(this.habito);
        }

    }
}