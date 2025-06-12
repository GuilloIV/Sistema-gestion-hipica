package mx.uv.feaa.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import java.time.LocalDateTime;
import java.util.Objects;

}
public abstract class Usuario {
    protected String id;
    protected String nombreUsuario;
    protected String email;
    protected String password;
    protected boolean activo;
    protected LocalDateTime fechaRegistro;
    protected LocalDateTime ultimoAcceso;
    protected String tipoUsuario;

    // Constructor por defecto
    public Usuario() {
        this.activo = true;
        this.fechaRegistro = LocalDateTime.now();
        this.ultimoAcceso = LocalDateTime.now();
    }


    // Constructor con parámetros básicos
    public Usuario(String nombreUsuario, String email) {
        this();
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.tipoUsuario = this.getClass().getSimpleName();
    }

    // Constructor completo
    public Usuario(String id, String nombreUsuario, String email, String password) {
        this();
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.password = password;
        this.tipoUsuario = this.getClass().getSimpleName();
    }

    // Métodos de negocio
    /**
     * Valida el formato del email
     */
    public boolean validarEmail() {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        // Validación básica de email
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * Valida la fortaleza de la contraseña
     */
    public boolean validarPassword() {
        if (password == null || password.length() < 8) {
            return false;
        }

        // Debe contener al menos una letra mayúscula, una minúscula y un número
        boolean tieneMayuscula = password.matches(".*[A-Z].*");
        boolean tieneMinuscula = password.matches(".*[a-z].*");
        boolean tieneNumero = password.matches(".*\\d.*");

        return tieneMayuscula && tieneMinuscula && tieneNumero;
    }

    /**
     * Valida que el nombre de usuario sea único y válido
     */
    public boolean validarNombreUsuario() {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            return false;
        }

        // Debe tener entre 3 y 20 caracteres
        if (nombreUsuario.length() < 3 || nombreUsuario.length() > 20) {
            return false;
        }

        // Solo letras, números y guiones bajos
        return nombreUsuario.matches("^[a-zA-Z0-9_]+$");
    }

    /**
     * Activa la cuenta del usuario
     */
    public boolean activarCuenta() {
        if (!activo) {
            this.activo = true;
            return true;
        }
        return false;
    }

    /**
     * Desactiva la cuenta del usuario
     */
    public boolean desactivarCuenta() {
        if (activo) {
            this.activo = false;
            return true;
        }
        return false;
    }

    /**
     * Actualiza la fecha del último acceso
     */
    public void actualizarUltimoAcceso() {
        this.ultimoAcceso = LocalDateTime.now();
    }

    /**
     * Cambia la contraseña del usuario
     */
    public boolean cambiarPassword(String passwordActual, String passwordNuevo) {
        if (!verificarPassword(passwordActual)) {
            return false;
        }

        this.password = passwordNuevo;
        return validarPassword();
    }

    /**
     * Verifica si la contraseña proporcionada es correcta
     */
    public boolean verificarPassword(String passwordIngresado) {
        return password != null && password.equals(passwordIngresado);
    }

    /**
     * Actualiza la información básica del usuario
     */
    public boolean actualizarInformacion(String nuevoEmail) {
        if (nuevoEmail != null && !nuevoEmail.equals(this.email)) {
            String emailAnterior = this.email;
            this.email = nuevoEmail;

            if (!validarEmail()) {
                this.email = emailAnterior; // Revertir si no es válido
                return false;
            }
        }
        return true;
    }

    /**
     * Obtiene información resumida del usuario
     */
    public String obtenerResumen() {
        return String.format("Usuario: %s (%s) - %s - Activo: %s - Registrado: %s",
                nombreUsuario, tipoUsuario, email, activo ? "Sí" : "No",
                fechaRegistro != null ? fechaRegistro.toLocalDate() : "N/A");
    }

    /**
     * Verifica si el usuario puede realizar operaciones
     */
    public boolean puedeOperar() {
        return isActivo() && validarEmail() && validarNombreUsuario();
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;}

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    @Override
    public String toString() {
        return String.format("Usuario{id='%s', nombreUsuario='%s', email='%s', tipo='%s', activo=%s}",
                id, nombreUsuario, email, tipoUsuario, activo);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Usuario usuario = (Usuario) obj;
        return Objects.equals(id, usuario.id) ||
                Objects.equals(nombreUsuario, usuario.nombreUsuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombreUsuario);
    }
}
