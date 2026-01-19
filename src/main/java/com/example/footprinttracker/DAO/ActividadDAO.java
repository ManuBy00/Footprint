package com.example.footprinttracker.DAO;

import com.example.footprinttracker.Connection.ConnectionDB;
import com.example.footprinttracker.Model.Actividad;
import com.example.footprinttracker.Model.Categoria;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class ActividadDAO {

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
}
