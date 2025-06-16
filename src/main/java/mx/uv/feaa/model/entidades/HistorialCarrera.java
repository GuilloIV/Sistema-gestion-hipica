package mx.uv.feaa.model.entidades;

import java.time.LocalDate;
import java.time.LocalTime;

public class HistorialCarrera {
    private String idHistorial;
    private String carrera_id;
    private String caballo_id;
    private String jinete_id;
    private int posicion;
    private LocalTime tiempo;
    private LocalDate fecha;
    private String hipodromo;

    // Métodos para determinar posición
    public boolean esVictoria() {
        return posicion == 1; // 1er lugar = victoria
    }

    public boolean esColocacion() {
        return posicion <= 3; // 1er, 2do o 3er lugar = colocación
    }

    // Métodos existentes (getters, setters, etc.)
    public String getIdHistorial() {
        return idHistorial;
    }

    public void setIdHistorial(String idHistorial) {
        this.idHistorial = idHistorial;
    }

    public String getIdCarrera() {
        return carrera_id;
    }

    public void setIdCarrera(String carrera_id) {
        this.carrera_id = carrera_id;
    }

    public String getIdCaballo() {
        return caballo_id;
    }

    public void setIdCaballo(String caballo_id) {
        this.caballo_id = caballo_id;
    }

    public String getIdJinete() {
        return jinete_id;
    }

    public void setIdJinete(String jinete_id) {
        this.jinete_id = jinete_id;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
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

    // Métodos "ById" para compatibilidad con DAO
    public void setCarreraById(String carrera_id) {
        this.carrera_id = carrera_id;
    }

    public void setCaballoById(String caballo_id) {
        this.caballo_id = caballo_id;
    }

    public void setJineteById(String jinete_id) {
        this.jinete_id = jinete_id;
    }

    @Override
    public String toString() {
        return "HistorialCarrera{" +
                "idHistorial='" + idHistorial + '\'' +
                ", carrera_id='" + carrera_id + '\'' +
                ", caballo_id='" + caballo_id + '\'' +
                ", jinete_id='" + jinete_id + '\'' +
                ", posicion=" + posicion +
                ", tiempo=" + tiempo +
                ", fecha=" + fecha +
                ", hipodromo='" + hipodromo + '\'' +
                '}';
    }
}