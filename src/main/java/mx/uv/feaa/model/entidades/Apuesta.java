package mx.uv.feaa.model.entidades;

import mx.uv.feaa.enumeracion.EstadoApuesta;
import mx.uv.feaa.enumeracion.TipoApuesta;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Apuesta {
    protected String idApuesta;
    protected String idUsuario; // Reference to Apostador
    protected String idCarrera; // Reference to Carrera
    protected double montoApostado;
    protected LocalDateTime fechaApuesta;
    protected EstadoApuesta estado;
    protected TipoApuesta tipoApuesta;
    protected Map<String, Object> seleccion;
    protected List<ApuestaSeleccion> selecciones;
    protected double cuotaAplicada;
    protected double montoGanado;
    private Usuario apostador;

    public Apuesta(String idApuesta, String idUsuario, String idCarrera,
                   TipoApuesta tipoApuesta, double montoApostado) {
        this.idApuesta = idApuesta;
        this.idUsuario = idUsuario;
        this.idCarrera = idCarrera;
        this.tipoApuesta = tipoApuesta;
        this.montoApostado = montoApostado;
        this.fechaApuesta = LocalDateTime.now();
        this.estado = EstadoApuesta.PENDIENTE;
        this.seleccion = new HashMap<>(); // Corregido: era new ApuestaSeleccion<>()
        this.selecciones = new ArrayList<>();
        this.cuotaAplicada = 0.0;
        this.montoGanado = 0.0;
    }

    public abstract double calcularDividendo(Resultado resultado);
    public abstract boolean esGanadora(Resultado resultado);

    public boolean validarApuesta() {
        return montoApostado > 0 &&
                seleccion != null && !seleccion.isEmpty();
    }

    public void procesar(Resultado resultado) {
        if (resultado == null || !estado.estaActiva()) return;

        if (esGanadora(resultado)) {
            estado = EstadoApuesta.GANADORA;
            montoGanado = calcularDividendo(resultado);
        } else {
            estado = EstadoApuesta.PERDEDORA;
            montoGanado = 0.0;
        }
    }

    public boolean cancelar() {
        if (estado.puedeCancelarse()) {
            estado = EstadoApuesta.CANCELADA;
            return true;
        }
        return false;
    }

    public String obtenerResumen() {
        return String.format("Apuesta %s: %s - $%.2f - %s",
                idApuesta, tipoApuesta.getDescripcion(), montoApostado, estado.getDescripcion());
    }

    // Getters and Setters
    public String getId() { return idApuesta; }
    public void setId(String id) { this.idApuesta = id; }
    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }
    public String getIdCarrera() { return idCarrera; }
    public void setIdCarrera(String idCarrera) { this.idCarrera = idCarrera; }
    public double getMontoApostado() { return montoApostado; }
    public void setMontoApostado(double montoApostado) {
        if (montoApostado > 0) this.montoApostado = montoApostado;
    }
    public LocalDateTime getFechaApuesta() { return fechaApuesta; }
    public void setFechaApuesta(LocalDateTime fechaApuesta) { this.fechaApuesta = fechaApuesta; }
    public EstadoApuesta getEstado() { return estado; }
    public void setEstado(EstadoApuesta estado) { this.estado = estado; }
    public TipoApuesta getTipoApuesta() { return tipoApuesta; }
    public void setTipoApuesta(TipoApuesta tipoApuesta) { this.tipoApuesta = tipoApuesta; }

    // Getters y setters para HashMap (mantener como estaba)
    public Map<String, Object> getSeleccion() { return new HashMap<>(seleccion); }
    public void setSeleccion(Map<String, Object> seleccion) {
        this.seleccion = seleccion != null ? new HashMap<>(seleccion) : new HashMap<>();
    }


    // Nuevos getters y setters para la Lista
    public List<ApuestaSeleccion> getSelecciones() {
        return new ArrayList<>(selecciones);
    }
    public void setSelecciones(List<ApuestaSeleccion> selecciones) {
        this.selecciones = selecciones != null ? new ArrayList<>(selecciones) : new ArrayList<>();
    }
    public void agregarSeleccion(ApuestaSeleccion seleccion) {
        if (seleccion != null) {
            this.selecciones.add(seleccion);
        }
    }
    public boolean removerSeleccion(ApuestaSeleccion seleccion) {
        return this.selecciones.remove(seleccion);
    }

    public double getCuotaAplicada() { return cuotaAplicada; }
    public void setCuotaAplicada(double cuotaAplicada) { this.cuotaAplicada = cuotaAplicada; }
    public double getMontoGanado() { return montoGanado; }
    public void setMontoGanado(double montoGanado) { this.montoGanado = montoGanado; }

    @Override
    public String toString() {
        return String.format("Apuesta{id='%s', tipo=%s, monto=%.2f, estado=%s, fecha=%s}",
                idApuesta, tipoApuesta != null ? tipoApuesta.getDescripcion() : "N/A",
                montoApostado, estado != null ? estado.getDescripcion() : "N/A", fechaApuesta);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Apuesta apuesta = (Apuesta) obj;
        return idApuesta != null ? idApuesta.equals(apuesta.idApuesta) : apuesta.idApuesta == null;
    }

    @Override
    public int hashCode() {
        return idApuesta != null ? idApuesta.hashCode() : 0;
    }

    public Usuario getApostador() {
        return apostador;
    }
}