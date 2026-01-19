package com.example.footprinttracker.DAO;

import com.example.footprinttracker.Connection.ConnectionDB;
import com.example.footprinttracker.Model.Categoria;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public List<Categoria> getAllCategorias() {
        try (Session session = ConnectionDB.getInstance().getSession()) {
            // HQL simple para traer todo
            return session.createQuery("FROM Categoria", Categoria.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // Devuelve lista vacía si falla
        }
    }
}
