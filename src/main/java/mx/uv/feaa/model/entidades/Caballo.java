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
    private String criadorId;  // Relación por ID
    private List<HistorialCarrera> historialCarreras;
    private EstadisticasRendimiento estadisticas;

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

    // Métodos de negocio
    public boolean validarDescanso() {
        if (ultimaCarrera == null) return true;
        return ChronoUnit.DAYS.between(ultimaCarrera, LocalDate.now()) >= 7;
    }

    public boolean puedeParticipar() {
        if (!validarDescanso()) return false;
        long edad = ChronoUnit.YEARS.between(fechaNacimiento, LocalDate.now());
        return edad >= 2 && peso >= 300 && peso <= 600;
    }

    public int getEdad() {
        return (int) ChronoUnit.YEARS.between(fechaNacimiento, LocalDate.now());
    }

    public boolean esVeterano() {
        return getEdad() > 8;
    }

    public boolean esDebutante() {
        return historialCarreras.isEmpty();
    }

    // Getters y Setters
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
    public String getCriadorId() { return criadorId; }
    public void setCriadorId(String criadorId) { this.criadorId = criadorId; }
    public List<HistorialCarrera> getHistorialCarreras() { return new ArrayList<>(historialCarreras); }
    public void setHistorialCarreras(List<HistorialCarrera> historialCarreras) {
        this.historialCarreras = new ArrayList<>(historialCarreras);
    }
    public EstadisticasRendimiento getEstadisticas() { return estadisticas; }
    public void setEstadisticas(EstadisticasRendimiento estadisticas) { this.estadisticas = estadisticas; }

    // Método para establecer relación por ID
    public void setCriadorById(String criadorId) {
        this.criadorId = criadorId;
    }

    @Override
    public String toString() {
        return String.format("Caballo{id='%s', nombre='%s', edad=%d, sexo=%s, peso=%.1f}",
                idCaballo, nombre, getEdad(), sexo, peso);
    }
}