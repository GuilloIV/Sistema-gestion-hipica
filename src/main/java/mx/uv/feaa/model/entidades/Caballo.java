package mx.uv.feaa.model.entidades;

import mx.uv.feaa.enumeracion.SexoCaballo;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Caballo {
    private String idCaballo;
    private String nombre;
    private LocalDate fechaNacimiento;
    private SexoCaballo sexo;
    private double peso;
    private String pedigri;
    private LocalDate ultimaCarrera;
    private List<HistorialCarrera> historialCarreras;
    private EstadisticasRendimiento estadisticas;
    private Criador criador;

    public Caballo() {
        this.historialCarreras = new ArrayList<>();
        this.estadisticas = new EstadisticasRendimiento();
    }

    public Caballo(String idCaballo, String nombre, LocalDate fechaNacimiento,
                   SexoCaballo sexo, double peso, String pedigri) {
        this();
        this.idCaballo = idCaballo;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.peso = peso;
        this.pedigri = pedigri;
    }

    public boolean validarDescanso() {
        if (ultimaCarrera == null) return true;
        LocalDate ahora = LocalDate.now();
        long diasDescanso = ChronoUnit.DAYS.between(ultimaCarrera, ahora);
        return diasDescanso >= 7;
    }

    public boolean puedeParticipar() {
        if (!validarDescanso()) return false;
        LocalDate ahora = LocalDate.now();
        long edadEnAnios = ChronoUnit.YEARS.between(fechaNacimiento, ahora);
        if (edadEnAnios < 2) return false;
        return peso >= 300 && peso <= 600;
    }

    public boolean puedeParticipar(Carrera carrera) {
        if (carrera == null || !puedeParticipar()) return false;
        LocalDate fechaCarrera = carrera.getFecha();
        long edadEnAnios = ChronoUnit.YEARS.between(fechaNacimiento, fechaCarrera);
        if (edadEnAnios < 2) return false;
        return carrera.getParticipantes().stream()
                .noneMatch(p -> p.getCaballo().equals(this));
    }

    public EstadisticasRendimiento obtenerRendimiento() {
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

    public void agregarHistorial(HistorialCarrera historial) {
        if (historial != null) {
            this.historialCarreras.add(historial);
            this.ultimaCarrera = historial.getFecha();
            obtenerRendimiento();
        }
    }

    public int getEdadEnAnios() {
        return (int) ChronoUnit.YEARS.between(fechaNacimiento, LocalDate.now());
    }

    public int getEdadEnAnios(LocalDate fecha) {
        return (int) ChronoUnit.YEARS.between(fechaNacimiento, fecha);
    }

    public boolean esVeterano() {
        return getEdadEnAnios() > 8;
    }

    public boolean esDebutante() {
        return historialCarreras.isEmpty();
    }

    // Getters and Setters
    public String getIdCaballo() { return idCaballo; }
    public void setIdCaballo(String idCaballo) { this.idCaballo = idCaballo; }
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
    public List<HistorialCarrera> getHistorialCarreras() { return new ArrayList<>(historialCarreras); }
    public void setHistorialCarreras(List<HistorialCarrera> historialCarreras) {
        this.historialCarreras = historialCarreras != null ? new ArrayList<>(historialCarreras) : new ArrayList<>();
    }
    public EstadisticasRendimiento getEstadisticas() { return estadisticas; }
    public void setEstadisticas(EstadisticasRendimiento estadisticas) { this.estadisticas = estadisticas; }

    @Override
    public String toString() {
        return String.format("Caballo{id='%s', nombre='%s', edad=%d años, sexo=%s, peso=%.1f kg, carreras=%d}",
                idCaballo, nombre, getEdadEnAnios(), sexo != null ? sexo.getDescripcion() : "N/A",
                peso, historialCarreras.size());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Caballo caballo = (Caballo) obj;
        return idCaballo != null ? idCaballo.equals(caballo.idCaballo) : caballo.idCaballo == null;
    }

    @Override
    public int hashCode() {
        return idCaballo != null ? idCaballo.hashCode() : 0;
    }

    public Criador getCriador() {
        return criador;
    }

    public void setCriador(Criador criador) {
        this.criador = criador;
    }
}