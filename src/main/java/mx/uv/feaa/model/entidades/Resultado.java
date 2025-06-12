package mx.uv.feaa.model.entidades;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class Resultado {
    private String idResultado;
    private String idCarrera;
    private Map<Integer, String> posiciones; // <Posición, idParticipante>
    private Map<String, LocalTime> tiempos;  // <idParticipante, Tiempo>
    private LocalDate fechaRegistro;

    public Resultado(String idResultado, String idCarrera) {
        this.idResultado = idResultado;
        this.idCarrera = idCarrera;
        this.posiciones = new HashMap<>();
        this.tiempos = new HashMap<>();
        this.fechaRegistro = LocalDate.now();
    }

    public void registrarPosicion(int posicion, String idParticipante, LocalTime tiempo) {
        posiciones.put(posicion, idParticipante);
        tiempos.put(idParticipante, tiempo);
    }

    public String obtenerGanador() {
        return posiciones.get(1);
    }

    public Integer obtenerPosicion(String idParticipante) {
        for (Map.Entry<Integer, String> entry : posiciones.entrySet()) {
            if (entry.getValue().equals(idParticipante)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public String obtenerParticipantePorPosicion(int posicion) {
        return posiciones.get(posicion);
    }

    public LocalTime obtenerTiempo(String idParticipante) {
        return tiempos.get(idParticipante);
    }

    public boolean esCompleto(int totalParticipantes) {
        return posiciones.size() == totalParticipantes &&
                tiempos.size() == totalParticipantes;
    }

    // Getters and Setters
    public String getIdResultado() { return idResultado; }
    public void setIdResultado(String idResultado) { this.idResultado = idResultado; }
    public String getIdCarrera() { return idCarrera; }
    public void setIdCarrera(String idCarrera) { this.idCarrera = idCarrera; }
    public Map<Integer, String> getPosiciones() { return new HashMap<>(posiciones); }
    public void setPosiciones(Map<Integer, String> posiciones) {
        this.posiciones = posiciones != null ? new HashMap<>(posiciones) : new HashMap<>();
    }
    public Map<String, LocalTime> getTiempos() { return new HashMap<>(tiempos); }
    public void setTiempos(Map<String, LocalTime> tiempos) {
        this.tiempos = tiempos != null ? new HashMap<>(tiempos) : new HashMap<>();
    }
    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Resultado ID: ").append(idResultado).append("\n");
        sb.append("Carrera ID: ").append(idCarrera).append("\n");
        sb.append("Fecha: ").append(fechaRegistro).append("\n");
        sb.append("Posiciones:\n");

        for (int i = 1; i <= posiciones.size(); i++) {
            String idParticipante = posiciones.get(i);
            if (idParticipante != null) {
                LocalTime tiempo = tiempos.get(idParticipante);
                sb.append(String.format("%d. Participante ID: %s - Tiempo: %s\n", i, idParticipante, tiempo));
            }
        }

        return sb.toString();
    }
}