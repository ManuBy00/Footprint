package com.example.footprinttracker.DAO;

import com.example.footprinttracker.Connection.ConnectionDB;
import com.example.footprinttracker.Model.Categoria;
import com.example.footprinttracker.Model.Recomendacion;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class RecomendacionDAO {
    /**
     * Recupera la lista de todas las recomendaciones
     */
    public List<Recomendacion> getAllRecomendaciones() {
        try (Session session = ConnectionDB.getInstance().getSession()) {

            String hql = "FROM Recomendacion r " +
                    "JOIN FETCH r.idCategoria " ;

            Query<Recomendacion> query = session.createQuery(hql, Recomendacion.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}

