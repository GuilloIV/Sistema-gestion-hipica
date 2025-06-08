package mx.uv.feaa.model;

import java.time.LocalTime;

public class EstadisticasRendimiento {
    private int totalCarreras;
    private int victorias;
    private int colocaciones;
    private LocalTime promedioTiempo;
    private double porcentajeVictorias;

    // Constructor
    public EstadisticasRendimiento() {
        this.totalCarreras = 0;
        this.victorias = 0;
        this.colocaciones = 0;
        this.promedioTiempo = LocalTime.of(0, 0, 0);
        this.porcentajeVictorias = 0.0;
    }

    public EstadisticasRendimiento(int totalCarreras, int victorias, int colocaciones) {
        this.totalCarreras = totalCarreras;
        this.victorias = victorias;
        this.colocaciones = colocaciones;
        this.porcentajeVictorias = totalCarreras > 0 ? (double) victorias / totalCarreras * 100 : 0.0;
    }

    // Métodos de cálculo
    public void actualizarEstadisticas(boolean gano, boolean coloco, LocalTime tiempo) {
        this.totalCarreras++;
        if (gano) {
            this.victorias++;
        }
        if (coloco) {
            this.colocaciones++;
        }
        this.porcentajeVictorias = (double) victorias / totalCarreras * 100;
        // Actualizar promedio de tiempo (simplificado)
        if (tiempo != null) {
            this.promedioTiempo = tiempo; // En una implementación real, calcularíamos el promedio
        }
    }

    // Getters y Setters
    public int getTotalCarreras() {
        return totalCarreras;
    }

    public void setTotalCarreras(int totalCarreras) {
        this.totalCarreras = totalCarreras;
    }

    public int getVictorias() {
        return victorias;
    }

    public void setVictorias(int victorias) {
        this.victorias = victorias;
    }

    public int getColocaciones() {
        return colocaciones;
    }

    public void setColocaciones(int colocaciones) {
        this.colocaciones = colocaciones;
    }

    public LocalTime getPromedioTiempo() {
        return promedioTiempo;
    }

    public void setPromedioTiempo(LocalTime promedioTiempo) {
        this.promedioTiempo = promedioTiempo;
    }

    public double getPorcentajeVictorias() {
        return porcentajeVictorias;
    }

    public void setPorcentajeVictorias(double porcentajeVictorias) {
        this.porcentajeVictorias = porcentajeVictorias;
    }

    @Override
    public String toString() {
        return String.format("Estadísticas: %d carreras, %d victorias (%.1f%%), %d colocaciones",
                totalCarreras, victorias, porcentajeVictorias, colocaciones);
    }
}