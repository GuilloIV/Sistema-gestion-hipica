package mx.uv.feaa.data.dao;

import mx.uv.feaa.model.dao.IGenericDAO;
import mx.uv.feaa.model.entidades.Apuesta;
import mx.uv.feaa.enumeracion.EstadoApuesta;
import mx.uv.feaa.enumeracion.TipoApuesta;
import mx.uv.feaa.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ApuestaDAO implements IGenericDAO<Apuesta, String> {
    private static final String TABLE = "Apuesta";
    private static final String[] COLUMNS = {"idApuesta", "apostador_id", "carrera_id",
            "tipoApuesta", "montoApostado", "fechaApuesta", "estado",
            "cuotaAplicada", "montoGanado"};

    @Override
    public Optional<Apuesta> getById(String id) throws SQLException {
        final String SQL = "SELECT * FROM " + TABLE + " WHERE idApuesta = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearApuesta(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Apuesta> getAll() throws SQLException {
        List<Apuesta> apuestas = new ArrayList<>();
        final String SQL = "SELECT * FROM " + TABLE;

        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {

            while (rs.next()) {
                apuestas.add(mapearApuesta(rs));
            }
        }
        return apuestas;
    }

    @Override
    public boolean save(Apuesta apuesta) throws SQLException {
        final String SQL = "INSERT INTO " + TABLE + " (" +
                String.join(", ", COLUMNS) + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, apuesta.getId());
            pstmt.setString(2, apuesta.getApostador().getIdUsuario());
            pstmt.setString(3, apuesta.getCarrera().getIdCarrera());
            pstmt.setString(4, apuesta.getTipoApuesta().name());
            pstmt.setDouble(5, apuesta.getMontoApostado());
            pstmt.setTimestamp(6, Timestamp.valueOf(apuesta.getFechaApuesta()));
            pstmt.setString(7, apuesta.getEstado().name());
            pstmt.setDouble(8, apuesta.getCuotaAplicada());
            pstmt.setDouble(9, apuesta.getMontoGanado());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Apuesta apuesta) throws SQLException {
        final String SQL = "UPDATE " + TABLE + " SET " +
                "apostador_id = ?, carrera_id = ?, tipoApuesta = ?, " +
                "montoApostado = ?, fechaApuesta = ?, estado = ?, " +
                "cuotaAplicada = ?, montoGanado = ? WHERE idApuesta = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, apuesta.getApostador().getIdUsuario());
            pstmt.setString(2, apuesta.getCarrera().getIdCarrera());
            pstmt.setString(3, apuesta.getTipoApuesta().name());
            pstmt.setDouble(4, apuesta.getMontoApostado());
            pstmt.setTimestamp(5, Timestamp.valueOf(apuesta.getFechaApuesta()));
            pstmt.setString(6, apuesta.getEstado().name());
            pstmt.setDouble(7, apuesta.getCuotaAplicada());
            pstmt.setDouble(8, apuesta.getMontoGanado());
            pstmt.setString(9, apuesta.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        final String SQL = "DELETE FROM " + TABLE + " WHERE idApuesta = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<Apuesta> getByApostador(String apostadorId) throws SQLException {
        List<Apuesta> apuestas = new ArrayList<>();
        final String SQL = "SELECT * FROM " + TABLE + " WHERE apostador_id = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, apostadorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    apuestas.add(mapearApuesta(rs));
                }
            }
        }
        return apuestas;
    }

    private Apuesta mapearApuesta(ResultSet rs) throws SQLException {
        Apuesta apuesta = new Apuesta();
        apuesta.setId(rs.getString("idApuesta"));
        // Nota: Se necesitarían DAOs para cargar objetos completos
        apuesta.setMontoApostado(rs.getDouble("montoApostado"));
        apuesta.setFechaApuesta(rs.getTimestamp("fechaApuesta").toLocalDateTime());
        apuesta.setEstado(EstadoApuesta.valueOf(rs.getString("estado")));
        apuesta.setTipoApuesta(TipoApuesta.valueOf(rs.getString("tipoApuesta")));
        apuesta.setCuotaAplicada(rs.getDouble("cuotaAplicada"));
        apuesta.setMontoGanado(rs.getDouble("montoGanado"));

        return apuesta;
    }
}