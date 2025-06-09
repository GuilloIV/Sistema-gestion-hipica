package mx.uv.feaa.model;

import mx.uv.feaa.enumeracion.EstadoApuesta;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Representa un apostador en el sistema (hereda de Usuario)
 */
public class Apostador extends Usuario {
    private double saldo;
    private double limiteApuesta;
    private List<Apuesta> historialApuestas;
    private double totalApostado;
    private double totalGanado;
    private int apuestasRealizadas;
    private LocalDate ultimaActividad;

    // Constantes
    private static final double LIMITE_APUESTA_DEFECTO = 1000.0;
    private static final double SALDO_MINIMO = 0.0;
    private static final double APUESTA_MINIMA = 10.0;

    // Constructor
    public Apostador() {
        super();
        this.saldo = 0.0;
        this.limiteApuesta = LIMITE_APUESTA_DEFECTO;
        this.historialApuestas = new ArrayList<>();
        this.totalApostado = 0.0;
        this.totalGanado = 0.0;
        this.apuestasRealizadas = 0;
    }

    public Apostador(String nombreUsuario, String email, String nombre, String telefono) {
        super(nombreUsuario, email);
        this.saldo = 0.0;
        this.limiteApuesta = LIMITE_APUESTA_DEFECTO;
        this.historialApuestas = new ArrayList<>();
        this.totalApostado = 0.0;
        this.totalGanado = 0.0;
        this.apuestasRealizadas = 0;
        // Campos adicionales específicos del apostador
        this.setNombre(nombre);
        this.setTelefono(telefono);
    }

    // Métodos de negocio
    /**
     * Deposita dinero en la cuenta del apostador
     */
    public boolean depositarDinero(double monto) {
        if (monto <= 0) {
            return false;
        }

        if (!isActivo()) {
            return false;
        }

        this.saldo += monto;
        this.ultimaActividad = LocalDate.now();
        return true;
    }

    /**
     * Retira dinero de la cuenta del apostador
     */
    public boolean retirarDinero(double monto) {
        if (monto <= 0 || monto > saldo) {
            return false;
        }

        if (!isActivo()) {
            return false;
        }

        this.saldo -= monto;
        this.ultimaActividad = LocalDate.now();
        return true;
    }

    /**
     * Realiza una apuesta
     */
    public boolean realizarApuesta(Apuesta apuesta) {
        if (apuesta == null) {
            return false;
        }

        // Validar que la cuenta esté activa
        if (!isActivo()) {
            return false;
        }

        // Validar monto mínimo
        if (apuesta.getMontoApostado() < APUESTA_MINIMA) {
            return false;
        }

        // Validar límite de apuesta
        if (apuesta.getMontoApostado() > limiteApuesta) {
            return false;
        }

        // Validar saldo suficiente
        if (saldo < apuesta.getMontoApostado()) {
            return false;
        }

        // Validar la apuesta
        if (!apuesta.validarApuesta()) {
            return false;
        }

        // Cambiar estado a CONFIRMADA después de validaciones
        apuesta.setEstado(EstadoApuesta.CONFIRMADA);

        // Debitar el monto
        this.saldo -= apuesta.getMontoApostado();
        this.totalApostado += apuesta.getMontoApostado();
        this.apuestasRealizadas++;

        // Agregar al historial
        this.historialApuestas.add(apuesta);
        this.ultimaActividad = LocalDate.now();

        return true;
    }

    /**
     * Acredita una ganancia en la cuenta
     */
    public void acreditarGanancia(double monto) {
        if (monto > 0) {
            this.saldo += monto;
            this.totalGanado += monto;
            this.ultimaActividad = LocalDate.now();
        }
    }

    /**
     * Consulta el historial de apuestas
     */
    public List<Apuesta> consultarHistorial() {
        return new ArrayList<>(historialApuestas);
    }

