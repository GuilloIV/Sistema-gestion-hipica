package mx.uv.feaa.model.entidades;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Jinete {
    private String idJinete;
    private String nombre;
    private LocalDate fechaNacimiento;
    private double peso;
    private String licencia;
    private LocalDate fechaVigenciaLicencia;
    private List<HistorialCarrera> historialCarreras;
    private EstadisticasRendimiento estadisticas;

    private static final double PESO_MINIMO_JINETE = 50.0;
    private static final double PESO_MAXIMO_JINETE = 57.0;
    private static final int EDAD_MINIMA_JINETE = 16;

    public Jinete() {
        this.historialCarreras = new ArrayList<>();
        this.estadisticas = new EstadisticasRendimiento();
    }

    public Jinete(String idJinete, String nombre, LocalDate fechaNacimiento,
                  double peso, String licencia, LocalDate fechaVigenciaLicencia) {
        this();
        this.idJinete = idJinete;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.peso = peso;
        this.licencia = licencia;
        this.fechaVigenciaLicencia = fechaVigenciaLicencia;
    }

    public boolean validarLicencia() {
        if (licencia == null || licencia.trim().isEmpty() || fechaVigenciaLicencia == null) {
            return false;
        }
        LocalDate ahora = LocalDate.now();
        return !fechaVigenciaLicencia.isBefore(ahora);
    }

    public boolean validarPesoReglamentario(double pesoAsignado) {
        if (peso < PESO_MINIMO_JINETE || peso > PESO_MAXIMO_JINETE) {
            return false;
        }
        double diferencia = Math.abs(peso - pesoAsignado);
        return diferencia <= 2.0;
    }

    public boolean validarEdadMinima() {
        LocalDate ahora = LocalDate.now();
        long edadEnAnios = ChronoUnit.YEARS.between(fechaNacimiento, ahora);
        return edadEnAnios >= EDAD_MINIMA_JINETE;
    }

    public boolean puedeParticipar() {
        return validarLicencia() && validarEdadMinima() &&
                peso >= PESO_MINIMO_JINETE && peso <= PESO_MAXIMO_JINETE;
    }

    public EstadisticasRendimiento obtenerEstadisticas() {
        int totalCarreras = historialCarreras.size();
        int victorias = (int) historialCarreras.stream()
                .filter(HistorialCarrera::esVictoria)
                .count();
        int colocaciones = (int) historialCarreras.stream()
                .filter(HistorialCarrera::esColocacion)
                .count();
        this.estadisticas = new EstadisticasRendimiento(totalCarreras, victorias, colocaciones);
        return this.estadisticas;
    }

    public void agregarHistorialCarrera(HistorialCarrera historial) {
        if (historial != null) {
            this.historialCarreras.add(historial);
            obtenerEstadisticas();
        }
    }

    public void agregarHistorial(HistorialCarrera historial) {
        agregarHistorialCarrera(historial);
    }

    public int getEdadEnAnios() {
        return (int) ChronoUnit.YEARS.between(fechaNacimiento, LocalDate.now());
    }

    // Getters and Setters
    public String getIdJinete() { return idJinete; }
    public void setIdJinete(String idJinete) { this.idJinete = idJinete; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }
    public String getLicencia() { return licencia; }
    public void setLicencia(String licencia) { this.licencia = licencia; }
    public LocalDate getFechaVigenciaLicencia() { return fechaVigenciaLicencia; }
    public void setFechaVigenciaLicencia(LocalDate fechaVigenciaLicencia) {
        this.fechaVigenciaLicencia = fechaVigenciaLicencia;
    }
    public List<HistorialCarrera> getHistorialCarreras() { return new ArrayList<>(historialCarreras); }
    public void setHistorialCarreras(List<HistorialCarrera> historialCarreras) {
        this.historialCarreras = historialCarreras != null ? new ArrayList<>(historialCarreras) : new ArrayList<>();
    }
    public EstadisticasRendimiento getEstadisticas() { return estadisticas; }
    public void setEstadisticas(EstadisticasRendimiento estadisticas) { this.estadisticas = estadisticas; }

    @Override
    public String toString() {
        return String.format("Jinete{id='%s', nombre='%s', edad=%d años, peso=%.1f kg, licencia='%s'}",
                idJinete, nombre, getEdadEnAnios(), peso, licencia);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Jinete jinete = (Jinete) obj;
        return idJinete != null ? idJinete.equals(jinete.idJinete) : jinete.idJinete == null;
    }

    @Override
    public int hashCode() {
        return idJinete != null ? idJinete.hashCode() : 0;
    }
}