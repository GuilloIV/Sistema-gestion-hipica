package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.entidades.Caballo;
import mx.uv.feaa.enumeracion.SexoCaballo;
import mx.uv.feaa.util.ConexionBD;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CaballoDAO implements IGenericDAO<Caballo, String> {
    private static final String TABLE_NAME = "Caballo";

    @Override
    public Optional<Caballo> getById(String id) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE idCaballo = ?";

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
        String sql = "SELECT * FROM " + TABLE_NAME;

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
        String sql = "INSERT INTO " + TABLE_NAME +
                " (idCaballo, nombre, fechaNacimiento, sexo, peso, pedigri, ultimaCarrera, criador_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            prepararStatement(stmt, caballo);
            stmt.setString(1, caballo.getIdCaballo());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Caballo caballo) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET " +
                "nombre = ?, fechaNacimiento = ?, sexo = ?, peso = ?, " +
                "pedigri = ?, ultimaCarrera = ?, criador_id = ? " +
                "WHERE idCaballo = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            prepararStatement(stmt, caballo);
            stmt.setString(8, caballo.getIdCaballo());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE idCaballo = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // Métodos específicos
    public List<Caballo> getByCriador(String criadorId) throws SQLException {
        List<Caballo> caballos = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE criador_id = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, criadorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    caballos.add(mapearCaballo(rs));
                }
            }
        }
        return caballos;
    }

    public boolean actualizarUltimaCarrera(String idCaballo, LocalDate fecha) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET ultimaCarrera = ? WHERE idCaballo = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(fecha));
            stmt.setString(2, idCaballo);

            return stmt.executeUpdate() > 0;
        }
    }

    // Métodos auxiliares
    private Caballo mapearCaballo(ResultSet rs) throws SQLException {
        Caballo caballo = new Caballo();
        caballo.setIdCaballo(rs.getString("idCaballo"));
        caballo.setNombre(rs.getString("nombre"));
        caballo.setFechaNacimiento(rs.getDate("fechaNacimiento").toLocalDate());
        caballo.setSexo(SexoCaballo.valueOf(rs.getString("sexo")));
        caballo.setPeso(rs.getDouble("peso"));
        caballo.setPedigri(rs.getString("pedigri"));

        Date ultimaCarrera = rs.getDate("ultimaCarrera");
        if (ultimaCarrera != null) {
            caballo.setUltimaCarrera(ultimaCarrera.toLocalDate());
        }

        caballo.setCriadorId(rs.getString("criador_id"));

        return caballo;
    }

    private void prepararStatement(PreparedStatement stmt, Caballo caballo) throws SQLException {
        stmt.setString(1, caballo.getNombre());
        stmt.setDate(2, Date.valueOf(caballo.getFechaNacimiento()));
        stmt.setString(3, caballo.getSexo().name());
        stmt.setDouble(4, caballo.getPeso());
        stmt.setString(5, caballo.getPedigri());
        stmt.setDate(6, caballo.getUltimaCarrera() != null ?
                Date.valueOf(caballo.getUltimaCarrera()) : null);
        stmt.setString(7, caballo.getCriadorId());
    }
}