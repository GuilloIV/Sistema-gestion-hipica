package mx.uv.feaa.model;

/**
 * Enumeration que define los estados posibles de una apuesta
 */
public enum EstadoApuesta {
    PENDIENTE("Pendiente"),
    CONFIRMADA("Confirmada"),
    GANADORA("Ganadora"),
    PERDEDORA("Perdedora"),
    CANCELADA("Cancelada"),
    PAGADA("Pagada"),
    REEMBOLSADA("Reembolsada");

    private final String descripcion;

    EstadoApuesta(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Verifica si la apuesta está activa
     */
    public boolean estaActiva() {
        return this == PENDIENTE || this == CONFIRMADA;
    }

    /**
     * Verifica si la apuesta ha sido procesada
     */
    public boolean estaProcesada() {
        return this == GANADORA || this == PERDEDORA || this == PAGADA;
    }

    /**
     * Verifica si la apuesta puede ser cancelada
     */
    public boolean puedeCancelarse() {
        return this == PENDIENTE || this == CONFIRMADA;
    }

    /**
     * Verifica si la apuesta requiere pago
     */
    public boolean requierePago() {
        return this == GANADORA;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
