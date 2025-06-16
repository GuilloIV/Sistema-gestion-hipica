package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.entidades.Criador;
import mx.uv.feaa.model.entidades.Usuario;
import mx.uv.feaa.util.ConexionBD;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CriadorDAO implements IGenericDAO<Criador, String> {
    private static final String TABLE_NAME = "Criador";
    private static final String ID_COLUMN = "idUsuario";

    @Override
    public Optional<Criador> getById(String id) throws SQLException {
        String sql = "SELECT u.*, c.* FROM " + TABLE_NAME + " c " +
                "JOIN Usuario u ON c." + ID_COLUMN + " = u." + ID_COLUMN + " " +
                "WHERE c." + ID_COLUMN + " = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearCriador(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Criador> getAll() throws SQLException {
        List<Criador> criadores = new ArrayList<>();
        String sql = "SELECT u.*, c.* FROM " + TABLE_NAME + " c " +
                "JOIN Usuario u ON c." + ID_COLUMN + " = u." + ID_COLUMN;

        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                criadores.add(mapearCriador(rs));
            }
        }
        return criadores;
    }

    @Override
    public boolean save(Criador criador) throws SQLException {
        // Primero guardamos el usuario base
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        if (!usuarioDAO.save(criador)) {
            return false;
        }

        // Luego guardamos los datos específicos del criador
        String sql = "INSERT INTO " + TABLE_NAME + " (idUsuario, licenciaCriador, fechaVigenciaLicencia, " +
                "direccion, telefono, nombreHaras) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, criador.getIdUsuario());
            stmt.setString(2, criador.getLicenciaCriador());
            stmt.setDate(3, Date.valueOf(criador.getFechaVigenciaLicencia()));
            stmt.setString(4, criador.getDireccion());
            stmt.setString(5, criador.getTelefono());
            stmt.setString(6, criador.getNombreHaras());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Criador criador) throws SQLException {
        // Actualizamos primero el usuario base
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        if (!usuarioDAO.update(criador)) {
            return false;
        }

        // Luego actualizamos los datos específicos del criador
        String sql = "UPDATE " + TABLE_NAME + " SET licenciaCriador = ?, fechaVigenciaLicencia = ?, " +
                "direccion = ?, telefono = ?, nombreHaras = ? WHERE " + ID_COLUMN + " = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, criador.getLicenciaCriador());
            stmt.setDate(2, Date.valueOf(criador.getFechaVigenciaLicencia()));
            stmt.setString(3, criador.getDireccion());
            stmt.setString(4, criador.getTelefono());
            stmt.setString(5, criador.getNombreHaras());
            stmt.setString(6, criador.getIdUsuario());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        // Al tener DELETE CASCADE en la FK, solo necesitamos borrar el usuario
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        return usuarioDAO.delete(id);
    }

    public boolean renovarLicencia(String idCriador, LocalDate nuevaFechaVigencia) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET fechaVigenciaLicencia = ? WHERE " + ID_COLUMN + " = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(nuevaFechaVigencia));
            stmt.setString(2, idCriador);

            return stmt.executeUpdate() > 0;
        }
    }

    private Criador mapearCriador(ResultSet rs) throws SQLException {
        // Mapear datos de Usuario
        Usuario usuario = new Usuario(
                rs.getString(ID_COLUMN),
                rs.getString("nombreUsuario"),
                rs.getString("email"),
                rs.getString("password")
        ) {
            @Override
            public String getTipoUsuarioEspecifico() {
                return "";
            }
        };

        usuario.setActivo(rs.getBoolean("activo"));
        usuario.setFechaRegistro(rs.getTimestamp("fechaRegistro").toLocalDateTime());

        if (rs.getTimestamp("ultimoAcceso") != null) {
            usuario.setUltimoAcceso(rs.getTimestamp("ultimoAcceso").toLocalDateTime());
        }

        // Mapear datos específicos de Criador
        Criador criador = new Criador();
        criador.setIdUsuario(usuario.getIdUsuario());
        criador.setNombreUsuario(usuario.getNombreUsuario());
        criador.setEmail(usuario.getEmail());
        criador.setPassword(usuario.getPassword());
        criador.setActivo(usuario.isActivo());
        criador.setFechaRegistro(usuario.getFechaRegistro());
        criador.setUltimoAcceso(usuario.getUltimoAcceso());

        criador.setLicenciaCriador(rs.getString("licenciaCriador"));
        criador.setFechaVigenciaLicencia(rs.getDate("fechaVigenciaLicencia").toLocalDate());
        criador.setDireccion(rs.getString("direccion"));
        criador.setTelefono(rs.getString("telefono"));
        criador.setNombreHaras(rs.getString("nombreHaras"));

        return criador;
    }
}