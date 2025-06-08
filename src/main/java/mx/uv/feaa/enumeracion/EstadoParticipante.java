package mx.uv.feaa.enumeracion;

/**
 * Enumeration que define los estados posibles de un participante
 */
public enum EstadoParticipante {
    INSCRITO("Inscrito"),
    CONFIRMADO("Confirmado"),
    PESADO("Pesado"),
    EN_PISTA("En Pista"),
    DESCALIFICADO("Descalificado"),
    RETIRADO("Retirado"),
    FINALIZADO("Finalizado");

    private final String descripcion;

    EstadoParticipante(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Verifica si el participante puede competir
     */
    public boolean puedeParticipar() {
        return this == CONFIRMADO || this == PESADO || this == EN_PISTA;
    }

    /**
     * Verifica si el participante está activo en la carrera
     */
    public boolean estaActivo() {
        return this != DESCALIFICADO && this != RETIRADO;
    }

    /**
     * Verifica si el participante ha completado la carrera
     */
    public boolean haCompletado() {
        return this == FINALIZADO;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
