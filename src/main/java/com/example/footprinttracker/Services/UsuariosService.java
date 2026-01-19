package com.example.footprinttracker.Services;

import com.example.footprinttracker.DAO.UsuarioDAO;
import com.example.footprinttracker.Model.Habito;
import com.example.footprinttracker.Model.Usuario;
import com.example.footprinttracker.Utils.Seguridad;
import com.example.footprinttracker.Utils.Sesion;
import com.example.footprinttracker.Utils.Utilidades;
import org.mindrot.jbcrypt.BCrypt;

import java.time.Instant;
import java.util.HashSet;

public class UsuariosService {
    UsuarioDAO usuarioDAO = new UsuarioDAO();

    public boolean registrarUsuario(String nombre, String email, String pass, String confirmPass) {

        // 1. Validaciones
        if (nombre.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            Utilidades.mostrarAlerta("Error", "Por favor, rellena todos los campos.");
            return false;
        }

        if (!pass.equals(confirmPass)) {
            Utilidades.mostrarAlerta("Error", "Las contraseñas no coinciden.");
            return false;
        }

        if (!Utilidades.validarCorreo(email)) {
            Utilidades.mostrarAlerta("Error", "Correo no válido.");
            return false;
        }

        if (usuarioDAO.getUsuarioByEmail(email) != null) {
            Utilidades.mostrarAlerta("Error", "El correo ya existe.");
            return false;
        }

        // 2. Crear objeto Usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setEmail(email);
        // IMPORTANTE: Ponemos la fecha de hoy
        // Asegúrate de tener este campo en tu entidad Usuario o bórralo si no lo usas
        nuevoUsuario.setFechaRegistro(Instant.now());
        nuevoUsuario.setHuellas(new HashSet<>());
        nuevoUsuario.setHabito(new Habito());

        // 3. Encriptar contraseña
        String hashedPass = BCrypt.hashpw(pass, BCrypt.gensalt());
        nuevoUsuario.setPassword(hashedPass);

        // 4. Guardar en BD
        return usuarioDAO.addUsuario(nuevoUsuario);
    }

    public boolean login(String email, String password) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuarioLoged = usuarioDAO.getUsuarioByEmail(email);
        System.out.println("CONTRASEÑA: " + password);


        // 3. Verificar Credenciales
        if (usuarioLoged != null && usuarioLoged.checkPassword(password)) {
            // --- LOGIN ÉXITOSO ---
            Sesion.getInstance().logIn(usuarioLoged);
            return true;
        } else {
            return false;
        }
    }

    public boolean cambiarNombre(String newNombre) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuario = Sesion.getInstance().getUsuarioIniciado();
        if (newNombre.length() < 4) {
            return false;
        }else {
            usuario.setNombre(newNombre);
            usuarioDAO.updateUsuario(usuario);
            return true;
        }
    }

    public boolean cambiarPassword(String passActual,  String passNuevo) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuario = Sesion.getInstance().getUsuarioIniciado();
        if (usuario.checkPassword(passActual)) {
            String passHashed = BCrypt.hashpw(passNuevo, BCrypt.gensalt());
            usuario.setPassword(passHashed);
            usuarioDAO.updateUsuario(usuario);
            return true;
        }else {
            return false;
        }
    }

    public boolean eliminarUsuario() {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuario = Sesion.getInstance().getUsuarioIniciado();
        if(usuarioDAO.deleteUsuario(usuario.getId())){
            return true;
        }else {
            return false;
        }
    }

    public void logOut() {
        Sesion.getInstance().logOut();
    }
}
