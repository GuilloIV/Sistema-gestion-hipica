package mx.uv.feaa.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Representa el resultado de una carrera
 */
public class Resultado {
    private Carrera carrera;
    private Map<Integer, Participante> posiciones;
    private Map<Participante, LocalTime> tiemposOficiales;
    private LocalDate fechaRegistro;

    // Constructor
    public Resultado(Carrera carrera) {
        this.carrera = carrera;
        this.posiciones = new HashMap<>();
        this.tiemposOficiales = new HashMap<>();
        this.fechaRegistro = LocalDate.now();
    }

    /**
     * Registra la posición de un participante
     */
    public void registrarPosicion(int posicion, Participante participante, LocalTime tiempo) {
        posiciones.put(posicion, participante);
        tiemposOficiales.put(participante, tiempo);
    }

    /**
     * Obtiene el ganador de la carrera (primera posición)
     */
    public Participante obtenerGanador() {
        return posiciones.get(1);
    }

    /**
     * Obtiene la posición de un participante específico
     */
    public Integer obtenerPosicion(Participante participante) {
        for (Map.Entry<Integer, Participante> entry : posiciones.entrySet()) {
            if (entry.getValue().equals(participante)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Obtiene el participante en una posición específica
     */
    public Participante obtenerParticipantePorPosicion(int posicion) {
        return posiciones.get(posicion);
    }

    /**
     * Obtiene el tiempo oficial de un participante
     */
    public LocalTime obtenerTiempo(Participante participante) {
        return tiemposOficiales.get(participante);
    }

    /**
     * Verifica si los resultados están completos
     */
    public boolean esCompleto() {
        return posiciones.size() == carrera.getParticipantes().size() &&
                tiemposOficiales.size() == carrera.getParticipantes().size();
    }

    // Getters y Setters
    public Carrera getCarrera() {
        return carrera; }
    public void setCarrera(Carrera carrera) {
        this.carrera = carrera; }

    public Map<Integer, Participante> getPosiciones() {
        return posiciones; }
    public void setPosiciones(Map<Integer, Participante> posiciones) {
        this.posiciones = posiciones;
    }

    public Map<Participante, LocalTime> getTiemposOficiales() {
        return tiemposOficiales;
    }
    public void setTiemposOficiales(Map<Participante, LocalTime> tiemposOficiales) {
        this.tiemposOficiales = tiemposOficiales;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Resultado de la carrera: ").append(carrera.getNombre()).append("\n");
        sb.append("Fecha: ").append(fechaRegistro).append("\n");
        sb.append("Posiciones:\n");

        for (int i = 1; i <= posiciones.size(); i++) {
            Participante p = posiciones.get(i);
            if (p != null) {
                LocalTime tiempo = tiemposOficiales.get(p);
                sb.append(String.format("%d. %s - Tiempo: %s\n", i, p.toString(), tiempo));
            }
        }

        return sb.toString();
    }
}