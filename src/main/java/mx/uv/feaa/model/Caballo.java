package mx.uv.feaa.model;

import mx.uv.feaa.enumeracion.SexoCaballo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Caballo {
    private String id;
    private String nombre;
    private LocalDate fechaNacimiento;
    private SexoCaballo sexo;
    private double peso;
    private String pedigri;
    private LocalDate ultimaCarrera;
    private List<HistorialCarrera> historialCarreras;
    private EstadisticasRendimiento estadisticas;

    // Constructor por defecto
    public Caballo() {
        this.historialCarreras = new ArrayList<>();
        this.estadisticas = new EstadisticasRendimiento();
    }

    // Constructor completo
    public Caballo(String id, String nombre, LocalDate fechaNacimiento,
                   SexoCaballo sexo, double peso, String pedigri) {
        this();
        this.id = id;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.peso = peso;
        this.pedigri = pedigri;
    }

    // Métodos de negocio
    public boolean validarDescanso() {
        if (ultimaCarrera == null) {
            return true; // Si nunca ha corrido, puede participar
        }

        LocalDate ahora = LocalDate.now();
        long diasDescanso = ChronoUnit.DAYS.between(ultimaCarrera, ahora);

        // Regla: mínimo 7 días de descanso entre carreras
        return diasDescanso >= 7;
    }

    /**
     * Método sobrecargado sin parámetros - verifica condiciones generales
     */
    public boolean puedeParticipar() {
        // Validar descanso mínimo
        if (!validarDescanso()) {
            return false;
        }

        // Validar edad mínima (2 años para competir)
        LocalDate ahora = LocalDate.now();
        long edadEnAnios = ChronoUnit.YEARS.between(fechaNacimiento, ahora);
        if (edadEnAnios < 2) {
            return false;
        }

        // Validar peso dentro de límites razonables (300-600 kg)
        if (peso < 300 || peso > 600) {
            return false;
        }

        return true;
    }

    /**
     * Método sobrecargado con parámetros - verifica condiciones específicas para una carrera
     */
    public boolean puedeParticipar(Carrera carrera) {
        if (carrera == null) {
            return false;
        }

        // Validaciones generales
        if (!puedeParticipar()) {
            return false;
        }

        // Validar edad específica para la fecha de la carrera
        LocalDate fechaCarrera = carrera.getFecha();
        long edadEnAnios = ChronoUnit.YEARS.between(fechaNacimiento, fechaCarrera);
        if (edadEnAnios < 2) {
            return false;
        }

        // Validar que no esté ya inscrito en la carrera
        for (Participante p : carrera.getParticipantes()) {
            if (this.equals(p.getCaballo())) {
                return false;
            }
        }

        return true;
    }

    public EstadisticasRendimiento obtenerRendimiento() {
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

    /**
     * Método corregido para agregar historial
     */
    public void agregarHistorial(HistorialCarrera historial) {
        if (historial != null) {
            this.historialCarreras.add(historial);
            this.ultimaCarrera = historial.getFecha();
            // Actualizar estadísticas
            obtenerRendimiento();
        }
    }

    /**
     * Método alternativo para mantener compatibilidad
     */
    public void agregarHistorialCarrera(HistorialCarrera historial) {
        agregarHistorial(historial);
    }

    public int getEdadEnAnios() {
        return (int) ChronoUnit.YEARS.between(fechaNacimiento, LocalDate.now());
    }

    /**
     * Obtiene la edad del caballo en una fecha específica
     */
    public int getEdadEnAnios(LocalDate fecha) {
        return (int) ChronoUnit.YEARS.between(fechaNacimiento, fecha);
    }

    /**
     * Verifica si el caballo es veterano (más de 8 años)
     */
    public boolean esVeterano() {
        return getEdadEnAnios() > 8;
    }

    /**
     * Verifica si el caballo es debutante (sin carreras)
     */
    public boolean esDebutante() {
        return historialCarreras.isEmpty();
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public SexoCaballo getSexo() { return sexo; }
    public void setSexo(SexoCaballo sexo) { this.sexo = sexo; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public String getPedigri() { return pedigri; }
    public void setPedigri(String pedigri) { this.pedigri = pedigri; }

    public LocalDate getUltimaCarrera() { return ultimaCarrera; }
    public void setUltimaCarrera(LocalDate ultimaCarrera) { this.ultimaCarrera = ultimaCarrera; }

    public List<HistorialCarrera> getHistorialCarreras() {
        return new ArrayList<>(historialCarreras);
    }

    public void setHistorialCarreras(List<HistorialCarrera> historialCarreras) {
        this.historialCarreras = historialCarreras != null ?
                new ArrayList<>(historialCarreras) : new ArrayList<>();
    }

    public EstadisticasRendimiento getEstadisticas() { return estadisticas; }
    public void setEstadisticas(EstadisticasRendimiento estadisticas) { this.estadisticas = estadisticas; }

    @Override
    public String toString() {
        return String.format("Caballo{id='%s', nombre='%s', edad=%d años, sexo=%s, peso=%.1f kg, carreras=%d}",
                id, nombre, getEdadEnAnios(), sexo != null ? sexo.getDescripcion() : "N/A",
                peso, historialCarreras.size());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Caballo caballo = (Caballo) obj;
        return id != null ? id.equals(caballo.id) : caballo.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}