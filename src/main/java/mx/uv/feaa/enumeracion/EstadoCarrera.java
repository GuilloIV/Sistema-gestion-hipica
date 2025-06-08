package mx.uv.feaa.enumeracion;

/**
 * Enumeration que define los estados posibles de una carrera
 */
public enum EstadoCarrera {
    PROGRAMADA("Programada"),
    INSCRIPCIONES_ABIERTAS("Inscripciones Abiertas"),
    APUESTAS_ABIERTAS("Apuestas Abiertas"),
    APUESTAS_CERRADAS("Apuestas Cerradas"),
    EN_CURSO("En Curso"),
    FINALIZADA("Finalizada"),
    CANCELADA("Cancelada"),
    SUSPENDIDA("Suspendida");

    private final String descripcion;

    EstadoCarrera(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Verifica si se pueden inscribir participantes
     */
    public boolean permiteInscripciones() {
        return this == PROGRAMADA || this == INSCRIPCIONES_ABIERTAS;
    }

    /**
     * Verifica si se pueden realizar apuestas
     */
    public boolean permiteApuestas() {
        return this == APUESTAS_ABIERTAS;
    }

    /**
     * Verifica si la carrera está activa
     */
    public boolean estaActiva() {
        return this != CANCELADA && this != SUSPENDIDA;
    }

    /**
     * Verifica si la carrera ha finalizado
     */
    public boolean haFinalizado() {
        return this == FINALIZADA;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}