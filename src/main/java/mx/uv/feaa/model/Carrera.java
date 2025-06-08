package mx.uv.feaa.model;

import mx.uv.feaa.enumeracion.EstadoCarrera;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Representa una carrera de caballos
 */
public class Carrera {
    private String id;
    private String nombre;
    private LocalDate fecha;
    private LocalTime hora;
    private String distancia;
    private EstadoCarrera estado;
    private List<Participante> participantes;
    private Resultado resultado;
    private int minimoParticipantes;
    private int maximoParticipantes;

    // Constructor
    public Carrera(String id, String nombre, LocalDate fecha, LocalTime hora, String distancia) {
        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
        this.hora = hora;
        this.distancia = distancia;
        this.estado = EstadoCarrera.PROGRAMADA;
        this.participantes = new ArrayList<>();
        this.minimoParticipantes = 4; // Mínimo reglamentario
        this.maximoParticipantes = 20; // Máximo reglamentario
    }

    /**
     * Agrega un participante a la carrera
     */
    public boolean agregarParticipante(Participante participante) {
        if (!estado.permiteInscripciones()) {
            return false;
        }

        if (participantes.size() >= maximoParticipantes) {
            return false;
        }

        if (!participante.validarElegibilidad()) {
            return false;
        }

        // Verificar que no haya números duplicados
        for (Participante p : participantes) {
            if (p.getNumeroCompetidor() == participante.getNumeroCompetidor()) {
                return false;
            }
        }

        participantes.add(participante);

        // Si se alcanza el mínimo, cambiar estado
        if (participantes.size() >= minimoParticipantes && estado == EstadoCarrera.PROGRAMADA) {
            estado = EstadoCarrera.INSCRIPCIONES_ABIERTAS;
        }

        return true;
    }

    /**
     * Valida que se cumplan las condiciones mínimas para la carrera
     */
    public boolean validarCondicionesMinimas() {
        return participantes.size() >= minimoParticipantes &&
                fecha.isAfter(LocalDate.now().minusDays(1)) &&
                estado.estaActiva();
    }

    /**
     * Registra el resultado de la carrera
     */
    public void registrarResultado(Resultado resultado) {
        if (estado == EstadoCarrera.EN_CURSO || estado == EstadoCarrera.APUESTAS_CERRADAS) {
            this.resultado = resultado;
            this.estado = EstadoCarrera.FINALIZADA;

            // Actualizar historial de participantes
            for (Participante participante : participantes) {
                Integer posicion = resultado.obtenerPosicion(participante);
                LocalTime tiempo = resultado.obtenerTiempo(participante);

                if (posicion != null && tiempo != null) {
                    HistorialCarrera historial = new HistorialCarrera(this, posicion, tiempo, fecha);
                    participante.getCaballo().agregarHistorial(historial);
                    participante.getJinete().agregarHistorial(historial);
                }
            }
        }
    }

    /**
     * Obtiene las cuotas actuales (simulación básica)
     */
    public Map<String, Double> obtenerCuotasActuales() {
        Map<String, Double> cuotas = new HashMap<>();

        for (Participante participante : participantes) {
            // Cuota básica basada en rendimiento histórico
            EstadisticasRendimiento rendimiento = participante.obtenerRendimientoHistorico();
            double cuota = calcularCuotaBasica(rendimiento);
            cuotas.put(participante.getCaballo().getNombre(), cuota);
        }

        return cuotas;
    }

    /**
     * Calcula una cuota básica basada en el rendimiento
     */
    private double calcularCuotaBasica(EstadisticasRendimiento rendimiento) {
        if (rendimiento.getTotalCarreras() == 0) {
            return 10.0; // Cuota por defecto para debutantes
        }

        double porcentajeVictorias = rendimiento.getPorcentajeVictorias();

        if (porcentajeVictorias >= 40) return 2.5;
        else if (porcentajeVictorias >= 25) return 4.0;
        else if (porcentajeVictorias >= 15) return 6.0;
        else if (porcentajeVictorias >= 10) return 8.0;
        else return 12.0;
    }

    /**
     * Inicia las apuestas para la carrera
     */
    public boolean iniciarApuestas() {
        if (validarCondicionesMinimas() &&
                (estado == EstadoCarrera.INSCRIPCIONES_ABIERTAS || estado == EstadoCarrera.PROGRAMADA)) {
            estado = EstadoCarrera.APUESTAS_ABIERTAS;
            return true;
        }
        return false;
    }

    /**
     * Cierra las apuestas para la carrera
     */
    public boolean cerrarApuestas() {
        if (estado == EstadoCarrera.APUESTAS_ABIERTAS) {
            estado = EstadoCarrera.APUESTAS_CERRADAS;
            return true;
        }
        return false;
    }

    /**
     * Inicia la carrera
     */
    public boolean iniciarCarrera() {
        if (estado == EstadoCarrera.APUESTAS_CERRADAS && validarCondicionesMinimas()) {
            estado = EstadoCarrera.EN_CURSO;
            return true;
        }
        return false;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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

    public List<Participante> getParticipantes() { return participantes; }
    public void setParticipantes(List<Participante> participantes) { this.participantes = participantes; }

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
