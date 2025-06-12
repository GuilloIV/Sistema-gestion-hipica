package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.entidades.Jinete;
import mx.uv.feaa.util.ConexionBD;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JineteDAO implements IGenericDAO<Jinete, String> {
    private static final String TABLE = "Jinete";
    private static final String[] COLUMNS = {
            "idJinete", "nombre", "fechaNacimiento", "peso",
            "licencia", "fechaVigenciaLicencia"
    };

    @Override
    public Optional<Jinete> getById(String id) throws SQLException {
        final String SQL = "SELECT * FROM " + TABLE + " WHERE idJinete = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearJinete(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Jinete> getAll() throws SQLException {
        List<Jinete> jinetes = new ArrayList<>();
        final String SQL = "SELECT * FROM " + TABLE;

        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {

            while (rs.next()) {
                jinetes.add(mapearJinete(rs));
            }
        }
        return jinetes;
    }

    @Override
    public boolean save(Jinete jinete) throws SQLException {
        final String SQL = "INSERT INTO " + TABLE + " (" +
                String.join(", ", COLUMNS) + ") VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, jinete.getIdJinete());
            pstmt.setString(2, jinete.getNombre());
            pstmt.setDate(3, Date.valueOf(jinete.getFechaNacimiento()));
            pstmt.setDouble(4, jinete.getPeso());
            pstmt.setString(5, jinete.getLicencia());
            pstmt.setDate(6, Date.valueOf(jinete.getFechaVigenciaLicencia()));

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Jinete jinete) throws SQLException {
        final String SQL = "UPDATE " + TABLE + " SET " +
                "nombre = ?, fechaNacimiento = ?, peso = ?, " +
                "licencia = ?, fechaVigenciaLicencia = ? " +
                "WHERE idJinete = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, jinete.getNombre());
            pstmt.setDate(2, Date.valueOf(jinete.getFechaNacimiento()));
            pstmt.setDouble(3, jinete.getPeso());
            pstmt.setString(4, jinete.getLicencia());
            pstmt.setDate(5, Date.valueOf(jinete.getFechaVigenciaLicencia()));
            pstmt.setString(6, jinete.getIdJinete());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        final String SQL = "DELETE FROM " + TABLE + " WHERE idJinete = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Jinete mapearJinete(ResultSet rs) throws SQLException {
        Jinete jinete = new Jinete();
        jinete.setIdJinete(rs.getString("idJinete"));
        jinete.setNombre(rs.getString("nombre"));
        jinete.setFechaNacimiento(rs.getDate("fechaNacimiento").toLocalDate());
        jinete.setPeso(rs.getDouble("peso"));
        jinete.setLicencia(rs.getString("licencia"));
        jinete.setFechaVigenciaLicencia(rs.getDate("fechaVigenciaLicencia").toLocalDate());
        return jinete;
    }
}