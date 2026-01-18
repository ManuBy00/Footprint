package com.example.footprinttracker.DAO;

import com.example.footprinttracker.Connection.ConnectionDB;
import com.example.footprinttracker.Model.Usuario;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.Connection;
import java.util.List;

public class UsuarioDAO {



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



    public Usuario getUsuarioByID(int id){
        try(Session session = ConnectionDB.getInstance().getSession()){
            return session.get(Usuario.class, id);
        }
    }

    public List<Usuario> getUsuarios(){
        try(Session session = ConnectionDB.getInstance().getSession()){
            String query = "FROM Usuario";
            return session.createQuery(query).getResultList();
        }
    }

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
