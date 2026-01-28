package com.example.footprinttracker.DAO;

import com.example.footprinttracker.Connection.ConnectionDB;
import com.example.footprinttracker.Model.Usuario;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.Connection;
import java.util.List;

public class UsuarioDAO {

    /**
     * Guarda un nuevo usuario en la base de datos.
     * Abre una transacción, intenta guardar el objeto y confirma los cambios (commit).
     * Si falla, deshace los cambios (rollback).
     *
     * @param usuario El objeto con los datos a guardar.
     * @return true si se guardó correctamente,  false si hubo error.
     */
    public boolean addUsuario(Usuario usuario) {
        boolean inserted = false;
        Transaction tx = null;

        try(Session session = ConnectionDB.getInstance().getSession()){
            tx = session.beginTransaction();
            session.persist(usuario);
            tx.commit();
            inserted = true;
        }catch(Exception ex){
            if(tx != null){
                tx.rollback();
            }
            ex.printStackTrace();
        }
        return inserted;
    }

    /**
     * Elimina un usuario de la base de datos buscando por su ID.
     * Primero recupera el usuario y luego lo borra dentro de una transacción segura.
     *
     * @param id El identificador único del usuario a borrar.
     * @return true si se eliminó con éxito.
     */
    public boolean deleteUsuario(int id) {
        boolean deleted = false;
        Transaction tx = null;
        try(Session session = ConnectionDB.getInstance().getSession()){
            tx = session.beginTransaction();
            Usuario usuario = session.get(Usuario.class, id);
            session.remove(usuario);
            tx.commit();
            deleted = true;
        }catch(Exception ex){
            if(tx != null){
                tx.rollback();
            }
            ex.printStackTrace();
        }
        return deleted;
    }


    /**
     * Actualiza los datos de un usuario existente.
     * Utiliza merge para sobrescribir los datos en la base de datos
     * con los del objeto que le pasamos.
     *
     * @param usuario El objeto usuario con los datos modificados.
     * @return true si la actualización fue exitosa.
     */
    public boolean updateUsuario(Usuario usuario) {
        boolean updated = false;
        Transaction tx = null;
        try(Session session = ConnectionDB.getInstance().getSession()){
            tx = session.beginTransaction();
            session.merge(usuario);
            tx.commit();
            updated = true;

        } catch (Exception e) {
            if(tx != null){
                tx.rollback();
            }
            e.printStackTrace();
        }
        return updated;
    }

    /**
     * Busca un usuario específico usando su correo electrónico.
     * Útil para el login o validaciones, ya que el email debe ser único.
     *
     * @param email El correo a buscar.
     * @return El objeto {@link Usuario} encontrado o {@code null} si no existe.
     */
    public Usuario getUsuarioByEmail(String email){
        Usuario usuario = null;
        try(Session session = ConnectionDB.getInstance().getSession()){
            String hql = "FROM Usuario u WHERE u.email = :email";
            Query<Usuario> query = session.createQuery(hql, Usuario.class);
            query.setParameter("email", email);
            usuario = query.uniqueResult();

            return usuario;
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
}
