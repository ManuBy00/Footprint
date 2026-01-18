package com.example.footprinttracker.Connection;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class ConnectionDB {

    //1. Atributo estatico de la misma clase (instance)
    private static ConnectionDB instance;
    //2. Atributo para el sessionFactory
    private SessionFactory sessionFactory;

    //3. Constructor privado
    private ConnectionDB() {
        try {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al construir la SessionFactory");
        }
    }

    //4. Metodo publico y estático para devolver la instancia de la sesión
    public static ConnectionDB getInstance() {
        if (instance == null) {
            instance = new ConnectionDB();
        }
        return instance;
    }

    //5. Metodo public para abrir la sesión
    public Session getSession() {return sessionFactory.openSession();}

    //6. Metodo que cierra SessionFactory
    public void close(){
        if(sessionFactory!=null&&sessionFactory.isOpen()){
            sessionFactory.close();
        }
    }

}
