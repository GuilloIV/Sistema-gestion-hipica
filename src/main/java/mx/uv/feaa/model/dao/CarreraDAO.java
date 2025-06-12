package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.entidades.Carrera;
import mx.uv.feaa.enumeracion.EstadoCarrera;
import mx.uv.feaa.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CarreraDAO implements IGenericDAO<Carrera, String> {
    private static final String TABLE = "Carrera";
    private static final String[] COLUMNS = {"idCarrera", "nombre", "fecha", "hora",
            "distancia", "estado", "minimoParticipantes", "maximoParticipantes"};

    @Override
    public Optional<Carrera> getById(String id) throws SQLException {
        final String SQL = "SELECT * FROM " + TABLE + " WHERE idCarrera = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearCarrera(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Carrera> getAll() throws SQLException {
        List<Carrera> carreras = new ArrayList<>();
        final String SQL = "SELECT * FROM " + TABLE;

        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {

            while (rs.next()) {
                carreras.add(mapearCarrera(rs));
            }
        }
        return carreras;
    }

    @Override
    public boolean save(Carrera carrera) throws SQLException {
        final String SQL = "INSERT INTO " + TABLE + " (" +
                String.join(", ", COLUMNS) + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, carrera.getIdCarrera());
            pstmt.setString(2, carrera.getNombre());
            pstmt.setDate(3, Date.valueOf(carrera.getFecha()));
            pstmt.setTime(4, Time.valueOf(carrera.getHora()));
            pstmt.setString(5, carrera.getDistancia());
            pstmt.setString(6, carrera.getEstado().name());
            pstmt.setInt(7, carrera.getMinimoParticipantes());
            pstmt.setInt(8, carrera.getMaximoParticipantes());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Carrera carrera) throws SQLException {
        final String SQL = "UPDATE " + TABLE + " SET " +
                "nombre = ?, fecha = ?, hora = ?, distancia = ?, " +
                "estado = ?, minimoParticipantes = ?, maximoParticipantes = ? " +
                "WHERE idCarrera = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, carrera.getNombre());
            pstmt.setDate(2, Date.valueOf(carrera.getFecha()));
            pstmt.setTime(3, Time.valueOf(carrera.getHora()));
            pstmt.setString(4, carrera.getDistancia());
            pstmt.setString(5, carrera.getEstado().name());
            pstmt.setInt(6, carrera.getMinimoParticipantes());
            pstmt.setInt(7, carrera.getMaximoParticipantes());
            pstmt.setString(8, carrera.getIdCarrera());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        final String SQL = "DELETE FROM " + TABLE + " WHERE idCarrera = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<Carrera> getByEstado(EstadoCarrera estado) throws SQLException {
        List<Carrera> carreras = new ArrayList<>();
        final String SQL = "SELECT * FROM " + TABLE + " WHERE estado = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, estado.name());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    carreras.add(mapearCarrera(rs));
                }
            }
        }
        return carreras;
    }

    private Carrera mapearCarrera(ResultSet rs) throws SQLException {
        Carrera carrera = new Carrera(
                rs.getString("idCarrera"),
                rs.getString("nombre"),
                rs.getDate("fecha").toLocalDate(),
                rs.getTime("hora").toLocalTime(),
                rs.getString("distancia")
        );
        carrera.setEstado(EstadoCarrera.valueOf(rs.getString("estado")));
        carrera.setMinimoParticipantes(rs.getInt("minimoParticipantes"));
        carrera.setMaximoParticipantes(rs.getInt("maximoParticipantes"));
        return carrera;
    }
}