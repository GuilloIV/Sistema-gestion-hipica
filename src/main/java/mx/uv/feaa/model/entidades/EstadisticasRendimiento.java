package mx.uv.feaa.model.entidades;

import java.time.LocalTime;

public class EstadisticasRendimiento {
    private String idEstadistica;
    private String idEntidad;
    private String tipoEntidad;
    private int totalCarreras;
    private int victorias;
    private int colocaciones;
    private LocalTime promedioTiempo;
    private double porcentajeVictorias;

    public EstadisticasRendimiento() {}

    public EstadisticasRendimiento(int totalCarreras, int victorias, int colocaciones) {
        this.totalCarreras = totalCarreras;
        this.victorias = victorias;
        this.colocaciones = colocaciones;
        this.porcentajeVictorias = totalCarreras > 0 ? (victorias * 100.0 / totalCarreras) : 0;
    }

    public EstadisticasRendimiento(String idEstadistica, String idEntidad, String tipoEntidad) {
        this();
        this.idEstadistica = idEstadistica;
        this.idEntidad = idEntidad;
        this.tipoEntidad = tipoEntidad;
    }

    // Getters and Setters
    public String getIdEstadistica() { return idEstadistica; }
    public void setIdEstadistica(String idEstadistica) { this.idEstadistica = idEstadistica; }
    public String getIdEntidad() { return idEntidad; }
    public void setIdEntidad(String idEntidad) { this.idEntidad = idEntidad; }
    public String getTipoEntidad() { return tipoEntidad; }
    public void setTipoEntidad(String tipoEntidad) { this.tipoEntidad = tipoEntidad; }
    public int getTotalCarreras() { return totalCarreras; }
    public void setTotalCarreras(int totalCarreras) {
        this.totalCarreras = totalCarreras;
        this.porcentajeVictorias = totalCarreras > 0 ? (victorias * 100.0 / totalCarreras) : 0;
    }
    public int getVictorias() { return victorias; }
    public void setVictorias(int victorias) {
        this.victorias = victorias;
        this.porcentajeVictorias = totalCarreras > 0 ? (victorias * 100.0 / totalCarreras) : 0;
    }
    public int getColocaciones() { return colocaciones; }
    public void setColocaciones(int colocaciones) { this.colocaciones = colocaciones; }
    public LocalTime getPromedioTiempo() { return promedioTiempo; }
    public void setPromedioTiempo(LocalTime promedioTiempo) { this.promedioTiempo = promedioTiempo; }
    public double getPorcentajeVictorias() { return porcentajeVictorias; }
    public void setPorcentajeVictorias(double porcentajeVictorias) { this.porcentajeVictorias = porcentajeVictorias; }

    @Override
    public String toString() {
        return String.format("Estadísticas: %d carreras, %d victorias (%.1f%%), %d colocaciones",
                totalCarreras, victorias, porcentajeVictorias, colocaciones);
    }
}