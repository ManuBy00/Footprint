package com.example.footprinttracker.Services;

import com.example.footprinttracker.DAO.HabitoDAO;
import com.example.footprinttracker.DAO.RecomendacionDAO;
import com.example.footprinttracker.Model.Categoria;
import com.example.footprinttracker.Model.Habito;
import com.example.footprinttracker.Model.Recomendacion;
import com.example.footprinttracker.Utils.Sesion;

import java.util.ArrayList;
import java.util.List;

public class RecomendacionService {
    RecomendacionDAO recomendacionDAO = new RecomendacionDAO();
    HabitoDAO habitoDAO = new HabitoDAO();


    /**
     * Obtiene una lista de recomendaciones personalizadas para el usuario actual.
     * La lógica de personalización se basa en los hábitos del usuario:
     * Obtiene todos los hábitos registrados por el usuario logueado.
     * Identifica las categorías únicas  asociadas a esos hábitos.
     * Filtra la lista de recomendaciones, seleccionando solo aquellas que
     * pertenecen a las categorías en las que el usuario tiene hábitos activos.
     *
     * @return Una lista filtrada de objetos  Recomendacion relevantes para el usuario.
     */
    public List<Recomendacion> getRecomendacionesUsuario(){
        List<Recomendacion> recomendaciones = recomendacionDAO.getAllRecomendaciones();
        List<Habito> habitosUsuario = habitoDAO.getHabitosUsuario(Sesion.getInstance().getUsuarioIniciado());
        List<Integer> categorias = new ArrayList<>();

        //recorremos los habitos para obtener las categorias, a partir de las cuales generar las recomendaciones
        for (Habito habito : habitosUsuario) {
            Integer idCat = habito.getIdActividad().getIdCategoria().getId();
            if (!categorias.contains(idCat)) {
                categorias.add(idCat);
            }
        }

        ArrayList<Recomendacion> recomendacionesFiltradas =  new ArrayList<>();
        for (Recomendacion recomendacion : recomendaciones) {
            if (categorias.contains(recomendacion.getIdCategoria().getId())){
                recomendacionesFiltradas.add(recomendacion);
            }
        }
        return recomendacionesFiltradas;
    }
}
