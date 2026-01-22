package com.example.footprinttracker.DAO;

import com.example.footprinttracker.Connection.ConnectionDB;
import com.example.footprinttracker.Model.Huella;
import com.example.footprinttracker.Model.Usuario;
import com.example.footprinttracker.Utils.Sesion;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class HuellaDAO {

    public boolean addHuella(Huella huella) {
        Transaction tx = null;
        try (Session session = ConnectionDB.getInstance().getSession()) {
            tx = session.beginTransaction();
            session.persist(huella);
            tx.commit();
            return true;
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            ex.printStackTrace();
            return false;
        }
    }

    public boolean deleteHuella(Huella huella) {
        Transaction tx = null;
        try (Session session = ConnectionDB.getInstance().getSession()) {
            tx = session.beginTransaction();
            Huella h = session.get(Huella.class, huella.getId());
            if (h != null) {
                session.remove(h);
                tx.commit();
                return true;
            }
            return false;
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            ex.printStackTrace();
            return false;
        }
    }

    public boolean updateHuella(Huella huella) {
        boolean updated = false;
        Transaction tx = null;
        try (Session session = ConnectionDB.getInstance().getSession()) {
            tx = session.beginTransaction();
            session.merge(huella);
            tx.commit();
            updated = true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return updated;
    }

    // Obtener todas las huellas de un usuario concreto (Para su historial)
    public List<Huella> getHuellasByUsuario(Usuario usuario) {
        try (Session session = ConnectionDB.getInstance().getSession()) {

            // 1. "JOIN FETCH h.idActividad a" -> Carga la Actividad y le pone el alias 'a'
            // 2. "JOIN FETCH a.idCategoria"   -> Usa el alias 'a' para cargar también la Categoría
            String hql = "FROM Huella h " +
                    "JOIN FETCH h.idActividad a " +
                    "JOIN FETCH a.idCategoria " +
                    "WHERE h.idUsuario.id = :uid " + // O h.idUsuario.id si no lo cambiaste
                    "ORDER BY h.fecha DESC";

            Query<Huella> query = session.createQuery(hql, Huella.class);
            query.setParameter("uid", usuario.getId());

            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Huella> obtenerHuellasMensual(int mes, int year) {
        try (Session session = ConnectionDB.getInstance().getSession()){
            Usuario usuario = Sesion.getInstance().getUsuarioIniciado();
            String hql = "From Huella h " +
                    "JOIN FETCH h.idActividad a " +
                    "JOIN FETCH a.idCategoria " +
                    "WHERE h.idUsuario.id = :uid " +
                    "AND month(h.fecha) = :mes " +
                    "AND year(h.fecha) = :year " +
                    "ORDER BY h.fecha DESC";

            Query<Huella> query = session.createQuery(hql, Huella.class);
            query.setParameter("uid", usuario.getId());
            query.setParameter("mes", mes);
            query.setParameter("year", year);

            return query.list();
        }
    }


}
