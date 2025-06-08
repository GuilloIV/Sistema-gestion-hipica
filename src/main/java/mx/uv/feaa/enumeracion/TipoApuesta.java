package mx.uv.feaa.enumeracion;


public enum TipoApuesta {
    GANADOR("Ganador", "Acertar el caballo que llegue en primer lugar"),
    COLOCADO("Colocado", "Acertar un caballo que llegue en los primeros lugares"),
    EXACTA("Exacta", "Acertar los dos primeros caballos en orden exacto"),
    QUINELA("Quinela", "Acertar los dos primeros caballos sin importar el orden"),
    TRIFECTA("Trifecta", "Acertar los tres primeros caballos en orden exacto");

    private final String nombre;
    private final String descripcion;

    TipoApuesta(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
