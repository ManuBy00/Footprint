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

    /**
     * Registra un nuevo usuario en la base de datos tras realizar múltiples validaciones.
     *
     * Si todas las validaciones pasan, se cifra la contraseña con BCrypt, se asigna la fecha
     * de registro actual y se persiste el usuario.
     *
     * @param nombre      El nombre de pila del usuario.
     * @param email       El correo electrónico (identificador único).
     * @param pass        La contraseña en texto plano.
     * @param confirmPass La repetición de la contraseña para confirmación.
     * @return true si el registro fue exitoso;  false si hubo errores de validación

     */
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

    /**
     * Autentica a un usuario en el sistema.
     * Verifica si el correo existe y si la contraseña proporcionada coincide con el hash almacenado en la base de datos (usando checkPassword).
     * Si las credenciales son correctas, inicializa la sesión global  Sesion.
     *
     * @param email    El correo electrónico del usuario.
     * @param password La contraseña en texto plano para verificar.
     * @return true si el login es correcto y se inicia sesión;  false si las credenciales son inválidas.
     */
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

    /**
     * Actualiza el nombre del usuario que tiene la sesión iniciada.
     */
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

    /**
     * Modifica la contraseña del usuario actual tras verificar su identidad.
     * Por seguridad, requiere que el usuario introduzca su contraseña actual antes de
     * permitir el cambio. La nueva contraseña se cifra inmediatamente con BCrypt antes
     * de guardarse.
     */
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

    /**
     * Elimina permanentemente la cuenta del usuario que tiene la sesión iniciada.
     * Obtiene el ID del usuario de la sesión actual y solicita al DAO su borrado.
     * @return true si la cuenta fue eliminada correctamente de la base de datos.
     */
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
