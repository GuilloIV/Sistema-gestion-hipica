package mx.uv.feaa.enumeracion;

/**
 * Enumeration que define los sexos de los caballos
 */
public enum SexoCaballo {
    MACHO("Macho"),
    HEMBRA("Hembra"),
    CASTRADO("Castrado");

    private final String descripcion;

    SexoCaballo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Verifica si puede competir en carreras de machos
     */
    public boolean puedeCompeririConMachos() {
        return this == MACHO || this == CASTRADO;
    }

    /**
     * Verifica si puede competir en carreras de hembras
     */
    public boolean puedeCompetirConHembras() {
        return this == HEMBRA;
    }

    /**
     * Verifica si puede competir en carreras mixtas
     */
    public boolean puedeCompetirEnMixtas() {
        return true; // Todos pueden competir en carreras mixtas
    }

    @Override
    public String toString() {
        return descripcion;
    }
}