    /**
     * Consulta el historial de apuestas por estado
     */
    public List<Apuesta> consultarHistorialPorEstado(mx.uv.feaa.enumeracion.EstadoApuesta estado) {
        return historialApuestas.stream()
                .filter(apuesta -> apuesta.getEstado() == estado)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene las apuestas activas
     */
    public List<Apuesta> obtenerApuestasActivas() {
        return historialApuestas.stream()
                .filter(apuesta -> apuesta.getEstado().estaActiva()) // Filtro corregido
                .collect(Collectors.toList());
    }

    /**
     * Calcula el balance total (ganado - apostado)
     */
    public double obtenerBalance() {
        return totalGanado - totalApostado;
    }

    /**
     * Obtiene el porcentaje de apuestas ganadoras
     */
    public double obtenerPorcentajeExito() {
        if (apuestasRealizadas == 0) {
            return 0.0;
        }

        long apuestasGanadoras = historialApuestas.stream()
                .filter(apuesta -> apuesta.getEstado() == mx.uv.feaa.enumeracion.EstadoApuesta.GANADORA)
                .count();

        return (double) apuestasGanadoras / apuestasRealizadas * 100.0;
    }

    /**
     * Verifica si puede realizar una apuesta por el monto especificado
     */
    public boolean puedeApostar(double monto) {
        return isActivo() &&
                monto >= APUESTA_MINIMA &&
                monto <= limiteApuesta &&
                saldo >= monto;
    }

    /**
     * Actualiza el límite de apuesta
     */
    public boolean actualizarLimiteApuesta(double nuevoLimite) {
        if (nuevoLimite >= APUESTA_MINIMA) {
            this.limiteApuesta = nuevoLimite;
            return true;
        }
        return false;
    }

    /**
     * Obtiene estadísticas del apostador
     */
    public String obtenerEstadisticas() {
        return String.format(
                "Estadísticas del Apostador:\n" +
                        "- Total apostado: $%.2f\n" +
                        "- Total ganado: $%.2f\n" +
                        "- Balance: $%.2f\n" +
                        "- Apuestas realizadas: %d\n" +
                        "- Porcentaje de éxito: %.1f%%\n" +
                        "- Saldo actual: $%.2f",
                totalApostado, totalGanado, obtenerBalance(),
                apuestasRealizadas, obtenerPorcentajeExito(), saldo
        );
    }

    // Campos adicionales específicos de Apostador
    private String nombre;
    private String telefono;

    // Getters y Setters específicos
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) {
        if (saldo >= SALDO_MINIMO) {
            this.saldo = saldo;
        }
    }

    public double getLimiteApuesta() { return limiteApuesta; }
    public void setLimiteApuesta(double limiteApuesta) {
        if (limiteApuesta >= APUESTA_MINIMA) {
            this.limiteApuesta = limiteApuesta;
        }
    }

    public List<Apuesta> getHistorialApuestas() { return new ArrayList<>(historialApuestas); }
    public void setHistorialApuestas(List<Apuesta> historialApuestas) {
        this.historialApuestas = historialApuestas != null ?
                new ArrayList<>(historialApuestas) : new ArrayList<>();
    }

    public double getTotalApostado() { return totalApostado; }
    public void setTotalApostado(double totalApostado) { this.totalApostado = totalApostado; }

    public double getTotalGanado() { return totalGanado; }
    public void setTotalGanado(double totalGanado) { this.totalGanado = totalGanado; }

    public int getApuestasRealizadas() { return apuestasRealizadas; }
    public void setApuestasRealizadas(int apuestasRealizadas) { this.apuestasRealizadas = apuestasRealizadas; }

    public LocalDate getUltimaActividad() { return ultimaActividad; }
    public void setUltimaActividad(LocalDate ultimaActividad) { this.ultimaActividad = ultimaActividad; }

    @Override
    public String toString() {
        return String.format("Apostador{usuario='%s', nombre='%s', saldo=%.2f, apuestas=%d, balance=%.2f}",
                getNombreUsuario(), nombre, saldo, apuestasRealizadas, obtenerBalance());
    }
}