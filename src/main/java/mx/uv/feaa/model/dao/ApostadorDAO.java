package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.entidades.Apostador;
import mx.uv.feaa.model.entidades.Usuario;
import mx.uv.feaa.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ApostadorDAO implements IGenericDAO<Apostador, String> {
    private static final String TABLE_NAME = "Apostador";
    private static final String ID_COLUMN = "idUsuario";

    @Override
    public Optional<Apostador> getById(String id) throws SQLException {
        String sql = "SELECT u.*, a.* FROM " + TABLE_NAME + " a " +
                "JOIN Usuario u ON a." + ID_COLUMN + " = u." + ID_COLUMN + " " +
                "WHERE a." + ID_COLUMN + " = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearApostador(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Apostador> getAll() throws SQLException {
        List<Apostador> apostadores = new ArrayList<>();
        String sql = "SELECT u.*, a.* FROM " + TABLE_NAME + " a " +
                "JOIN Usuario u ON a." + ID_COLUMN + " = u." + ID_COLUMN;

        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                apostadores.add(mapearApostador(rs));
            }
        }
        return apostadores;
    }

    @Override
    public boolean save(Apostador apostador) throws SQLException {
        // Primero guardamos el usuario base
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        if (!usuarioDAO.save(apostador)) {
            return false;
        }

        // Luego guardamos los datos específicos del apostador
        String sql = "INSERT INTO " + TABLE_NAME + " (idUsuario, saldo, limiteApuesta, nombre, telefono) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, apostador.getIdUsuario());
            stmt.setDouble(2, apostador.getSaldo());
            stmt.setDouble(3, apostador.getLimiteApuesta());
            stmt.setString(4, apostador.getNombre());
            stmt.setString(5, apostador.getTelefono());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Apostador apostador) throws SQLException {
        // Actualizamos primero el usuario base
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        if (!usuarioDAO.update(apostador)) {
            return false;
        }

        // Luego actualizamos los datos específicos del apostador
        String sql = "UPDATE " + TABLE_NAME + " SET saldo = ?, limiteApuesta = ?, nombre = ?, telefono = ? " +
                "WHERE " + ID_COLUMN + " = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, apostador.getSaldo());
            stmt.setDouble(2, apostador.getLimiteApuesta());
            stmt.setString(3, apostador.getNombre());
            stmt.setString(4, apostador.getTelefono());
            stmt.setString(5, apostador.getIdUsuario());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        // Al tener DELETE CASCADE en la FK, solo necesitamos borrar el usuario
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        return usuarioDAO.delete(id);
    }

    public boolean actualizarSaldo(String idUsuario, double nuevoSaldo) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET saldo = ? WHERE " + ID_COLUMN + " = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, nuevoSaldo);
            stmt.setString(2, idUsuario);

            return stmt.executeUpdate() > 0;
        }
    }

    private Apostador mapearApostador(ResultSet rs) throws SQLException {
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

        // Mapear datos específicos de Apostador
        Apostador apostador = new Apostador();
        apostador.setIdUsuario(usuario.getIdUsuario());
        apostador.setNombreUsuario(usuario.getNombreUsuario());
        apostador.setEmail(usuario.getEmail());
        apostador.setPassword(usuario.getPassword());
        apostador.setActivo(usuario.isActivo());
        apostador.setFechaRegistro(usuario.getFechaRegistro());
        apostador.setUltimoAcceso(usuario.getUltimoAcceso());

        apostador.setSaldo(rs.getDouble("saldo"));
        apostador.setLimiteApuesta(rs.getDouble("limiteApuesta"));
        apostador.setNombre(rs.getString("nombre"));
        apostador.setTelefono(rs.getString("telefono"));

        return apostador;
    }
}