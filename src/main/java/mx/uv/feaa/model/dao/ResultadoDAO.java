package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.entidades.Resultado;
import mx.uv.feaa.util.ConexionBD;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class ResultadoDAO implements IGenericDAO<Resultado, String> {
    @Override
    public Optional<Resultado> getById(String id) throws SQLException {
        String sql = "SELECT * FROM Resultado WHERE idResultado = ?";
        Resultado resultado = null;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    resultado = mapearResultado(rs);
                    // Cargar detalles del resultado
                    cargarDetallesResultado(conn, resultado);
                }
            }
        }
        return Optional.ofNullable(resultado);
    }

    @Override
    public List<Resultado> getAll() throws SQLException {
        String sql = "SELECT * FROM Resultado";
        List<Resultado> resultados = new ArrayList<>();

        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Resultado resultado = mapearResultado(rs);
                // Cargar detalles del resultado
                cargarDetallesResultado(conn, resultado);
                resultados.add(resultado);
            }
        }
        return resultados;
    }

    @Override
    public boolean save(Resultado resultado) throws SQLException {
        String sql = "INSERT INTO Resultado (idResultado, carrera_id, fechaRegistro) VALUES (?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, resultado.getIdResultado());
            stmt.setString(2, resultado.getIdCarrera());
            stmt.setDate(3, Date.valueOf(resultado.getFechaRegistro()));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                // Guardar detalles del resultado
                guardarDetallesResultado(conn, resultado);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean update(Resultado resultado) throws SQLException {
        String sql = "UPDATE Resultado SET carrera_id = ?, fechaRegistro = ? WHERE idResultado = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, resultado.getIdCarrera());
            stmt.setDate(2, Date.valueOf(resultado.getFechaRegistro()));
            stmt.setString(3, resultado.getIdResultado());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                // Actualizar detalles del resultado
                actualizarDetallesResultado(conn, resultado);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(String id) throws SQLException {
        // Los detalles se eliminan en cascada por la FK
        String sql = "DELETE FROM Resultado WHERE idResultado = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Resultado mapearResultado(ResultSet rs) throws SQLException {
        Resultado resultado = new Resultado(
                rs.getString("idResultado"),
                rs.getString("carrera_id")
        );
        resultado.setFechaRegistro(rs.getDate("fechaRegistro").toLocalDate());
        return resultado;
    }

    private void cargarDetallesResultado(Connection conn, Resultado resultado) throws SQLException {
        String sql = "SELECT * FROM ResultadoDetalle WHERE resultado_id = ? ORDER BY posicion";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, resultado.getIdResultado());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int posicion = rs.getInt("posicion");
                    String idParticipante = rs.getString("participante_id");
                    LocalTime tiempo = rs.getTime("tiempoOficial").toLocalTime();

                    resultado.registrarPosicion(posicion, idParticipante, tiempo);
                }
            }
        }
    }

    private void guardarDetallesResultado(Connection conn, Resultado resultado) throws SQLException {
        String sql = "INSERT INTO ResultadoDetalle (idDetalle, resultado_id, participante_id, posicion, tiempoOficial) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Map.Entry<Integer, String> entry : resultado.getPosiciones().entrySet()) {
                int posicion = entry.getKey();
                String idParticipante = entry.getValue();
                LocalTime tiempo = resultado.getTiempos().get(idParticipante);

                stmt.setString(1, UUID.randomUUID().toString());
                stmt.setString(2, resultado.getIdResultado());
                stmt.setString(3, idParticipante);
                stmt.setInt(4, posicion);
                stmt.setTime(5, Time.valueOf(tiempo));

                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    private void actualizarDetallesResultado(Connection conn, Resultado resultado) throws SQLException {
        // Primero eliminar los detalles existentes
        String deleteSql = "DELETE FROM ResultadoDetalle WHERE resultado_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            stmt.setString(1, resultado.getIdResultado());
            stmt.executeUpdate();
        }

        // Luego insertar los nuevos detalles
        guardarDetallesResultado(conn, resultado);
    }

    // Métodos adicionales específicos para Resultado
    public Optional<Resultado> getByCarreraId(String idCarrera) throws SQLException {
        String sql = "SELECT * FROM Resultado WHERE carrera_id = ?";
        Resultado resultado = null;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idCarrera);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    resultado = mapearResultado(rs);
                    cargarDetallesResultado(conn, resultado);
                }
            }
        }
        return Optional.ofNullable(resultado);
    }
}