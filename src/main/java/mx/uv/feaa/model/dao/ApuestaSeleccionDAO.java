package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.entidades.ApuestaSeleccion;
import mx.uv.feaa.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ApuestaSeleccionDAO implements IGenericDAO<ApuestaSeleccion, String> {
    private static final String TABLE = "ApuestaSeleccion";

    @Override
    public Optional<ApuestaSeleccion> getById(String id) throws SQLException {
        String sql = "SELECT * FROM " + TABLE + " WHERE idSeleccion = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearSeleccion(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<ApuestaSeleccion> getAll() throws SQLException {
        List<ApuestaSeleccion> selecciones = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE;

        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                selecciones.add(mapearSeleccion(rs));
            }
        }
        return selecciones;
    }

    @Override
    public boolean save(ApuestaSeleccion seleccion) throws SQLException {
        String sql = "INSERT INTO " + TABLE + " (idSeleccion, apuesta_id, participante_id, ordenSeleccion) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, seleccion.getIdSeleccion());
            stmt.setString(2, seleccion.getApuestaId());
            stmt.setString(3, seleccion.getParticipanteId());
            stmt.setInt(4, seleccion.getOrdenSeleccion());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(ApuestaSeleccion seleccion) throws SQLException {
        String sql = "UPDATE " + TABLE + " SET apuesta_id = ?, participante_id = ?, ordenSeleccion = ? " +
                "WHERE idSeleccion = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, seleccion.getApuestaId());
            stmt.setString(2, seleccion.getParticipanteId());
            stmt.setInt(3, seleccion.getOrdenSeleccion());
            stmt.setString(4, seleccion.getIdSeleccion());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM " + TABLE + " WHERE idSeleccion = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // Métodos adicionales específicos
    public List<ApuestaSeleccion> getByApuestaId(String apuestaId) throws SQLException {
        List<ApuestaSeleccion> selecciones = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE + " WHERE apuesta_id = ? ORDER BY ordenSeleccion";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, apuestaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    selecciones.add(mapearSeleccion(rs));
                }
            }
        }
        return selecciones;
    }

    public boolean deleteByApuestaId(String apuestaId) throws SQLException {
        String sql = "DELETE FROM " + TABLE + " WHERE apuesta_id = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, apuestaId);
            return stmt.executeUpdate() > 0;
        }
    }

    private ApuestaSeleccion mapearSeleccion(ResultSet rs) throws SQLException {
        ApuestaSeleccion seleccion = new ApuestaSeleccion();
        seleccion.setIdSeleccion(rs.getString("idSeleccion"));
        seleccion.setApuestaId(rs.getString("apuesta_id"));
        seleccion.setParticipanteId(rs.getString("participante_id"));
        seleccion.setOrdenSeleccion(rs.getInt("ordenSeleccion"));
        return seleccion;
    }
}