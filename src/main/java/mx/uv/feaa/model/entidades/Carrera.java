package mx.uv.feaa.model.entidades;

import mx.uv.feaa.enumeracion.EstadoCarrera;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Carrera {
    private String idCarrera;
    private String nombre;
    private LocalDate fecha;
    private LocalTime hora;
    private String distancia;
    private EstadoCarrera estado;
    private List<Participante> participantes;
    private Resultado resultado;
    private int minimoParticipantes;
    private int maximoParticipantes;

    public Carrera(String idCarrera, String nombre, LocalDate fecha, LocalTime hora, String distancia) {
        this.idCarrera = idCarrera;
        this.nombre = nombre;
        this.fecha = fecha;
        this.hora = hora;
        this.distancia = distancia;
        this.estado = EstadoCarrera.PROGRAMADA;
        this.participantes = new ArrayList<>();
        this.minimoParticipantes = 4;
        this.maximoParticipantes = 20;
    }

    public boolean agregarParticipante(Participante participante) {
        if (!estado.permiteInscripciones() ||
                participantes.size() >= maximoParticipantes ||
                !participante.validarElegibilidad()) {
            return false;
        }

        if (participantes.stream().anyMatch(p -> p.getNumeroCompetidor() == participante.getNumeroCompetidor())) {
            return false;
        }

        participantes.add(participante);
        if (participantes.size() >= minimoParticipantes && estado == EstadoCarrera.PROGRAMADA) {
            estado = EstadoCarrera.INSCRIPCIONES_ABIERTAS;
        }
        return true;
    }

    public boolean validarCondicionesMinimas() {
        return participantes.size() >= minimoParticipantes &&
                fecha.isAfter(LocalDate.now().minusDays(1)) &&
                estado.estaActiva();
    }

    public void registrarResultado(Resultado resultado) {
        if (estado == EstadoCarrera.EN_CURSO || estado == EstadoCarrera.APUESTAS_CERRADAS) {
            this.resultado = resultado;
            this.estado = EstadoCarrera.FINALIZADA;

            for (Participante participante : participantes) {
                Integer posicion = resultado.obtenerPosicion(String.valueOf(participante));
                LocalTime tiempo = resultado.obtenerTiempo(String.valueOf(participante));
                if (posicion != null && tiempo != null) {
                    HistorialCarrera historial = new HistorialCarrera();
                    historial.setCarreraById(this.idCarrera);
                    historial.setCaballoById(participante.getCaballo().getIdCaballo());
                    historial.setJineteById(participante.getJinete().getIdJinete());
                    historial.setPosicion(posicion);
                    historial.setTiempo(tiempo);
                    historial.setFecha(this.fecha);
                    historial.setHipodromo("Hipódromo Principal"); // O usar this.hipodromo si existe

                    participante.getCaballo().agregarHistorial(historial);
                    participante.getJinete().agregarHistorial(historial);
                }
            }
        }
    }
    public Map<String, Double> obtenerCuotasActuales() {
        Map<String, Double> cuotas = new HashMap<>();
        for (Participante participante : participantes) {
            EstadisticasRendimiento rendimiento = participante.obtenerRendimientoHistorico();
            cuotas.put(participante.getCaballo().getNombre(), calcularCuotaBasica(rendimiento));
        }
        return cuotas;
    }

    private double calcularCuotaBasica(EstadisticasRendimiento rendimiento) {
        if (rendimiento.getTotalCarreras() == 0) return 10.0;
        double porcentajeVictorias = rendimiento.getPorcentajeVictorias();
        if (porcentajeVictorias >= 40) return 2.5;
        else if (porcentajeVictorias >= 25) return 4.0;
        else if (porcentajeVictorias >= 15) return 6.0;
        else if (porcentajeVictorias >= 10) return 8.0;
        else return 12.0;
    }

    public boolean iniciarApuestas() {
        if (validarCondicionesMinimas() &&
                (estado == EstadoCarrera.INSCRIPCIONES_ABIERTAS || estado == EstadoCarrera.PROGRAMADA)) {
            estado = EstadoCarrera.APUESTAS_ABIERTAS;
            return true;
        }
        return false;
    }

    public boolean cerrarApuestas() {
        if (estado == EstadoCarrera.APUESTAS_ABIERTAS) {
            estado = EstadoCarrera.APUESTAS_CERRADAS;
            return true;
        }
        return false;
    }

    public boolean iniciarCarrera() {
        if (estado == EstadoCarrera.APUESTAS_CERRADAS && validarCondicionesMinimas()) {
            estado = EstadoCarrera.EN_CURSO;
            return true;
        }
        return false;
    }

    // Getters and Setters
    public String getIdCarrera() { return idCarrera; }
    public void setIdCarrera(String idCarrera) { this.idCarrera = idCarrera; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }
    public String getDistancia() { return distancia; }
    public void setDistancia(String distancia) { this.distancia = distancia; }
    public EstadoCarrera getEstado() { return estado; }
    public void setEstado(EstadoCarrera estado) { this.estado = estado; }
    public List<Participante> getParticipantes() { return new ArrayList<>(participantes); }
    public void setParticipantes(List<Participante> participantes) {
        this.participantes = participantes != null ? new ArrayList<>(participantes) : new ArrayList<>();
    }
    public Resultado getResultado() { return resultado; }
    public void setResultado(Resultado resultado) { this.resultado = resultado; }
    public int getMinimoParticipantes() { return minimoParticipantes; }
    public void setMinimoParticipantes(int minimoParticipantes) { this.minimoParticipantes = minimoParticipantes; }
    public int getMaximoParticipantes() { return maximoParticipantes; }
    public void setMaximoParticipantes(int maximoParticipantes) { this.maximoParticipantes = maximoParticipantes; }

    @Override
    public String toString() {
        return String.format("Carrera: %s - %s %s (%s) - Participantes: %d - Estado: %s",
                nombre, fecha, hora, distancia, participantes.size(), estado);
    }
}