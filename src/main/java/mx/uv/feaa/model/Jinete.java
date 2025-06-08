package mx.uv.feaa.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Jinete {
    private String id;
    private String nombre;
    private LocalDate fechaNacimiento;
    private double peso;
    private String licencia;
    private LocalDate fechaVigenciaLicencia;
    private List<HistorialCarrera> historialCarreras;
    private EstadisticasRendimiento estadisticas;

    // Constantes
    private static final double PESO_MINIMO_JINETE = 50.0; // kg
    private static final double PESO_MAXIMO_JINETE = 57.0; // kg
    private static final int EDAD_MINIMA_JINETE = 16; // años

    // Constructor
    public Jinete() {
        this.historialCarreras = new ArrayList<>();
        this.estadisticas = new EstadisticasRendimiento();
    }

    public Jinete(String id, String nombre, LocalDate fechaNacimiento,
                  double peso, String licencia, LocalDate fechaVigenciaLicencia) {
        this();
        this.id = id;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.peso = peso;
        this.licencia = licencia;
        this.fechaVigenciaLicencia = fechaVigenciaLicencia;
    }

    // Métodos de negocio
    public boolean validarLicencia() {
        if (licencia == null || licencia.trim().isEmpty()) {
            return false;
        }

        if (fechaVigenciaLicencia == null) {
            return false;
        }

        LocalDate ahora = LocalDate.now();
        return !fechaVigenciaLicencia.isBefore(ahora);
    }

    public boolean validarPesoReglamentario(double pesoAsignado) {
        // El peso del jinete debe estar dentro del rango reglamentario
        if (peso < PESO_MINIMO_JINETE || peso > PESO_MAXIMO_JINETE) {
            return false;
        }

        // El peso asignado debe ser alcanzable por el jinete
        // (considerando un margen de +/- 2 kg)
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
        // Recalcular estadísticas basadas en el historial
        int totalCarreras = historialCarreras.size();
        int victorias = (int) historialCarreras.stream()
                .mapToInt(h -> h.esVictoria() ? 1 : 0)
                .sum();
        int colocaciones = (int) historialCarreras.stream()
                .mapToInt(h -> h.esColocacion() ? 1 : 0)
                .sum();

        this.estadisticas = new EstadisticasRendimiento(totalCarreras, victorias, colocaciones);
        return this.estadisticas;
    }

    public void agregarHistorialCarrera(HistorialCarrera historial) {
        if (historial != null) {
            this.historialCarreras.add(historial);
            // Actualizar estadísticas
            obtenerEstadisticas();
        }
    }

    public int getEdadEnAnios() {
        return (int) ChronoUnit.YEARS.between(fechaNacimiento, LocalDate.now());
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public String getLicencia() {
        return licencia;
    }

    public void setLicencia(String licencia) {
        this.licencia = licencia;
    }

    public LocalDate getFechaVigenciaLicencia() {
        return fechaVigenciaLicencia;
    }

    public void setFechaVigenciaLicencia(LocalDate fechaVigenciaLicencia) {
        this.fechaVigenciaLicencia = fechaVigenciaLicencia;
    }

    public List<HistorialCarrera> getHistorialCarreras() {
        return new ArrayList<>(historialCarreras);
    }

    public void setHistorialCarreras(List<HistorialCarrera> historialCarreras) {
        this.historialCarreras = historialCarreras != null ?
                new ArrayList<>(historialCarreras) : new ArrayList<>();
    }

    public EstadisticasRendimiento getEstadisticas() {
        return estadisticas;
    }

    public void setEstadisticas(EstadisticasRendimiento estadisticas) {
        this.estadisticas = estadisticas;
    }

    @Override
    public String toString() {
        return String.format("Jinete{id='%s', nombre='%s', edad=%d años, peso=%.1f kg, licencia='%s'}",
                id, nombre, getEdadEnAnios(), peso, licencia);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Jinete jinete = (Jinete) obj;
        return id != null ? id.equals(jinete.id) : jinete.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
