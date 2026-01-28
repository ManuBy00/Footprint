package com.example.footprinttracker.DAO;

import com.example.footprinttracker.Connection.ConnectionDB;
import com.example.footprinttracker.Model.Actividad;
import com.example.footprinttracker.Model.Categoria;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class ActividadDAO {

    /**
     * Obtiene las actividades disponibles asociadas a una categoría específica.
     *
     * @param categoria El objeto categoría por el cual se desea filtrar.
     * @return Lista de objetos  Actividad que pertenecen a dicha categoría.
     */
    public List<Actividad> getActividadesPorCategoria(Categoria categoria) {
        try (Session session = ConnectionDB.getInstance().getSession()) {
            // Buscamos actividades donde el objeto 'categoria' tenga ese ID
            String hql = "FROM Actividad a WHERE a.idCategoria = :idCat";

            Query<Actividad> query = session.createQuery(hql, Actividad.class);
            query.setParameter("idCat", categoria);

            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Recupera todas las actividades registradas en el sistema
     * @return Una lista con todas las actividades existentes en la base de datos.
     */
    public List<Actividad> getActividades() {
        try (Session session = ConnectionDB.getInstance().getSession()) {
            String hql = "FROM Actividad";
            Query<Actividad> query = session.createQuery(hql, Actividad.class);
            return query.list();
        }
    }

}
