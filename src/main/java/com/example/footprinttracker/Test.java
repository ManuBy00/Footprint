package com.example.footprinttracker;

import com.example.footprinttracker.DAO.HuellaDAO;
import com.example.footprinttracker.DAO.UsuarioDAO;
import com.example.footprinttracker.Model.Habito;
import com.example.footprinttracker.Model.Huella;
import com.example.footprinttracker.Model.Usuario;
import com.example.footprinttracker.Utils.Sesion;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Test {
    public static void main(String[] args) {
        UsuarioDAO dao = new UsuarioDAO();
        String nombre = "Pepe";
        String email = "user@gmail.com";
        String password = "password";
        Instant now = Instant.now();
        Habito habito = new Habito();
        Huella huella = new Huella();
        Set<Huella> huellas = new HashSet<Huella>();


        HuellaDAO huellaDAO = new HuellaDAO();


    }
}
