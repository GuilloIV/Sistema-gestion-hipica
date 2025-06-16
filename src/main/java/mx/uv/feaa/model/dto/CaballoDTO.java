package mx.uv.feaa.model.dto;

import mx.uv.feaa.enumeracion.SexoCaballo;

import java.time.LocalDate;

public class CaballoDTO {
    private String id;
    private String nombre;
    private LocalDate fechaNacimiento;
    private SexoCaballo sexo;
    private double peso;
    private String pedigri;  // Nuevo campo agregado
    private LocalDate ultimaCarrera;
    private String criadorId;

    public CaballoDTO() {
    }

    public CaballoDTO(String id, String nombre, LocalDate fechaNacimiento,
                      SexoCaballo sexo, double peso, LocalDate ultimaCarrera) {
        this.id = id;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.peso = peso;
        this.ultimaCarrera = ultimaCarrera;
    }

    // Constructor completo con todos los campos
    public CaballoDTO(String id, String nombre, LocalDate fechaNacimiento,
                      SexoCaballo sexo, double peso, String pedigri,
                      LocalDate ultimaCarrera, String criadorId) {
        this.id = id;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.peso = peso;
        this.pedigri = pedigri;
        this.ultimaCarrera = ultimaCarrera;
        this.criadorId = criadorId;
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

    public SexoCaballo getSexo() {
        return sexo;
    }

    public void setSexo(SexoCaballo sexo) {
        this.sexo = sexo;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public String getPedigri() {
        return pedigri;
    }

    public void setPedigri(String pedigri) {
        this.pedigri = pedigri;
    }

    public LocalDate getUltimaCarrera() {
        return ultimaCarrera;
    }

    public void setUltimaCarrera(LocalDate ultimaCarrera) {
        this.ultimaCarrera = ultimaCarrera;
    }

    public String getCriadorId() {
        return criadorId;
    }

    public void setCriadorId(String criadorId) {
        this.criadorId = criadorId;
    }

    @Override
    public String toString() {
        return "CaballoDTO{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                ", sexo=" + sexo +
                ", peso=" + peso +
                ", pedigri='" + pedigri + '\'' +
                ", ultimaCarrera=" + ultimaCarrera +
                ", criadorId='" + criadorId + '\'' +
                '}';
    }
}