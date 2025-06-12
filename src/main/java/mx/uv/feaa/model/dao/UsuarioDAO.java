package mx.uv.feaa.model.dao;


import mx.uv.feaa.model.entidades.Usuario;
import mx.uv.feaa.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDAO implements IGenericDAO<Usuario, String> {
    private static final String TABLE = "Usuario";
    private static final String[] COLUMNS = {
            "idUsuario", "nombreUsuario", "email", "password",
            "activo", "fechaRegistro", "ultimoAcceso", "tipoUsuario"
    };

    @Override
    public Optional<Usuario> getById(String id) throws SQLException {
        final String SQL = "SELECT * FROM " + TABLE + " WHERE idUsuario = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Usuario> getAll() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        final String SQL = "SELECT * FROM " + TABLE;

        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {

            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        }
        return usuarios;
    }

    @Override
    public boolean save(Usuario usuario) throws SQLException {
        final String SQL = "INSERT INTO " + TABLE + " (" +
                String.join(", ", COLUMNS) + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, usuario.getIdUsuario());
            pstmt.setString(2, usuario.getNombreUsuario());
            pstmt.setString(3, usuario.getEmail());
            pstmt.setString(4, usuario.getPassword());
            pstmt.setBoolean(5, usuario.isActivo());
            pstmt.setTimestamp(6, Timestamp.valueOf(usuario.getFechaRegistro()));

            Timestamp ultimoAcceso = usuario.getUltimoAcceso() != null ?
                    Timestamp.valueOf(usuario.getUltimoAcceso()) : null;
            pstmt.setTimestamp(7, ultimoAcceso);

            pstmt.setString(8, usuario.getTipoUsuario());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Usuario usuario) throws SQLException {
        final String SQL = "UPDATE " + TABLE + " SET " +
                "nombreUsuario = ?, email = ?, password = ?, activo = ?, " +
                "ultimoAcceso = ?, tipoUsuario = ? WHERE idUsuario = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, usuario.getNombreUsuario());
            pstmt.setString(2, usuario.getEmail());
            pstmt.setString(3, usuario.getPassword());
            pstmt.setBoolean(4, usuario.isActivo());

            Timestamp ultimoAcceso = usuario.getUltimoAcceso() != null ?
                    Timestamp.valueOf(usuario.getUltimoAcceso()) : null;
            pstmt.setTimestamp(5, ultimoAcceso);

            pstmt.setString(6, usuario.getTipoUsuario());
            pstmt.setString(7, usuario.getIdUsuario());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        final String SQL = "DELETE FROM " + TABLE + " WHERE idUsuario = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    public Optional<Usuario> getByUsername(String username) throws SQLException {
        final String SQL = "SELECT * FROM " + TABLE + " WHERE nombreUsuario = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
            }
        }
        return Optional.empty();
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario() {
            @Override
            public String getTipoUsuarioEspecifico() {
                try {
                    return rs.getString("tipoUsuario");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        usuario.setIdUsuario(rs.getString("idUsuario"));
        usuario.setNombreUsuario(rs.getString("nombreUsuario"));
        usuario.setEmail(rs.getString("email"));
        usuario.setPassword(rs.getString("password"));
        usuario.setActivo(rs.getBoolean("activo"));
        usuario.setFechaRegistro(rs.getTimestamp("fechaRegistro").toLocalDateTime());

        Timestamp ultimoAcceso = rs.getTimestamp("ultimoAcceso");
        if (ultimoAcceso != null) {
            usuario.setUltimoAcceso(ultimoAcceso.toLocalDateTime());
        }

        usuario.setTipoUsuario(rs.getString("tipoUsuario"));
        return usuario;
    }
}