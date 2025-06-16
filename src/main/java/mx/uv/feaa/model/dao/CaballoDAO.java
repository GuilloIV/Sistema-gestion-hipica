package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.entidades.Caballo;
import mx.uv.feaa.model.entidades.Criador;
import mx.uv.feaa.util.ConexionBD;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CaballoDAO implements IGenericDAO<Caballo, String> {

    @Override
    public Optional<Caballo> getById(String id) throws SQLException {
        String sql = "SELECT c.*, cr.idUsuario as criador_id FROM Caballo c " +
                "LEFT JOIN Criador cr ON c.criador_id = cr.idUsuario " +
                "WHERE c.idCaballo = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearCaballo(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Caballo> getAll() throws SQLException {
        List<Caballo> caballos = new ArrayList<>();
        String sql = "SELECT c.*, cr.idUsuario as criador_id FROM Caballo c " +
                "LEFT JOIN Criador cr ON c.criador_id = cr.idUsuario";

        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                caballos.add(mapearCaballo(rs));
            }
        }
        return caballos;
    }

    @Override
    public boolean save(Caballo caballo) throws SQLException {
        String sql = "INSERT INTO Caballo (idCaballo, nombre, fechaNacimiento, sexo, peso, pedigri, ultimaCarrera, criador_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            configurarStatement(stmt, caballo);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Caballo caballo) throws SQLException {
        String sql = "UPDATE Caballo SET nombre = ?, fechaNacimiento = ?, sexo = ?, peso = ?, " +
                "pedigri = ?, ultimaCarrera = ?, criador_id = ? WHERE idCaballo = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, caballo.getNombre());
            stmt.setDate(2, Date.valueOf(caballo.getFechaNacimiento()));
            stmt.setString(3, caballo.getSexo().name());
            stmt.setDouble(4, caballo.getPeso());
            stmt.setString(5, caballo.getPedigri());
            stmt.setDate(6, caballo.getUltimaCarrera() != null ?
                    Date.valueOf(caballo.getUltimaCarrera()) : null);
            stmt.setString(7, caballo.getCriador() != null ?
                    caballo.getCriador().getIdUsuario() : null);
            stmt.setString(8, caballo.getIdCaballo());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM Caballo WHERE idCaballo = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public List<Caballo> getByCriador(String idCriador) throws SQLException {
        List<Caballo> caballos = new ArrayList<>();
        String sql = "SELECT c.* FROM Caballo c WHERE c.criador_id = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idCriador);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    caballos.add(mapearCaballo(rs));
                }
            }
        }
        return caballos;
    }

    public List<Caballo> getCaballosActivos() throws SQLException {
        List<Caballo> caballos = new ArrayList<>();
        String sql = "SELECT c.* FROM Caballo c WHERE c.ultimaCarrera IS NULL OR DATEDIFF(CURDATE(), c.ultimaCarrera) >= 7";

        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                caballos.add(mapearCaballo(rs));
            }
        }
        return caballos;
    }

    private Caballo mapearCaballo(ResultSet rs) throws SQLException {
        Caballo caballo = new Caballo();
        caballo.setIdCaballo(rs.getString("idCaballo"));
        caballo.setNombre(rs.getString("nombre"));
        caballo.setFechaNacimiento(rs.getDate("fechaNacimiento").toLocalDate());
        caballo.setSexo(mx.uv.feaa.enumeracion.SexoCaballo.valueOf(rs.getString("sexo")));
        caballo.setPeso(rs.getDouble("peso"));
        caballo.setPedigri(rs.getString("pedigri"));

        Date ultimaCarrera = rs.getDate("ultimaCarrera");
        if (ultimaCarrera != null) {
            caballo.setUltimaCarrera(ultimaCarrera.toLocalDate());
        }

        // Mapear criador si está en el resultado
        try {
            String criadorId = rs.getString("criador_id");
            if (criadorId != null) {
                Criador criador = new Criador();
                criador.setIdUsuario(criadorId);
                caballo.setCriador(criador);
            }
        } catch (SQLException e) {
            // Columna criador_id no existe en este ResultSet
        }

        return caballo;
    }

    private void configurarStatement(PreparedStatement stmt, Caballo caballo) throws SQLException {
        stmt.setString(1, caballo.getIdCaballo());
        stmt.setString(2, caballo.getNombre());
        stmt.setDate(3, Date.valueOf(caballo.getFechaNacimiento()));
        stmt.setString(4, caballo.getSexo().name());
        stmt.setDouble(5, caballo.getPeso());
        stmt.setString(6, caballo.getPedigri());
        stmt.setDate(7, caballo.getUltimaCarrera() != null ?
                Date.valueOf(caballo.getUltimaCarrera()) : null);
        stmt.setString(8, caballo.getCriador() != null ?
                caballo.getCriador().getIdUsuario() : null);
    }

    // Métodos adicionales específicos para Caballo

    public boolean actualizarUltimaCarrera(String idCaballo, LocalDate fechaCarrera) throws SQLException {
        String sql = "UPDATE Caballo SET ultimaCarrera = ? WHERE idCaballo = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(fechaCarrera));
            stmt.setString(2, idCaballo);

            return stmt.executeUpdate() > 0;
        }
    }

    public List<Caballo> buscarPorNombre(String nombre) throws SQLException {
        List<Caballo> caballos = new ArrayList<>();
        String sql = "SELECT c.* FROM Caballo c WHERE c.nombre LIKE ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nombre + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    caballos.add(mapearCaballo(rs));
                }
            }
        }
        return caballos;
    }
}