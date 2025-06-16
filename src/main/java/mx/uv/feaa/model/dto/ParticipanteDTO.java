package mx.uv.feaa.model.dto;


public class ParticipanteDTO {
    private String id;
    private String nombre;
    private String tipo; // jinete, entrenador, criador, dueño
    private String nacionalidad;

    public ParticipanteDTO() {
    }

    public ParticipanteDTO(String id, String nombre, String tipo, String nacionalidad) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.nacionalidad = nacionalidad;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getNacionalidad() { return nacionalidad; }
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }
}
