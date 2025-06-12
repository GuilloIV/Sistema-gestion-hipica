package mx.uv.feaa.model.entidades;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract class Usuario {
    protected String idUsuario;
    protected String nombreUsuario;
    protected String email;
    protected String password;
    protected boolean activo;
    protected LocalDateTime fechaRegistro;
    protected LocalDateTime ultimoAcceso;
    protected String tipoUsuario;

    public Usuario() {
        this.activo = true;
        this.fechaRegistro = LocalDateTime.now();
        this.ultimoAcceso = LocalDateTime.now();
    }

    public Usuario(String nombreUsuario, String email) {
        this();
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.tipoUsuario = getTipoUsuarioEspecifico();
    }

    public Usuario(String idUsuario, String nombreUsuario, String email, String password) {
        this();
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.password = password;
        this.tipoUsuario = getTipoUsuarioEspecifico();
    }

    public abstract String getTipoUsuarioEspecifico();

    public String getPrefijoTabla() {
        return switch (getTipoUsuarioEspecifico()) {
            case "APOSTADOR" -> "a";
            case "CRIADOR" -> "c";
            default -> "";
        };
    }

    public boolean validarEmail() {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    public boolean validarPassword() {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean tieneMayuscula = password.matches(".*[A-Z].*");
        boolean tieneMinuscula = password.matches(".*[a-z].*");
        boolean tieneNumero = password.matches(".*\\d.*");
        return tieneMayuscula && tieneMinuscula && tieneNumero;
    }

    public boolean validarNombreUsuario() {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            return false;
        }
        if (nombreUsuario.length() < 3 || nombreUsuario.length() > 20) {
            return false;
        }
        return nombreUsuario.matches("^[a-zA-Z0-9_]+$");
    }

    public boolean activarCuenta() {
        if (!activo) {
            this.activo = true;
            return true;
        }
        return false;
    }

    public boolean desactivarCuenta() {
        if (activo) {
            this.activo = false;
            return true;
        }
        return false;
    }

    public void actualizarUltimoAcceso() {
        this.ultimoAcceso = LocalDateTime.now();
    }

    public boolean cambiarPassword(String passwordActual, String passwordNuevo) {
        if (!verificarPassword(passwordActual)) {
            return false;
        }
        this.password = passwordNuevo;
        return validarPassword();
    }

    public boolean verificarPassword(String passwordIngresado) {
        return password != null && password.equals(passwordIngresado);
    }

    public boolean actualizarInformacion(String nuevoEmail) {
        if (nuevoEmail != null && !nuevoEmail.equals(this.email)) {
            String emailAnterior = this.email;
            this.email = nuevoEmail;
            if (!validarEmail()) {
                this.email = emailAnterior;
                return false;
            }
        }
        return true;
    }

    public String obtenerResumen() {
        return String.format("Usuario: %s (%s) - %s - Activo: %s - Registrado: %s",
                nombreUsuario, tipoUsuario, email, activo ? "Sí" : "No",
                fechaRegistro != null ? fechaRegistro.toLocalDate() : "N/A");
    }

    public boolean puedeOperar() {
        return isActivo() && validarEmail() && validarNombreUsuario();
    }

    // Getters and Setters
    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }
    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public LocalDateTime getUltimoAcceso() { return ultimoAcceso; }
    public void setUltimoAcceso(LocalDateTime ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }
    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    @Override
    public String toString() {
        return String.format("Usuario{id='%s', nombreUsuario='%s', email='%s', tipo='%s', activo=%s}",
                idUsuario, nombreUsuario, email, tipoUsuario, activo);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Usuario usuario = (Usuario) obj;
        return Objects.equals(idUsuario, usuario.idUsuario) ||
                Objects.equals(nombreUsuario, usuario.nombreUsuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, nombreUsuario);
    }
}