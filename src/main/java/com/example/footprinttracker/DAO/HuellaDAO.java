package com.example.footprinttracker.DAO;

import com.example.footprinttracker.Connection.ConnectionDB;
import com.example.footprinttracker.Model.Huella;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

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

    public boolean deleteHuella(int idHuella) {
        Transaction tx = null;
        try (Session session = ConnectionDB.getInstance().getSession()) {
            tx = session.beginTransaction();
            Huella h = session.get(Huella.class, idHuella);
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
    public List<Huella> getHuellasByUsuario(int idUsuario) {
        try (Session session = ConnectionDB.getInstance().getSession()) {
            String hql = "FROM Huella h WHERE h.Usuario.id = :uid ORDER BY h.fecha DESC";
            Query<Huella> query = session.createQuery(hql, Huella.class);
            query.setParameter("uid", idUsuario);
            return query.list();
        }
    }


}
