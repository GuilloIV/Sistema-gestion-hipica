package mx.uv.feaa.model.entidades;

import mx.uv.feaa.enumeracion.TipoApuesta;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ApuestaGanador extends Apuesta {
    public ApuestaGanador(String idApuesta, String idUsuario, String idCarrera,
                          TipoApuesta tipoApuesta, double montoApostado) {
        super(idApuesta, idUsuario, idCarrera, tipoApuesta, montoApostado);
    }

    @Override
    public double calcularDividendo(Resultado resultado) {
        // Implementación específica para apuestas de ganador
        if (esGanadora(resultado)) {
            return getMontoApostado() * getCuotaAplicada();
        }
        return 0;
    }

    @Override
    public boolean esGanadora(Resultado resultado) {
        if (resultado == null || getSeleccion() == null || getSeleccion().isEmpty()) {
            return false;
        }

        String idParticipanteSeleccionado = (String) getSeleccion().get("participante");
        String idGanador = resultado.obtenerGanador();

        return idParticipanteSeleccionado != null &&
                idParticipanteSeleccionado.equals(idGanador);
    }
}
