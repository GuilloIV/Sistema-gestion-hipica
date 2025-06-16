package mx.uv.feaa.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ApostadorDTO extends UsuarioDTO {
    private BigDecimal saldo;
    private BigDecimal limiteApuesta;
    private BigDecimal totalApostado;
    private BigDecimal totalGanado;
    private int apuestasRealizadas;
    private LocalDate ultimaActividad;




    public ApostadorDTO() {
        super();
        this.tipoUsuario = "APOSTADOR";
        this.saldo = BigDecimal.ZERO;
        this.limiteApuesta = new BigDecimal("1000.00");
        this.totalApostado = BigDecimal.ZERO;
        this.totalGanado = BigDecimal.ZERO;
        this.apuestasRealizadas = 0;
    }

    public ApostadorDTO(String id, String nombreUsuario, String email, String password) {
        this();
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.password = password;
    }

    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }

    public BigDecimal getLimiteApuesta() { return limiteApuesta; }
    public void setLimiteApuesta(BigDecimal limiteApuesta) { this.limiteApuesta = limiteApuesta; }

    public BigDecimal getTotalApostado() { return totalApostado; }
    public void setTotalApostado(BigDecimal totalApostado) { this.totalApostado = totalApostado; }

    public BigDecimal getTotalGanado() { return totalGanado; }
    public void setTotalGanado(BigDecimal totalGanado) { this.totalGanado = totalGanado; }

    public int getApuestasRealizadas() { return apuestasRealizadas; }
    public void setApuestasRealizadas(int apuestasRealizadas) { this.apuestasRealizadas = apuestasRealizadas; }

    public LocalDate getUltimaActividad() { return ultimaActividad; }
    public void setUltimaActividad(LocalDate ultimaActividad) { this.ultimaActividad = ultimaActividad; }

    public BigDecimal getBalanceNeto() {
        return totalGanado.subtract(totalApostado);
    }

    public double getPorcentajeExito() {
        if (apuestasRealizadas == 0) return 0.0;
        return (totalGanado.doubleValue() / totalApostado.doubleValue()) * 100;
    }

    public boolean puedeApostar(BigDecimal montoApuesta) {
        return activo &&
                saldo.compareTo(montoApuesta) >= 0 &&
                montoApuesta.compareTo(limiteApuesta) <= 0 &&
                montoApuesta.compareTo(new BigDecimal("10.00")) >= 0;
    }

    public void debitarSaldo(BigDecimal monto) {
        if (saldo.compareTo(monto) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }
        this.saldo = this.saldo.subtract(monto);
    }

    public void acreditarSaldo(BigDecimal monto) {
        this.saldo = this.saldo.add(monto);
    }
}
