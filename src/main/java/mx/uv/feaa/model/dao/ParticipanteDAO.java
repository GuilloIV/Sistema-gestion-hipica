package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.dao.IGenericDAO;
import mx.uv.feaa.enumeracion.EstadoParticipante;
import mx.uv.feaa.model.entidades.Participante;
import mx.uv.feaa.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParticipanteDAO implements IGenericDAO<Participante, String> {
    private static final String TABLE = "Participante";
    private static final String[] COLUMNS = {
            "idParticipante", "carrera_id", "numeroCompetidor", "pesoAsignado",
            "caballo_id", "jinete_id", "estado"
    };

    @Override
    public Optional<Participante> getById(String id) throws SQLException {
        final String SQL = "SELECT * FROM " + TABLE + " WHERE idParticipante = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearParticipante(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Participante> getAll() throws SQLException {
        List<Participante> participantes = new ArrayList<>();
        final String SQL = "SELECT * FROM " + TABLE;

        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {

            while (rs.next()) {
                participantes.add(mapearParticipante(rs));
            }
        }
        return participantes;
    }

    @Override
    public boolean save(Participante participante) throws SQLException {
        final String SQL = "INSERT INTO " + TABLE + " (" +
                String.join(", ", COLUMNS) + ") VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, participante.getIdParticipante());
            pstmt.setString(2, participante.getCarrera().getIdCarrera());
            pstmt.setInt(3, participante.getNumeroCompetidor());
            pstmt.setDouble(4, participante.getPesoAsignado());
            pstmt.setString(5, participante.getCaballo().getIdCaballo());
            pstmt.setString(6, participante.getJinete().getIdJinete());
            pstmt.setString(7, participante.getEstado().name());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Participante participante) throws SQLException {
        final String SQL = "UPDATE " + TABLE + " SET " +
                "carrera_id = ?, numeroCompetidor = ?, pesoAsignado = ?, " +
                "caballo_id = ?, jinete_id = ?, estado = ? " +
                "WHERE idParticipante = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, participante.getCarrera().getIdCarrera());
            pstmt.setInt(2, participante.getNumeroCompetidor());
            pstmt.setDouble(3, participante.getPesoAsignado());
            pstmt.setString(4, participante.getCaballo().getIdCaballo());
            pstmt.setString(5, participante.getJinete().getIdJinete());
            pstmt.setString(6, participante.getEstado().name());
            pstmt.setString(7, participante.getIdParticipante());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        final String SQL = "DELETE FROM " + TABLE + " WHERE idParticipante = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<Participante> getByCarrera(String carreraId) throws SQLException {
        List<Participante> participantes = new ArrayList<>();
        final String SQL = "SELECT * FROM " + TABLE + " WHERE carrera_id = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, carreraId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    participantes.add(mapearParticipante(rs));
                }
            }
        }
        return participantes;
    }

    private Participante mapearParticipante(ResultSet rs) throws SQLException {
        Participante participante = new Participante();
        participante.setIdParticipante(rs.getString("idParticipante"));
        participante.setNumeroCompetidor(rs.getInt("numeroCompetidor"));
        participante.setPesoAsignado(rs.getDouble("pesoAsignado"));
        participante.setEstado(EstadoParticipante.valueOf(rs.getString("estado")));
        return participante;
    }
}