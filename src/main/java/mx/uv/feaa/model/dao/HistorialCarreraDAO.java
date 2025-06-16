package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.entidades.HistorialCarrera;
import mx.uv.feaa.util.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HistorialCarreraDAO implements IGenericDAO<HistorialCarrera, String> {
    private static final String TABLE_NAME = "HistorialCarrera";
    private static final String SELECT_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE idHistorial = ?";
    private static final String SELECT_ALL = "SELECT * FROM " + TABLE_NAME;
    private static final String INSERT = "INSERT INTO " + TABLE_NAME +
            "(idHistorial, carrera_id, caballo_id, jinete_id, posicion, tiempo, fecha, hipodromo) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE " + TABLE_NAME + " SET " +
            "carrera_id = ?, caballo_id = ?, jinete_id = ?, posicion = ?, " +
            "tiempo = ?, fecha = ?, hipodromo = ? WHERE idHistorial = ?";
    private static final String DELETE = "DELETE FROM " + TABLE_NAME + " WHERE idHistorial = ?";

    @Override
    public Optional<HistorialCarrera> getById(String id) throws SQLException {
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearHistorial(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<HistorialCarrera> getAll() throws SQLException {
        List<HistorialCarrera> historiales = new ArrayList<>();

        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL)) {

            while (rs.next()) {
                historiales.add(mapearHistorial(rs));
            }
        }
        return historiales;
    }

    @Override
    public boolean save(HistorialCarrera historial) throws SQLException {
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT)) {

            prepararStatementParaInsertUpdate(stmt, historial);
            stmt.setString(1, historial.getIdHistorial());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(HistorialCarrera historial) throws SQLException {
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE)) {

            prepararStatementParaInsertUpdate(stmt, historial);
            stmt.setString(8, historial.getIdHistorial());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE)) {

            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private HistorialCarrera mapearHistorial(ResultSet rs) throws SQLException {
        HistorialCarrera historial = new HistorialCarrera();

        historial.setIdHistorial(rs.getString("idHistorial"));
        historial.setCarreraById(rs.getString("carrera_id"));
        historial.setCaballoById(rs.getString("caballo_id"));
        historial.setJineteById(rs.getString("jinete_id"));
        historial.setPosicion(rs.getInt("posicion"));

        Time tiempo = rs.getTime("tiempo");
        if (tiempo != null) {
            historial.setTiempo(tiempo.toLocalTime());
        }

        Date fecha = rs.getDate("fecha");
        if (fecha != null) {
            historial.setFecha(fecha.toLocalDate());
        }

        historial.setHipodromo(rs.getString("hipodromo"));

        return historial;
    }

    private void prepararStatementParaInsertUpdate(PreparedStatement stmt, HistorialCarrera historial)
            throws SQLException {

        stmt.setString(2, historial.getIdCarrera());
        stmt.setString(3, historial.getIdCaballo());
        stmt.setString(4, historial.getIdJinete());
        stmt.setInt(5, historial.getPosicion());

        if (historial.getTiempo() != null) {
            stmt.setTime(6, Time.valueOf(historial.getTiempo()));
        } else {
            stmt.setNull(6, Types.TIME);
        }

        if (historial.getFecha() != null) {
            stmt.setDate(7, Date.valueOf(historial.getFecha()));
        } else {
            stmt.setNull(7, Types.DATE);
        }

        stmt.setString(8, historial.getHipodromo());
    }

    // Métodos adicionales específicos para HistorialCarrera
    public List<HistorialCarrera> getByCaballoId(String idCaballo) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE caballo_id = ?";
        List<HistorialCarrera> historiales = new ArrayList<>();

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idCaballo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    historiales.add(mapearHistorial(rs));
                }
            }
        }
        return historiales;
    }

    public List<HistorialCarrera> getByJineteId(String idJinete) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE jinete_id = ?";
        List<HistorialCarrera> historiales = new ArrayList<>();

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idJinete);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    historiales.add(mapearHistorial(rs));
                }
            }
        }
        return historiales;
    }
}