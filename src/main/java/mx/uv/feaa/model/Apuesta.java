package mx.uv.feaa.model;

import mx.uv.feaa.enumeracion.EstadoApuesta;
import mx.uv.feaa.enumeracion.TipoApuesta;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase abstracta que representa una apuesta en el sistema
 */
public abstract class Apuesta {
    protected String id;
    protected double montoApostado;
    protected LocalDateTime fechaApuesta;
    protected EstadoApuesta estado;
    protected Carrera carrera;
    protected Apostador apostador;
    protected TipoApuesta tipoApuesta;
    protected Map<String, Object> seleccion;
    protected double cuotaAplicada;
    protected double montoGanado;

    // Constructor
    public Apuesta(String id, Apostador apostador, Carrera carrera,
                   TipoApuesta tipoApuesta, double montoApostado) {
        this.id = id;
        this.apostador = apostador;
        this.carrera = carrera;
        this.tipoApuesta = tipoApuesta;
        this.montoApostado = montoApostado;
        this.fechaApuesta = LocalDateTime.now();
        this.estado = EstadoApuesta.PENDIENTE; // Estado inicial corregido
        this.seleccion = new HashMap<>();
        this.cuotaAplicada = 0.0;
        this.montoGanado = 0.0;
    }

    /**
     * Método abstracto para calcular el dividendo según el tipo de apuesta
     */
    public abstract double calcularDividendo(Resultado resultado);

    /**
     * Método abstracto para verificar si la apuesta es ganadora
     */
    public abstract boolean esGanadora(Resultado resultado);

    /**
     * Valida si la apuesta puede ser procesada
     */
    public boolean validarApuesta() {
        // Verificar que el monto sea válido
        if (montoApostado <= 0) {
            return false;
        }

        // Verificar que el apostador tenga saldo suficiente
        if (apostador.getSaldo() < montoApostado) {
            return false;
        }

        // Verificar que la carrera permita apuestas
        if (carrera == null || !carrera.getEstado().permiteApuestas()) {
            return false;
        }

        // Verificar que la selección sea válida
        if (seleccion == null || seleccion.isEmpty()) {
            return false;
        }

        return true;
    }

    /**
     * Procesa el resultado de la apuesta
     */
    public void procesar(Resultado resultado) {
        if (resultado == null || !estado.estaActiva()) { // Verificación corregida
            return;
        }

        if (esGanadora(resultado)) {
            estado = EstadoApuesta.GANADORA;
            montoGanado = calcularDividendo(resultado);
            apostador.acreditarGanancia(montoGanado);
        } else {
            estado = EstadoApuesta.PERDEDORA;
            montoGanado = 0.0;
        }
    }

    /**
     * Cancela la apuesta y devuelve el dinero
     */
    public boolean cancelar() {
        if (estado.puedeCancelarse()) { // Verificación corregida
            estado = EstadoApuesta.CANCELADA;
            apostador.acreditarGanancia(montoApostado); // Devolver el dinero
            return true;
        }
        return false;
    }

    /**
     * Obtiene información resumida de la apuesta
     */
    public String obtenerResumen() {
        return String.format("Apuesta %s: %s - $%.2f - %s",
                id, tipoApuesta.getDescripcion(), montoApostado, estado.getDescripcion());
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public double getMontoApostado() { return montoApostado; }
    public void setMontoApostado(double montoApostado) {
        if (montoApostado > 0) {
            this.montoApostado = montoApostado;
        }
    }

    public LocalDateTime getFechaApuesta() { return fechaApuesta; }
    public void setFechaApuesta(LocalDateTime fechaApuesta) { this.fechaApuesta = fechaApuesta; }

    public EstadoApuesta getEstado() { return estado; }
    public void setEstado(EstadoApuesta estado) { this.estado = estado; }

    public Carrera getCarrera() { return carrera; }
    public void setCarrera(Carrera carrera) { this.carrera = carrera; }

    public Apostador getApostador() { return apostador; }
    public void setApostador(Apostador apostador) { this.apostador = apostador; }

    public TipoApuesta getTipoApuesta() { return tipoApuesta; }
    public void setTipoApuesta(TipoApuesta tipoApuesta) { this.tipoApuesta = tipoApuesta; }

    public Map<String, Object> getSeleccion() { return new HashMap<>(seleccion); }
    public void setSeleccion(Map<String, Object> seleccion) {
        this.seleccion = seleccion != null ? new HashMap<>(seleccion) : new HashMap<>();
    }

    public double getCuotaAplicada() { return cuotaAplicada; }
    public void setCuotaAplicada(double cuotaAplicada) { this.cuotaAplicada = cuotaAplicada; }

    public double getMontoGanado() { return montoGanado; }
    public void setMontoGanado(double montoGanado) { this.montoGanado = montoGanado; }

    @Override
    public String toString() {
        return String.format("Apuesta{id='%s', tipo=%s, monto=%.2f, estado=%s, fecha=%s}",
                id, tipoApuesta != null ? tipoApuesta.getDescripcion() : "N/A",
                montoApostado, estado != null ? estado.getDescripcion() : "N/A", fechaApuesta);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Apuesta apuesta = (Apuesta) obj;
        return id != null ? id.equals(apuesta.id) : apuesta.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}