package com.example.footprinttracker.DAO;

import com.example.footprinttracker.Connection.ConnectionDB;
import com.example.footprinttracker.Model.Habito;
import com.example.footprinttracker.Model.Huella;
import com.example.footprinttracker.Model.Usuario;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class HabitoDAO {

    /**
     * Guarda un nuevo hábito en la base de datos.
     *
     * Abre una transacción segura para persistir el objeto. Si algo falla,
     * se deshacen los cambios (rollback) para mantener la integridad de los datos.
     *
     * @param habito El objeto hábito con los datos a guardar.
     * @return  true si se guardó correctamente,  false si hubo error.
     */
    public boolean addHabito(Habito habito) {
        Transaction tx = null;
        try (Session session = ConnectionDB.getInstance().getSession()) {
            tx = session.beginTransaction();
            session.persist(habito);
            tx.commit();
            return true;
        }catch (Exception ex) {
            if (tx != null) tx.rollback();
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina un hábito existente de la base de datos.
     *
     * @param habito El objeto hábito a eliminar.
     * @return  true si la eliminación fue exitosa.
     */
    public boolean deleteHabito(Habito habito) {
        Transaction tx = null;
        try (Session session = ConnectionDB.getInstance().getSession()) {
            tx = session.beginTransaction();
            session.remove(habito);
            tx.commit();
            return true;
        }catch (Exception ex) {
            if (tx != null) tx.rollback();
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Actualiza la información de un hábito ya existente.
     * Utiliza merge para actualizar el registro en la base de datos
     * con los nuevos valores del objeto modificado.
     *
     * @param habito El objeto con los datos actualizados.
     * @return  true si la actualización se completó correctamente.
     */
    public boolean updateHabito(Habito habito) {
        Transaction tx = null;
        try (Session session = ConnectionDB.getInstance().getSession()) {
            tx = session.beginTransaction();
            session.merge(habito);
            tx.commit();
            return true;
        }catch (Exception ex) {
            if (tx != null) tx.rollback();
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene la lista de hábitos activos de un usuario específico
     * @param usuario El usuario del que se quieren recuperar los hábitos.
     * @return Lista de hábitos ordenada por fecha (ascendente).
     */
    public List<Habito> getHabitosUsuario(Usuario usuario) {
        try (Session session = ConnectionDB.getInstance().getSession()) {
            String hql = "FROM Habito h " +
                    "JOIN FETCH h.idActividad a " +
                    "JOIN FETCH a.idCategoria " +
                    "WHERE h.idUsuario.id = :idUsuario " +
                    "ORDER BY h.ultimaFecha ASC";
            Query query = session.createQuery(hql,  Habito.class);
            query.setParameter("idUsuario", usuario.getId());
            return query.list();
        }
    }
}

