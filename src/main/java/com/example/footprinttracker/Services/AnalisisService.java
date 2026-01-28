package com.example.footprinttracker.Services;

import com.example.footprinttracker.DAO.HuellaDAO;
import com.example.footprinttracker.DTO.CategoriaEstadistica;
import com.example.footprinttracker.DTO.ImpactoCategoria;
import com.example.footprinttracker.DTO.ImpactoMensual;
import com.example.footprinttracker.Model.Usuario;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalisisService {

    private final HuellaDAO huellaDAO;

    public AnalisisService() {
        this.huellaDAO = new HuellaDAO();
    }

    /**
     * Calcula los KPIs (Diario, Semanal, Mensual) y los devuelve en un mapa.
     * Claves: "hoy", "semana", "mes".
     */
    public Map<String, Double> obtenerImpactos(Usuario usuario) {
        Map<String, Double> resultados = new HashMap<>();
        LocalDate hoy = LocalDate.now();

        //  Calculamos DIARIO
        Double diario = huellaDAO.obtenerImpactoEnRango(usuario, hoy, hoy);
        resultados.put("hoy", diario);

        //  Calculamos SEMANAL (Lunes a Domingo actual)
        LocalDate inicioSemana = hoy.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate finSemana = hoy.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));
        Double semanal = huellaDAO.obtenerImpactoEnRango(usuario, inicioSemana, finSemana);
        resultados.put("semana", semanal);

        //  Calculamos MENSUAL (Día 1 al último día del mes)
        LocalDate inicioMes = hoy.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate finMes = hoy.with(TemporalAdjusters.lastDayOfMonth());
        Double mensual = huellaDAO.obtenerImpactoEnRango(usuario, inicioMes, finMes);
        resultados.put("mes", mensual);

        return resultados;
    }

    /**
     * Obtiene los datos para el gráfico de sectores.
     * @return Lista de objetos [NombreCategoria, Cantidad]
     */
    public List<CategoriaEstadistica> obtenerDatosDistribucion(Usuario usuario) {
        return huellaDAO.obtenerDistribucionCategorias(usuario);
    }

    /**
     * Obtiene los datos para el gráfico de barras del año actual.
     * @return Lista de objetos [NumeroMes, ImpactoTotal]
     */
    public List<ImpactoMensual> obtenerDatosEvolucionMensual(Usuario usuario) {
        int yearActual = LocalDate.now().getYear();
        return huellaDAO.obtenerImpactoMensual(usuario, yearActual);
    }


    /**
     * Genera los datos necesarios para comparar la huella de carbono del usuario
     * frente a la media de la comunidad, desglosado por categorías.
     *
     * El método realiza los siguientes pasos:
     * Obtiene el impacto del usuario actual por categoría.
     * Obtiene el impacto global de todos los usuarios.
     * Calcula la media aritmética dividiendo el global entre el número total de usuarios con huellas.
     * Fusiona ambos datos en un mapa
     *
     * @param usuario El usuario conectado del que se quiere analizar el impacto.
     * @return Un Map donde la clave es el nombre de la categoría (ej: "Transporte")
     * y el valor es un array de  Double con dos posiciones:
     *[0]: Huella del usuario actual.
     *[1]: Media de la comunidad.
     */
    public Map<String, Double[]> obtenerDatosComparativa(Usuario usuario) {
        Map<String, Double[]> resultado = new HashMap<>();

        //  Obtenemos los datos
        List<ImpactoCategoria> datosUsuario = huellaDAO.obtenerImpactoPorCategoriaUsuario(usuario);
        List<ImpactoCategoria> datosGlobales = huellaDAO.obtenerImpactoGlobalPorCategoria();

        Long totalUsuarios = huellaDAO.contarUsuariosConHuellas();
        if (totalUsuarios == 0) totalUsuarios = 1L;

        // Procesar datos GLOBALES
        for (ImpactoCategoria item : datosGlobales) {
            String cat = item.getCategoria();
            // División con decimales real
            Double media = item.getImpactoTotal() / totalUsuarios;
            resultado.put(cat, new Double[]{0.0, media});
        }

        // Procesar datos del USUARIO
        for (ImpactoCategoria item : datosUsuario) {
            String cat = item.getCategoria();
            Double miImpacto = item.getImpactoTotal(); // Ya es Double, no hay que convertir

            if (resultado.containsKey(cat)) {
                resultado.get(cat)[0] = miImpacto;
            } else {
                resultado.put(cat, new Double[]{miImpacto, 0.0});
            }
        }

        return resultado;
    }
}
