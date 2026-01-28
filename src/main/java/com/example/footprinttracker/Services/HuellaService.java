package com.example.footprinttracker.Services;

import com.example.footprinttracker.DAO.ActividadDAO;
import com.example.footprinttracker.DAO.CategoriaDAO;
import com.example.footprinttracker.DAO.HuellaDAO;
import com.example.footprinttracker.Model.Actividad;
import com.example.footprinttracker.Model.Categoria;
import com.example.footprinttracker.Model.Huella;
import com.example.footprinttracker.Model.Usuario;
import com.example.footprinttracker.Utils.Sesion;

import java.time.LocalDate;
import java.util.List;

public class HuellaService {
    Usuario usuarioLoged = Sesion.getInstance().getUsuarioIniciado();
    HuellaDAO dao = new HuellaDAO();
    CategoriaDAO cDao = new CategoriaDAO();
    ActividadDAO aDao = new ActividadDAO();


    /**
     * Registra una nueva huella de carbono en el sistema.
     * Crea una instancia de {@link Huella} con los datos proporcionados y solicita al DAO que la persista en la base de datos.
     *
     * @param categoria La categoría a la que pertenece la actividad (ej: Transporte).
     * @param actividad La actividad específica realizada (ej: Viaje en coche).
     * @param cantidad  El valor numérico del consumo o distancia.
     * @param unidad    La unidad de medida asociada (km, litros, kWh, etc.).
     * @return true si la huella se guardó correctamente, false en caso contrario.
     */
    public boolean addHuella(Categoria categoria, Actividad actividad, Integer cantidad, String unidad) {
        Huella huella = new Huella(categoria, actividad, cantidad, unidad);
        if (dao.addHuella(huella)){
            return true;
        }else {
            return  false;
        }
    }

    /**
     * Recupera el historial completo de huellas del usuario actual.
     * Utiliza la sesión del usuario logueado ({@code usuarioLoged}) para filtrar los resultados.
     * @return Una lista de objetos {@link Huella} asociados al usuario.
     */
    public List<Huella> cargarHuellasUsuario(){
        return dao.getHuellasByUsuario(usuarioLoged);
    }

    /**
     * Obtiene el catálogo maestro de categorías disponibles en el sistema.
     *
     * @return Una lista con todas las categorías existentes.
     */
    public List<Categoria> cargarCategorias(){
        return cDao.getAllCategorias();
    }


    public List<Actividad> cargarActividadesPorCategoria(Categoria categoria){
        return aDao.getActividadesPorCategoria(categoria);
    }

    /**
     * Elimina un registro de huella de carbono de la base de datos.
     *
     * @param huella El objeto huella que se desea eliminar.
     * @return {@code true} si la eliminación fue exitosa, {@code false} si hubo un error.
     */
    public boolean deleteHuella(Huella huella) {
        if(dao.deleteHuella(huella)){
            return true;
        }else {
            return false;
        }
    }

    /**
     * Calcula el impacto total de huella de carbono para un mes y año específicos.
     * La fórmula aplicada es:  Sumatoria (Valor de consumo * Factor de Emisión de la Categoría)
     * Incluye trazas de depuración en consola para verificar los valores procesados.
     *
     * @param mes El mes a consultar
     * @param year El año a consultar
     * @return El total acumulado de kg de CO2
     */
    public double calcularHuellaMensual(int mes, int year){
        List<Huella> huellas = dao.obtenerHuellasMensual(mes, year);
        double impacto = 0;
        // DEBUG: Comprueba si realmente hay huellas
        System.out.println("Huellas encontradas este mes: " + huellas.size());
        for (Huella huella : huellas){
            System.out.println("valor de la huella: " + huella.getValor() + "\n valor de factor: " + huella.getIdActividad().getIdCategoria().getFactorEmision());
            impacto += huella.getValor() * huella.getIdActividad().getIdCategoria().getFactorEmision();
        }
        return impacto;
    }


    /**
     * Cuenta el número total de registros de huella realizados en el mes actual.
     * Obtiene la fecha actual del sistema para determinar el mes y año corriente.
     * @return El número entero de registros encontrados para este mes.
     */
    public int registroMensual(){
        LocalDate fecha = LocalDate.now();
        int mes = fecha.getMonth().getValue();
        int year = fecha.getYear();
        int numRegistros =  dao.obtenerHuellasMensual(mes, year).toArray().length;
        return numRegistros;
    }

    public String calcularRangoUsuario(){
        int mes = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();

        double mediaGlobal = dao.mediaMensualGlobal(mes, year);
        double mediaUsuario = calcularHuellaMensual(mes, year);

        double margen = mediaGlobal * 0.10;

        double limiteInferior = mediaGlobal - margen; // Ej: 100 - 10 = 90
        double limiteSuperior = mediaGlobal + margen; // Ej: 100 + 10 = 110

        if (mediaUsuario < limiteInferior) {
            return "Eco-Friendly";

        } else if (mediaUsuario > limiteSuperior) {
            return "Consumidor";

        } else {
            // Está en la zona intermedia (entre 90 y 110)
            return "Sostenible";
        }
    }
}
