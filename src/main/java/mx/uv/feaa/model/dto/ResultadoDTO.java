package mx.uv.feaa.model.dto;

public class ResultadoDTO {
    private String id;
    private String carreraId;
    private String caballoId;
    private Integer posicion;
    private Double tiempo;
    private String jockeyId;

    public ResultadoDTO() {
    }

    public ResultadoDTO(String id, String carreraId, String caballoId, Integer posicion, Double tiempo, String jockeyId) {
        this.id = id;
        this.carreraId = carreraId;
        this.caballoId = caballoId;
        this.posicion = posicion;
        this.tiempo = tiempo;
        this.jockeyId = jockeyId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCarreraId() { return carreraId; }
    public void setCarreraId(String carreraId) { this.carreraId = carreraId; }

    public String getCaballoId() { return caballoId; }
    public void setCaballoId(String caballoId) { this.caballoId = caballoId; }

    public Integer getPosicion() { return posicion; }
    public void setPosicion(Integer posicion) { this.posicion = posicion; }

    public Double getTiempo() { return tiempo; }
    public void setTiempo(Double tiempo) { this.tiempo = tiempo; }

    public String getJockeyId() { return jockeyId; }
    public void setJockeyId(String jockeyId) { this.jockeyId = jockeyId; }
}