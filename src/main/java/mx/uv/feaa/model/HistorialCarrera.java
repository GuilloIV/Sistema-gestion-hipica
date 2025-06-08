package mx.uv.feaa.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class HistorialCarrera {
    private String carreraId;
    private String nombreCarrera;
    private Integer posicion;
    private LocalTime tiempo;
    private LocalDate fecha;
    private String hipodromo;

    // Constructor
    public HistorialCarrera() {}

    public HistorialCarrera(String carreraId, String nombreCarrera, Integer posicion,
                            LocalTime tiempo, LocalDate fecha, String hipodromo) {
        this.carreraId = carreraId;
        this.nombreCarrera = nombreCarrera;
        this.posicion = posicion;
        this.tiempo = tiempo;
        this.fecha = fecha;
        this.hipodromo = hipodromo;
    }

    // Métodos de utilidad
    public boolean esVictoria() {
        return posicion != null && posicion == 1;
    }

    public boolean esColocacion() {
        return posicion != null && posicion <= 3;
    }

    // Getters y Setters
    public String getCarreraId() {
        return carreraId;
    }

    public void setCarreraId(String carreraId) {
        this.carreraId = carreraId;
    }

    public String getNombreCarrera() {
        return nombreCarrera;
    }

    public void setNombreCarrera(String nombreCarrera) {
        this.nombreCarrera = nombreCarrera;
    }

    public Integer getPosicion() {
        return posicion;
    }

    public void setPosicion(Integer posicion) {
        this.posicion = posicion;
    }

    public LocalTime getTiempo() {
        return tiempo;
    }

    public void setTiempo(LocalTime tiempo) {
        this.tiempo = tiempo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getHipodromo() {
        return hipodromo;
    }

    public void setHipodromo(String hipodromo) {
        this.hipodromo = hipodromo;
    }

    @Override
    public String toString() {
        return String.format("%s - Posición: %d, Tiempo: %s (%s)",
                nombreCarrera, posicion, tiempo, fecha);
    }
}