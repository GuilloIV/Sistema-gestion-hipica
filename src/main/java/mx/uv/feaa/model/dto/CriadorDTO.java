package mx.uv.feaa.model.dto;

import java.time.LocalDate;
import java.util.List;

public class CriadorDTO extends UsuarioDTO {
    private String licenciaCriador;
    private LocalDate fechaVigenciaLicencia;
    private String direccion;
    private String telefono;
    private String nombreHaras;
    private int caballosRegistrados;
    private int caballosActivos;
    private List<String> caballosIds;
    private int totalCarreras;
    private int totalVictorias;
    private double porcentajeVictorias;
    private boolean licenciaVigente;

    public CriadorDTO() {
        super();
        this.tipoUsuario = "CRIADOR";
        this.caballosRegistrados = 0;
        this.caballosActivos = 0;
    }

    public String getLicenciaCriador() { return licenciaCriador; }
    public void setLicenciaCriador(String licenciaCriador) { this.licenciaCriador = licenciaCriador; }

    public LocalDate getFechaVigenciaLicencia() { return fechaVigenciaLicencia; }
    public void setFechaVigenciaLicencia(LocalDate fechaVigenciaLicencia) {
        this.fechaVigenciaLicencia = fechaVigenciaLicencia;
        this.licenciaVigente = isLicenciaVigente();
    }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getNombreHaras() { return nombreHaras; }
    public void setNombreHaras(String nombreHaras) { this.nombreHaras = nombreHaras; }

    public int getCaballosRegistrados() { return caballosRegistrados; }
    public void setCaballosRegistrados(int caballosRegistrados) { this.caballosRegistrados = caballosRegistrados; }

    public int getCaballosActivos() { return caballosActivos; }
    public void setCaballosActivos(int caballosActivos) { this.caballosActivos = caballosActivos; }

    public List<String> getCaballosIds() { return caballosIds; }
    public void setCaballosIds(List<String> caballosIds) { this.caballosIds = caballosIds; }

    public int getTotalCarreras() { return totalCarreras; }
    public void setTotalCarreras(int totalCarreras) {
        this.totalCarreras = totalCarreras;
        calcularPorcentajeVictorias();
    }

    public int getTotalVictorias() { return totalVictorias; }
    public void setTotalVictorias(int totalVictorias) {
        this.totalVictorias = totalVictorias;
        calcularPorcentajeVictorias();
    }

    public double getPorcentajeVictorias() { return porcentajeVictorias; }
    public void setPorcentajeVictorias(double porcentajeVictorias) { this.porcentajeVictorias = porcentajeVictorias; }

    public boolean isLicenciaVigente() {
        return fechaVigenciaLicencia != null && !fechaVigenciaLicencia.isBefore(LocalDate.now());
    }

    public boolean getLicenciaVigente() { return licenciaVigente; }
    public void setLicenciaVigente(boolean licenciaVigente) { this.licenciaVigente = licenciaVigente; }

    private void calcularPorcentajeVictorias() {
        if (totalCarreras > 0) {
            this.porcentajeVictorias = (double) totalVictorias / totalCarreras * 100;
        } else {
            this.porcentajeVictorias = 0.0;
        }
    }
}