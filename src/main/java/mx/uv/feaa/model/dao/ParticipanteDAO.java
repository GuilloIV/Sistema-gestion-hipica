package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.entidades.*;
import mx.uv.feaa.enumeracion.EstadoParticipante;
import mx.uv.feaa.util.ConexionBD;
import java.sql.*;
import java.util.*;

public class ParticipanteDAO implements IGenericDAO<Participante, String> {
    private final CaballoDAO caballoDAO;
    private final JineteDAO jineteDAO;
    private final CarreraDAO carreraDAO;

    public ParticipanteDAO() {
        this.caballoDAO = new CaballoDAO();
        this.jineteDAO = new JineteDAO();
        this.carreraDAO = new CarreraDAO();
    }

    @Override
    public Optional<Participante> getById(String id) throws SQLException {
        String sql = "SELECT * FROM Participante WHERE idParticipante = ?";
        Participante participante = null;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    participante = mapearParticipante(rs);
                }
            }
        }
        return Optional.ofNullable(participante);
    }

    @Override
    public List<Participante> getAll() throws SQLException {
        String sql = "SELECT * FROM Participante";
        List<Participante> participantes = new ArrayList<>();

        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                participantes.add(mapearParticipante(rs));
            }
        }
        return participantes;
    }

    @Override
    public boolean save(Participante participante) throws SQLException {
        String sql = "INSERT INTO Participante (idParticipante, carrera_id, numeroCompetidor, " +
                "pesoAsignado, caballo_id, jinete_id, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, participante.getIdParticipante());
            stmt.setString(2, participante.getCarrera().getIdCarrera());
            stmt.setInt(3, participante.getNumeroCompetidor());
            stmt.setDouble(4, participante.getPesoAsignado());
            stmt.setString(5, participante.getCaballo().getIdCaballo());
            stmt.setString(6, participante.getJinete().getIdJinete());
            stmt.setString(7, participante.getEstado().name());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Participante participante) throws SQLException {
        String sql = "UPDATE Participante SET carrera_id = ?, numeroCompetidor = ?, pesoAsignado = ?, " +
                "caballo_id = ?, jinete_id = ?, estado = ? WHERE idParticipante = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, participante.getCarrera().getIdCarrera());
            stmt.setInt(2, participante.getNumeroCompetidor());
            stmt.setDouble(3, participante.getPesoAsignado());
            stmt.setString(4, participante.getCaballo().getIdCaballo());
            stmt.setString(5, participante.getJinete().getIdJinete());
            stmt.setString(6, participante.getEstado().name());
            stmt.setString(7, participante.getIdParticipante());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM Participante WHERE idParticipante = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Participante mapearParticipante(ResultSet rs) throws SQLException {
        Participante participante = new Participante(
                rs.getString("idParticipante"),
                rs.getInt("numeroCompetidor"),
                rs.getDouble("pesoAsignado"),
                caballoDAO.getById(rs.getString("caballo_id")).orElse(null),
                jineteDAO.getById(rs.getString("jinete_id")).orElse(null)
        );

        participante.setEstado(EstadoParticipante.valueOf(rs.getString("estado")));
        participante.setCarrera(carreraDAO.getById(rs.getString("carrera_id")).orElse(null));

        return participante;
    }

    public List<Participante> getByCarreraId(String carreraId) throws SQLException {
        String sql = "SELECT * FROM Participante WHERE carrera_id = ? ORDER BY numeroCompetidor";
        List<Participante> participantes = new ArrayList<>();

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, carreraId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    participantes.add(mapearParticipante(rs));
                }
            }
        }
        return participantes;
    }

    public boolean updateEstado(String idParticipante, EstadoParticipante nuevoEstado) throws SQLException {
        String sql = "UPDATE Participante SET estado = ? WHERE idParticipante = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevoEstado.name());
            stmt.setString(2, idParticipante);

            return stmt.executeUpdate() > 0;
        }
    }
}