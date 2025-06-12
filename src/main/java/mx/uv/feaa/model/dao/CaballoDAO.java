package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.entidades.Caballo;
import mx.uv.feaa.model.entidades.Criador;
import mx.uv.feaa.enumeracion.SexoCaballo;
import mx.uv.feaa.util.ConexionBD;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CaballoDAO implements IGenericDAO<Caballo, String> {
    private static final String TABLE = "Caballo";
    private static final String[] COLUMNS = {
            "idCaballo", "nombre", "fechaNacimiento", "sexo",
            "peso", "pedigri", "ultimaCarrera", "criador_id"
    };

    @Override
    public Optional<Caballo> getById(String id) throws SQLException {
        final String SQL = "SELECT * FROM " + TABLE + " WHERE idCaballo = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
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
        final String SQL = "SELECT * FROM " + TABLE;

        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {

            while (rs.next()) {
                caballos.add(mapearCaballo(rs));
            }
        }
        return caballos;
    }

    @Override
    public boolean save(Caballo caballo) throws SQLException {
        final String SQL = "INSERT INTO " + TABLE + " (" +
                String.join(", ", COLUMNS) + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, caballo.getIdCaballo());
            pstmt.setString(2, caballo.getNombre());
            pstmt.setDate(3, Date.valueOf(caballo.getFechaNacimiento()));
            pstmt.setString(4, caballo.getSexo().name());
            pstmt.setDouble(5, caballo.getPeso());
            pstmt.setString(6, caballo.getPedigri());

            LocalDate ultimaCarrera = caballo.getUltimaCarrera();
            pstmt.setDate(7, ultimaCarrera != null ? Date.valueOf(ultimaCarrera) : null);

            Criador criador = caballo.getCriador();
            pstmt.setString(8, criador != null ? criador.getIdUsuario() : null);

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Caballo caballo) throws SQLException {
        final String SQL = "UPDATE " + TABLE + " SET " +
                "nombre = ?, fechaNacimiento = ?, sexo = ?, peso = ?, " +
                "pedigri = ?, ultimaCarrera = ?, criador_id = ? " +
                "WHERE idCaballo = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, caballo.getNombre());
            pstmt.setDate(2, Date.valueOf(caballo.getFechaNacimiento()));
            pstmt.setString(3, caballo.getSexo().name());
            pstmt.setDouble(4, caballo.getPeso());
            pstmt.setString(5, caballo.getPedigri());

            LocalDate ultimaCarrera = caballo.getUltimaCarrera();
            pstmt.setDate(6, ultimaCarrera != null ? Date.valueOf(ultimaCarrera) : null);

            Criador criador = caballo.getCriador();
            pstmt.setString(7, criador != null ? criador.getIdUsuario() : null);
            pstmt.setString(8, caballo.getIdCaballo());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        final String SQL = "DELETE FROM " + TABLE + " WHERE idCaballo = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<Caballo> getByCriador(String criadorId) throws SQLException {
        List<Caballo> caballos = new ArrayList<>();
        final String SQL = "SELECT * FROM " + TABLE + " WHERE criador_id = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, criadorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    caballos.add(mapearCaballo(rs));
                }
            }
        }
        return caballos;
    }

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

        // CORRECCIÓN: ASIGNAR CRIADOR CON ID
        String criadorId = rs.getString("criador_id");
        if (criadorId != null && !criadorId.trim().isEmpty()) {
            Criador criador = new Criador();
            criador.setIdUsuario(criadorId);
            caballo.setCriador(criador);
        }

        return caballo;
    }

}