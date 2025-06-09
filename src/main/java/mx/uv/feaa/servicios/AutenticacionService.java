package mx.uv.feaa.servicios;


import mx.uv.feaa.model.Usuario;
import mx.uv.feaa.model.Apostador;
import mx.uv.feaa.model.Criador;
import mx.uv.feaa.excepciones.CredencialesInvalidasException;
import mx.uv.feaa.excepciones.UsuarioNoEncontradoException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio para autenticación y gestión de usuarios.
 */
public class AutenticacionService {
    private Map<String, Usuario> usuariosRegistrados; // Simula una base de datos en memoria

    public AutenticacionService() {
        this.usuariosRegistrados = new HashMap<>();
        // Ejemplo de usuarios precargados (en producción, usar una base de datos)
        usuariosRegistrados.put("apostador1", new Apostador("apostador1", "apostador@uv.mx", "Juan Pérez", "5551234567"));
        usuariosRegistrados.put("criador1", new Criador("criador1", "criador@uv.mx", "Licencia-123", LocalDate.now().plusYears(1), "Hidalgo 123", "5559876543", "Haras UV"));
    }

    /**
     * Realiza el login y devuelve el tipo de usuario.
     * @param nombreUsuario Nombre de usuario.
     * @param password Contraseña.
     * @return Tipo de usuario (ej. "Apostador", "Criador").
     * @throws CredencialesInvalidasException Si las credenciales son incorrectas.
     * @throws UsuarioNoEncontradoException Si el usuario no existe.
     */
    public String login(String nombreUsuario, String password)
            throws CredencialesInvalidasException, UsuarioNoEncontradoException {

        Usuario usuario = usuariosRegistrados.get(nombreUsuario);

        if (usuario == null) {
            throw new UsuarioNoEncontradoException("Usuario no registrado.");
        }

        if (!usuario.verificarPassword(password)) {
            throw new CredencialesInvalidasException("Contraseña incorrecta.");
        }

        if (!usuario.isActivo()) {
            throw new CredencialesInvalidasException("Cuenta inactiva.");
        }

        usuario.actualizarUltimoAcceso();
        return usuario.getTipoUsuario(); // Devuelve "Apostador", "Criador", etc.
    }

    /**
     * Registra un nuevo usuario en el sistema.
     */
    public void registrarUsuario(Usuario usuario) {
        if (usuario != null && usuario.validarNombreUsuario() && usuario.validarEmail()) {
            usuariosRegistrados.put(usuario.getNombreUsuario(), usuario);
        }
    }
}
