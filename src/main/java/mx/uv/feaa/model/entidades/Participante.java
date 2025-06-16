package mx.uv.feaa.model.entidades;

import mx.uv.feaa.enumeracion.EstadoParticipante;

public class Participante {
    private String idParticipante;
    private int numeroCompetidor;
    private double pesoAsignado;
    private Caballo caballo;
    private Jinete jinete;
    private EstadoParticipante estado;
    private Carrera carrera;

    public Participante(String idParticipante, int numeroCompetidor, double pesoAsignado,
                        Caballo caballo, Jinete jinete) {
        this.idParticipante = idParticipante;
        this.numeroCompetidor = numeroCompetidor;
        this.pesoAsignado = pesoAsignado;
        this.caballo = caballo;
        this.jinete = jinete;
        this.estado = EstadoParticipante.INSCRITO;
    }

    public boolean validarElegibilidad() {
        if (caballo == null || !caballo.puedeParticipar() ||
                jinete == null || !jinete.validarLicencia() ||
                !jinete.validarPesoReglamentario(pesoAsignado) ||
                !estado.puedeParticipar() ||
                numeroCompetidor <= 0 ||
                pesoAsignado < 50 || pesoAsignado > 65) {
            return false;
        }
        return true;
    }

    public EstadisticasRendimiento obtenerRendimientoHistorico() {
        EstadisticasRendimiento rendimientoJinete = jinete.obtenerEstadisticas();
        EstadisticasRendimiento rendimientoCaballo = caballo.obtenerEstadisticasCaballo();

        rendimientoJinete = rendimientoJinete != null ? rendimientoJinete : new EstadisticasRendimiento();
        rendimientoCaballo = rendimientoCaballo != null ? rendimientoCaballo : new EstadisticasRendimiento();

        int totalCarreras = Math.max(rendimientoJinete.getTotalCarreras(), rendimientoCaballo.getTotalCarreras());
        int totalVictorias = (int) Math.round((rendimientoJinete.getVictorias() * 0.6) + (rendimientoCaballo.getVictorias() * 0.4));
        int totalColocaciones = (int) Math.round((rendimientoJinete.getColocaciones() * 0.6) + (rendimientoCaballo.getColocaciones() * 0.4));

        return new EstadisticasRendimiento(totalCarreras, totalVictorias, totalColocaciones);
    }

    public int obtenerExperienciaCombinada() {
        EstadisticasRendimiento rendimientoJinete = jinete.obtenerEstadisticas();
        EstadisticasRendimiento rendimientoCaballo = caballo.obtenerEstadisticasCaballo();
        int experienciaJinete = rendimientoJinete != null ? rendimientoJinete.getTotalCarreras() : 0;
        int experienciaCaballo = rendimientoCaballo != null ? rendimientoCaballo.getTotalCarreras() : 0;
        return experienciaJinete + experienciaCaballo;
    }

    public boolean esPrimeraCarreraJuntos() {
        return true; // Simplificado por ahora
    }

    public boolean actualizarEstado(EstadoParticipante nuevoEstado) {
        if (nuevoEstado == null) return false;

        switch (estado) {
            case INSCRITO:
                if (nuevoEstado == EstadoParticipante.CONFIRMADO || nuevoEstado == EstadoParticipante.RETIRADO) {
                    estado = nuevoEstado;
                    return true;
                }
                break;
            case CONFIRMADO:
                if (nuevoEstado == EstadoParticipante.PESADO || nuevoEstado == EstadoParticipante.RETIRADO) {
                    estado = nuevoEstado;
                    return true;
                }
                break;
            case PESADO:
                if (nuevoEstado == EstadoParticipante.EN_PISTA || nuevoEstado == EstadoParticipante.RETIRADO) {
                    estado = nuevoEstado;
                    return true;
                }
                break;
            case EN_PISTA:
                if (nuevoEstado == EstadoParticipante.FINALIZADO || nuevoEstado == EstadoParticipante.DESCALIFICADO) {
                    estado = nuevoEstado;
                    return true;
                }
                break;
        }
        return false;
    }

    // Getters and Setters
    public String getIdParticipante() { return idParticipante; }
    public void setIdParticipante(String idParticipante) { this.idParticipante = idParticipante; }
    public int getNumeroCompetidor() { return numeroCompetidor; }
    public void setNumeroCompetidor(int numeroCompetidor) {
        if (numeroCompetidor > 0) this.numeroCompetidor = numeroCompetidor;
    }
    public double getPesoAsignado() { return pesoAsignado; }
    public void setPesoAsignado(double pesoAsignado) {
        if (pesoAsignado >= 50 && pesoAsignado <= 65) this.pesoAsignado = pesoAsignado;
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


    public Carrera getCarrera() {
        return carrera;
    }

    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
    }
}