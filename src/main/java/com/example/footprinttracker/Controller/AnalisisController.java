package com.example.footprinttracker.Controller;

import com.example.footprinttracker.DTO.CategoriaEstadistica;
import com.example.footprinttracker.DTO.ImpactoMensual;
import com.example.footprinttracker.Model.Usuario;
import com.example.footprinttracker.Services.AnalisisService; // Importamos el servicio
import com.example.footprinttracker.Utils.Sesion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.net.URL;
import java.text.DecimalFormat;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class AnalisisController implements Initializable {


    // --- ELEMENTOS FXML ---
    @FXML private Label lblImpactoDiario;
    @FXML private Label lblImpactoSemanal;
    @FXML private Label lblImpactoMensual;
    @FXML private PieChart pieChartCategorias;
    @FXML private BarChart<String, Number> barChartMensual;
    @FXML public BarChart barChartComparativa;

    // --- DEPENDENCIAS ---
    private final AnalisisService analisisService; // Usamos el Servicio, no el DAO
    private Usuario usuario;
    private final DecimalFormat df = new DecimalFormat("#.00");

    // Constructor (si usas inyección de dependencias) o inicialización simple
    public AnalisisController() {
        this.analisisService = new AnalisisService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.usuario = Sesion.getInstance().getUsuarioIniciado();

        // Delegamos en métodos privados para mantener el código limpio
        cargarImpactos();
        cargarGraficoSectores();
        cargarGraficoBarras();
        cargarGraficoComparativo();
    }

    /**
     * Carga los indicadores clave de rendimiento (KPIs) de impacto ambiental.
     * Pide al servicio los totales calculados para el día de hoy, la semana actual y el mes,
     * y actualiza las tres etiquetas de texto de la interfaz con los valores formateados.
     */
    private void cargarImpactos() {
        // 1. Pedimos los cálculos al servicio
        Map<String, Double> kpis = analisisService.obtenerImpactos(usuario);

        // 2. Actualizamos la UI
        lblImpactoDiario.setText(df.format(kpis.get("hoy")) + " kg CO₂");
        lblImpactoSemanal.setText(df.format(kpis.get("semana")) + " kg CO₂");
        lblImpactoMensual.setText(df.format(kpis.get("mes")) + " kg CO₂");
    }

    /**
     * Genera el gráfico circular (PieChart) de distribución por categorías.
     * Obtiene la lista de estadísticas, transforma los objetos de datos en el formato
     * que requiere JavaFX y muestra visualmente qué categorías tienen más registros.
     */
    private void cargarGraficoSectores() {
        // 1. Ahora recibimos una lista de objetos bonitos
        List<CategoriaEstadistica> datos = analisisService.obtenerDatosDistribucion(usuario);

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();

        // 2. Usamos getters normales. ¡Mucho más legible!
        for (CategoriaEstadistica item : datos) {
            pieData.add(new PieChart.Data(
                    item.getNombreCategoria(), // Sin (String) fila[0]
                    item.getCantidad()         // Sin (Long) fila[1]
            ));
        }

        pieChartCategorias.setData(pieData);
        pieChartCategorias.setTitle("Huellas registradas");
    }

    /**
     * Construye el gráfico de barras de evolución mensual.
     * Recupera los datos de impacto por mes, convierte el número del mes a su nombre
     * abreviado en español (ej: 1 -> "ene") y dibuja la serie temporal para ver la tendencia del año.
     */
    private void cargarGraficoBarras() {
        // 1. Recibimos una lista limpia de objetos
        List<ImpactoMensual> datos = analisisService.obtenerDatosEvolucionMensual(usuario);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Año Actual");

        // 2. Usamos los getters del DTO. ¡Mucho más seguro y legible!
        for (ImpactoMensual item : datos) {

            // Ya no hay dudas de qué es cada cosa
            int mesNum = item.getMes();
            Double impacto = item.getTotalImpacto();

            String nombreMes = Month.of(mesNum).getDisplayName(TextStyle.SHORT, new Locale("es", "ES"));

            series.getData().add(new XYChart.Data<>(nombreMes, impacto));
        }

        barChartMensual.getData().clear();
        barChartMensual.getData().add(series);
    }

    /**
     * Crea el gráfico comparativo entre el usuario y la comunidad.
     * Genera dos series de datos distintas ("Mi Huella" y "Media Comunidad") y las añade
     * al gráfico para mostrar las barras agrupadas por categoría, facilitando la comparación directa.
     */
    private void cargarGraficoComparativo() {
        Map<String, Double[]> datos = analisisService.obtenerDatosComparativa(usuario);

        XYChart.Series<String, Number> serieUsuario = new XYChart.Series<>();
        serieUsuario.setName("Mi Huella");

        XYChart.Series<String, Number> serieMedia = new XYChart.Series<>();
        serieMedia.setName("Media Comunidad");

        for (Map.Entry<String, Double[]> entry : datos.entrySet()) {
            String categoria = entry.getKey();
            Double[] valores = entry.getValue();

            serieUsuario.getData().add(new XYChart.Data<>(categoria, valores[0]));
            serieMedia.getData().add(new XYChart.Data<>(categoria, valores[1]));
        }

        barChartComparativa.getData().clear();
        barChartComparativa.getData().addAll(serieUsuario, serieMedia);
    }
}