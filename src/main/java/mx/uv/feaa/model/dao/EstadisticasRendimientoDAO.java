package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.entidades.EstadisticasRendimiento;
import mx.uv.feaa.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EstadisticasRendimientoDAO implements IGenericDAO<EstadisticasRendimiento, String> {

    private static final String TABLA = "EstadisticasRendimiento";

    @Override
    public Optional<EstadisticasRendimiento> getById(String id) throws SQLException {
        String sql = String.format("SELECT * FROM %s WHERE idEstadistica = ?", TABLA);
        EstadisticasRendimiento estadistica = null;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    estadistica = mapearEstadistica(rs);
                }
            }
        }

        return Optional.ofNullable(estadistica);
    }

    @Override
    public List<EstadisticasRendimiento> getAll() throws SQLException {
        String sql = String.format("SELECT * FROM %s", TABLA);
        List<EstadisticasRendimiento> estadisticas = new ArrayList<>();

        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                estadisticas.add(mapearEstadistica(rs));
            }
        }

        return estadisticas;
    }

    @Override
    public boolean save(EstadisticasRendimiento estadistica) throws SQLException {
        String sql = String.format("INSERT INTO %s (idEstadistica, caballo_id, jinete_id, totalCarreras, victorias, " +
                "colocaciones, promedioTiempo, porcentajeVictorias) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", TABLA);

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            configurarStatement(stmt, estadistica);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(EstadisticasRendimiento estadistica) throws SQLException {
        String sql = String.format("UPDATE %s SET caballo_id = ?, jinete_id = ?, totalCarreras = ?, victorias = ?, " +
                "colocaciones = ?, promedioTiempo = ?, porcentajeVictorias = ? WHERE idEstadistica = ?", TABLA);

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            configurarStatement(stmt, estadistica);
            stmt.setString(8, estadistica.getIdEstadistica());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        String sql = String.format("DELETE FROM %s WHERE idEstadistica = ?", TABLA);

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // Métodos adicionales específicos para EstadisticasRendimiento

    public Optional<EstadisticasRendimiento> getByCaballoId(String caballoId) throws SQLException {
        String sql = String.format("SELECT * FROM %s WHERE caballo_id = ?", TABLA);
        EstadisticasRendimiento estadistica = null;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, caballoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    estadistica = mapearEstadistica(rs);
                }
            }
        }

        return Optional.ofNullable(estadistica);
    }

    public Optional<EstadisticasRendimiento> getByJineteId(String jineteId) throws SQLException {
        String sql = String.format("SELECT * FROM %s WHERE jinete_id = ?", TABLA);
        EstadisticasRendimiento estadistica = null;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, jineteId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    estadistica = mapearEstadistica(rs);
                }
            }
        }

        return Optional.ofNullable(estadistica);
    }

    // Métodos auxiliares

    private EstadisticasRendimiento mapearEstadistica(ResultSet rs) throws SQLException {
        EstadisticasRendimiento estadistica = new EstadisticasRendimiento();

        estadistica.setIdEstadistica(rs.getString("idEstadistica"));
        estadistica.setIdEntidad(rs.getString("caballo_id") != null ? rs.getString("caballo_id") : rs.getString("jinete_id"));
        estadistica.setTipoEntidad(rs.getString("caballo_id") != null ? "CABALLO" : "JINETE");
        estadistica.setTotalCarreras(rs.getInt("totalCarreras"));
        estadistica.setVictorias(rs.getInt("victorias"));
        estadistica.setColocaciones(rs.getInt("colocaciones"));
        estadistica.setPromedioTiempo(rs.getTime("promedioTiempo") != null ? rs.getTime("promedioTiempo").toLocalTime() : null);
        estadistica.setPorcentajeVictorias(rs.getDouble("porcentajeVictorias"));

        return estadistica;
    }

    private void configurarStatement(PreparedStatement stmt, EstadisticasRendimiento estadistica) throws SQLException {
        stmt.setString(1, estadistica.getIdEstadistica());

        if (estadistica.getTipoEntidad().equals("CABALLO")) {
            stmt.setString(2, estadistica.getIdEntidad());
            stmt.setNull(3, Types.VARCHAR);
        } else {
            stmt.setNull(2, Types.VARCHAR);
            stmt.setString(3, estadistica.getIdEntidad());
        }

        stmt.setInt(4, estadistica.getTotalCarreras());
        stmt.setInt(5, estadistica.getVictorias());
        stmt.setInt(6, estadistica.getColocaciones());

        if (estadistica.getPromedioTiempo() != null) {
            stmt.setTime(7, Time.valueOf(estadistica.getPromedioTiempo()));
        } else {
            stmt.setNull(7, Types.TIME);
        }

        stmt.setDouble(8, estadistica.getPorcentajeVictorias());
    }
}