package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.dao.IGenericDAO;
import mx.uv.feaa.model.entidades.Apostador;
import mx.uv.feaa.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ApostadorDAO implements IGenericDAO<Apostador, String> {
    private static final String TABLE = "Apostador";
    private static final String[] COLUMNS = {"idUsuario", "saldo", "limiteApuesta", "totalApostado",
            "totalGanado", "apuestasRealizadas", "ultimaActividad", "nombre", "telefono"};

    @Override
    public Optional<Apostador> getById(String id) throws SQLException {
        final String SQL = "SELECT a.*, u.* FROM " + TABLE + " a " +
                "JOIN Usuario u ON a.idUsuario = u.idUsuario " +
                "WHERE a.idUsuario = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearApostador(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Apostador> getAll() throws SQLException {
        List<Apostador> apostadores = new ArrayList<>();
        final String SQL = "SELECT a.*, u.* FROM " + TABLE + " a " +
                "JOIN Usuario u ON a.idUsuario = u.idUsuario";

        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {

            while (rs.next()) {
                apostadores.add(mapearApostador(rs));
            }
        }
        return apostadores;
    }

    @Override
    public boolean save(Apostador apostador) throws SQLException {
        // Primero insertar en Usuario
        final String SQL_USUARIO = "INSERT INTO Usuario (idUsuario, nombreUsuario, email, password, activo) " +
                "VALUES (?, ?, ?, ?, ?)";

        // Luego insertar en Apostador
        final String SQL_APOSTADOR = "INSERT INTO " + TABLE + " (" +
                String.join(", ", COLUMNS) + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        return ConexionBD.ejecutarTransaccion(conn -> {
            try (PreparedStatement pstmtUsuario = conn.prepareStatement(SQL_USUARIO);
                 PreparedStatement pstmtApostador = conn.prepareStatement(SQL_APOSTADOR)) {

                // Insertar en Usuario
                pstmtUsuario.setString(1, apostador.getIdUsuario());
                pstmtUsuario.setString(2, apostador.getNombreUsuario());
                pstmtUsuario.setString(3, apostador.getEmail());
                pstmtUsuario.setString(4, apostador.getPassword());
                pstmtUsuario.setBoolean(5, apostador.isActivo());
                pstmtUsuario.executeUpdate();

                // Insertar en Apostador
                pstmtApostador.setString(1, apostador.getIdUsuario());
                pstmtApostador.setDouble(2, apostador.getSaldo());
                pstmtApostador.setDouble(3, apostador.getLimiteApuesta());
                // Resto de parámetros...
                return pstmtApostador.executeUpdate() > 0;
            }
        });
    }

    @Override
    public boolean update(Apostador apostador) throws SQLException {
        final String SQL = "UPDATE " + TABLE + " SET " +
                "saldo = ?, limiteApuesta = ?, totalApostado = ?, " +
                "totalGanado = ?, apuestasRealizadas = ?, ultimaActividad = ?, " +
                "nombre = ?, telefono = ? WHERE idUsuario = ?";

        return ConexionBD.ejecutarTransaccion(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
                pstmt.setDouble(1, apostador.getSaldo());
                pstmt.setDouble(2, apostador.getLimiteApuesta());
                pstmt.setDouble(3, apostador.getTotalApostado());
                pstmt.setDouble(4, apostador.getTotalGanado());
                pstmt.setInt(5, apostador.getApuestasRealizadas());
                pstmt.setDate(6, apostador.getUltimaActividad() != null ?
                        Date.valueOf(apostador.getUltimaActividad()) : null);
                pstmt.setString(7, apostador.getNombre());
                pstmt.setString(8, apostador.getTelefono());
                pstmt.setString(9, apostador.getIdUsuario());

                boolean b = pstmt.executeUpdate() > 0;

            }
        });
    }

    @Override
    public boolean delete(String id) throws SQLException {
        final String SQL = "DELETE FROM " + TABLE + " WHERE idUsuario = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Apostador mapearApostador(ResultSet rs) throws SQLException {
        Apostador apostador = new Apostador();
        apostador.setIdUsuario(rs.getString("idUsuario"));
        apostador.setSaldo(rs.getDouble("saldo"));
        apostador.setLimiteApuesta(rs.getDouble("limiteApuesta"));
        apostador.setTotalApostado(rs.getDouble("totalApostado"));
        apostador.setTotalGanado(rs.getDouble("totalGanado"));
        apostador.setApuestasRealizadas(rs.getInt("apuestasRealizadas"));

        Date ultimaActividad = rs.getDate("ultimaActividad");
        apostador.setUltimaActividad(ultimaActividad != null ? ultimaActividad.toLocalDate() : null);

        apostador.setNombre(rs.getString("nombre"));
        apostador.setTelefono(rs.getString("telefono"));

        // Campos de Usuario
        apostador.setNombreUsuario(rs.getString("nombreUsuario"));
        apostador.setEmail(rs.getString("email"));
        apostador.setPassword(rs.getString("password"));
        apostador.setActivo(rs.getBoolean("activo"));
        apostador.setFechaRegistro(rs.getTimestamp("fechaRegistro").toLocalDateTime());

        Timestamp ultimoAcceso = rs.getTimestamp("ultimoAcceso");
        apostador.setUltimoAcceso(ultimoAcceso != null ? ultimoAcceso.toLocalDateTime() : null);

        return apostador;
    }
}