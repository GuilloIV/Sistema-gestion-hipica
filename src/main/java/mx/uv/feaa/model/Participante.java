package mx.uv.feaa.model;

import mx.uv.feaa.enumeracion.EstadoParticipante;

/**
 * Representa un participante en una carrera (combinación de jinete y caballo)
 */
public class Participante {
    private int numeroCompetidor;
    private double pesoAsignado;
    private Caballo caballo;
    private Jinete jinete;
    private EstadoParticipante estado;

    // Constructor
    public Participante(int numeroCompetidor, double pesoAsignado, Caballo caballo, Jinete jinete) {
        this.numeroCompetidor = numeroCompetidor;
        this.pesoAsignado = pesoAsignado;
        this.caballo = caballo;
        this.jinete = jinete;
        this.estado = EstadoParticipante.INSCRITO;
    }

    /**
     * Valida si el participante es elegible para competir
     */
    public boolean validarElegibilidad() {
        // Validar que el caballo pueda participar
        if (caballo == null || !caballo.puedeParticipar()) {
            return false;
        }

        // Validar que el jinete tenga licencia válida
        if (jinete == null || !jinete.validarLicencia()) {
            return false;
        }

        // Validar peso reglamentario del jinete
        if (!jinete.validarPesoReglamentario(pesoAsignado)) {
            return false;
        }

        // Validar que el estado permita participar
        if (!estado.puedeParticipar()) {
            return false;
        }

        // Validar número de competidor válido
        if (numeroCompetidor <= 0) {
            return false;
        }

        // Validar peso asignado dentro de límites
        if (pesoAsignado < 50 || pesoAsignado > 65) { // Límites típicos en kg
            return false;
        }

        return true;
    }

    /**
     * Obtiene el rendimiento histórico combinado del jinete y caballo
     */
    public EstadisticasRendimiento obtenerRendimientoHistorico() {
        EstadisticasRendimiento rendimientoJinete = jinete.obtenerEstadisticas();
        EstadisticasRendimiento rendimientoCaballo = caballo.obtenerRendimiento();

        // Validar que ambos tengan estadísticas
        if (rendimientoJinete == null) {
            rendimientoJinete = new EstadisticasRendimiento();
        }
        if (rendimientoCaballo == null) {
            rendimientoCaballo = new EstadisticasRendimiento();
        }

        // Combinar estadísticas con peso ponderado
        // 60% peso del jinete, 40% peso del caballo
        int totalCarreras = Math.max(rendimientoJinete.getTotalCarreras(), rendimientoCaballo.getTotalCarreras());

        // Calcular victorias ponderadas
        int victoriasJinete = rendimientoJinete.getVictorias();
        int victoriasCaballo = rendimientoCaballo.getVictorias();
        int totalVictorias = (int) Math.round((victoriasJinete * 0.6) + (victoriasCaballo * 0.4));

        // Calcular colocaciones ponderadas
        int colocacionesJinete = rendimientoJinete.getColocaciones();
        int colocacionesCaballo = rendimientoCaballo.getColocaciones();
        int totalColocaciones = (int) Math.round((colocacionesJinete * 0.6) + (colocacionesCaballo * 0.4));

        return new EstadisticasRendimiento(totalCarreras, totalVictorias, totalColocaciones);
    }

    /**
     * Calcula la experiencia combinada del participante
     */
    public int obtenerExperienciaCombinada() {
        EstadisticasRendimiento rendimientoJinete = jinete.obtenerEstadisticas();
        EstadisticasRendimiento rendimientoCaballo = caballo.obtenerRendimiento();

        int experienciaJinete = rendimientoJinete != null ? rendimientoJinete.getTotalCarreras() : 0;
        int experienciaCaballo = rendimientoCaballo != null ? rendimientoCaballo.getTotalCarreras() : 0;

        return experienciaJinete + experienciaCaballo;
    }

    /**
     * Verifica si es la primera carrera juntos
     */
    public boolean esPrimeraCarreraJuntos() {
        // Buscar en el historial del caballo si ya corrió con este jinete
        for (HistorialCarrera historial : caballo.getHistorialCarreras()) {
            // Esta lógica requeriría que HistorialCarrera tenga referencia al jinete
            // Por ahora, asumimos que es su primera carrera juntos si no hay historial
        }
        return true; // Simplificado por ahora
    }

    /**
     * Actualiza el estado del participante
     */
    public boolean actualizarEstado(EstadoParticipante nuevoEstado) {
        if (nuevoEstado == null) {
            return false;
        }

        // Validar transiciones de estado válidas
        switch (estado) {
            case INSCRITO:
                if (nuevoEstado == EstadoParticipante.CONFIRMADO ||
                        nuevoEstado == EstadoParticipante.RETIRADO) {
                    estado = nuevoEstado;
                    return true;
                }
                break;
            case CONFIRMADO:
                if (nuevoEstado == EstadoParticipante.PESADO ||
                        nuevoEstado == EstadoParticipante.RETIRADO) {
                    estado = nuevoEstado;
                    return true;
                }
                break;
            case PESADO:
                if (nuevoEstado == EstadoParticipante.EN_PISTA ||
                        nuevoEstado == EstadoParticipante.RETIRADO) {
                    estado = nuevoEstado;
                    return true;
                }
                break;
            case EN_PISTA:
                if (nuevoEstado == EstadoParticipante.FINALIZADO ||
                        nuevoEstado == EstadoParticipante.DESCALIFICADO) {
                    estado = nuevoEstado;
                    return true;
                }
                break;
        }
        return false;
    }

    // Getters y Setters
    public int getNumeroCompetidor() { return numeroCompetidor; }
    public void setNumeroCompetidor(int numeroCompetidor) {
        if (numeroCompetidor > 0) {
            this.numeroCompetidor = numeroCompetidor;
        }
    }

    public double getPesoAsignado() { return pesoAsignado; }
    public void setPesoAsignado(double pesoAsignado) {
        if (pesoAsignado >= 50 && pesoAsignado <= 65) {
            this.pesoAsignado = pesoAsignado;
        }
    }

    public Caballo getCaballo() { return caballo; }
    public void setCaballo(Caballo caballo) { this.caballo = caballo; }

    public Jinete getJinete() { return jinete; }
    public void setJinete(Jinete jinete) { this.jinete = jinete; }

    public EstadoParticipante getEstado() { return estado; }
    public void setEstado(EstadoParticipante estado) { this.estado = estado; }

    @Override
    public String toString() {
        return String.format("Participante #%d: %s montando a %s (Peso: %.1f kg) - Estado: %s",
                numeroCompetidor,
                jinete != null ? jinete.getNombre() : "N/A",
                caballo != null ? caballo.getNombre() : "N/A",
                pesoAsignado,
                estado != null ? estado.getDescripcion() : "N/A");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Participante that = (Participante) obj;
        return numeroCompetidor == that.numeroCompetidor;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(numeroCompetidor);
    }
}
