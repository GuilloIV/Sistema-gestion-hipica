package mx.uv.feaa.model.entidades;

public class ApuestaSeleccion {
    private String idSeleccion;
    private String apuestaId;
    private String participanteId;
    private int ordenSeleccion;

    // Constructores
    public ApuestaSeleccion() {}

    public ApuestaSeleccion(String idSeleccion, String apuestaId, String participanteId, int ordenSeleccion) {
        this.idSeleccion = idSeleccion;
        this.apuestaId = apuestaId;
        this.participanteId = participanteId;
        this.ordenSeleccion = ordenSeleccion;
    }

    // Getters y Setters
    public String getIdSeleccion() {
        return idSeleccion;
    }

    public void setIdSeleccion(String idSeleccion) {
        this.idSeleccion = idSeleccion;
    }

    public String getApuestaId() {
        return apuestaId;
    }

    public void setApuestaId(String apuestaId) {
        this.apuestaId = apuestaId;
    }

    public String getParticipanteId() {
        return participanteId;
    }

    public void setParticipanteId(String participanteId) {
        this.participanteId = participanteId;
    }

    public int getOrdenSeleccion() {
        return ordenSeleccion;
    }

    public void setOrdenSeleccion(int ordenSeleccion) {
        this.ordenSeleccion = ordenSeleccion;
    }

    @Override
    public String toString() {
        return "ApuestaSeleccion{" +
                "idSeleccion='" + idSeleccion + '\'' +
                ", apuestaId='" + apuestaId + '\'' +
                ", participanteId='" + participanteId + '\'' +
                ", ordenSeleccion=" + ordenSeleccion +
                '}';
    }
}