package mx.uv.feaa.model.entidades;

import mx.uv.feaa.enumeracion.EstadoApuesta;
import mx.uv.feaa.model.entidades.Usuario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Apostador extends Usuario {
    private double saldo;
    private double limiteApuesta;
    private List<Apuesta> historialApuestas;
    private double totalApostado;
    private double totalGanado;
    private int apuestasRealizadas;
    private LocalDate ultimaActividad;
    private String nombre;
    private String telefono;

    private static final double LIMITE_APUESTA_DEFECTO = 1000.0;
    private static final double SALDO_MINIMO = 0.0;
    private static final double APUESTA_MINIMA = 10.0;

    public Apostador() {

    }


    // Updated constructor to match Usuario's constructors
    public Apostador(String idUsuario, String nombreUsuario, String email, String password,
                     String nombre, String telefono) {
        super(idUsuario, nombreUsuario, email, password);
        this.saldo = 0.0;
        this.limiteApuesta = LIMITE_APUESTA_DEFECTO;
        this.historialApuestas = new ArrayList<>();
        this.totalApostado = 0.0;
        this.totalGanado = 0.0;
        this.apuestasRealizadas = 0;
        this.nombre = nombre;
        this.telefono = telefono;
    }



    @Override
    public String getTipoUsuarioEspecifico() {
        return "APOSTADOR";
    }

    public boolean depositarDinero(double monto) {
        if (monto <= 0 || !isActivo()) {
            return false;
        }
        this.saldo += monto;
        this.ultimaActividad = LocalDate.now();
        return true;
    }



    public boolean retirarDinero(double monto) {
        if (monto <= 0 || monto > saldo || !isActivo()) {
            return false;
        }
        this.saldo -= monto;
        this.ultimaActividad = LocalDate.now();
        return true;
    }

    public boolean realizarApuesta(Apuesta apuesta) {
        if (apuesta == null || !isActivo() ||
                apuesta.getMontoApostado() < APUESTA_MINIMA ||
                apuesta.getMontoApostado() > limiteApuesta ||
                saldo < apuesta.getMontoApostado() ||
                !apuesta.validarApuesta()) {
            return false;
        }

        apuesta.setEstado(EstadoApuesta.CONFIRMADA);
        this.saldo -= apuesta.getMontoApostado();
        this.totalApostado += apuesta.getMontoApostado();
        this.apuestasRealizadas++;
        this.historialApuestas.add(apuesta);
        this.ultimaActividad = LocalDate.now();
        return true;
    }

    public void acreditarGanancia(double monto) {
        if (monto > 0) {
            this.saldo += monto;
            this.totalGanado += monto;
            this.ultimaActividad = LocalDate.now();
        }
    }

    public List<Apuesta> consultarHistorial() {
        return new ArrayList<>(historialApuestas);
    }

    public List<Apuesta> consultarHistorialPorEstado(EstadoApuesta estado) {
        return historialApuestas.stream()
                .filter(apuesta -> apuesta.getEstado() == estado)
                .collect(Collectors.toList());
    }

    public List<Apuesta> obtenerApuestasActivas() {
        return historialApuestas.stream()
                .filter(apuesta -> apuesta.getEstado().estaActiva())
                .collect(Collectors.toList());
    }

    public double obtenerBalance() {
        return totalGanado - totalApostado;
    }

    public double obtenerPorcentajeExito() {
        if (apuestasRealizadas == 0) return 0.0;
        long apuestasGanadoras = historialApuestas.stream()
                .filter(apuesta -> apuesta.getEstado() == EstadoApuesta.GANADORA)
                .count();
        return (double) apuestasGanadoras / apuestasRealizadas * 100.0;
    }

    public boolean puedeApostar(double monto) {
        return isActivo() && monto >= APUESTA_MINIMA && monto <= limiteApuesta && saldo >= monto;
    }

    public boolean actualizarLimiteApuesta(double nuevoLimite) {
        if (nuevoLimite >= APUESTA_MINIMA) {
            this.limiteApuesta = nuevoLimite;
            return true;
        }
        return false;
    }

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
                apuestasRealizadas, obtenerPorcentajeExito(), saldo);
    }

    // Getters and Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) {
        if (saldo >= SALDO_MINIMO) this.saldo = saldo;
    }
    public double getLimiteApuesta() { return limiteApuesta; }
    public void setLimiteApuesta(double limiteApuesta) {
        if (limiteApuesta >= APUESTA_MINIMA) this.limiteApuesta = limiteApuesta;
    }
    public List<Apuesta> getHistorialApuestas() { return new ArrayList<>(historialApuestas); }
    public void setHistorialApuestas(List<Apuesta> historialApuestas) {
        this.historialApuestas = historialApuestas != null ? new ArrayList<>(historialApuestas) : new ArrayList<>();
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