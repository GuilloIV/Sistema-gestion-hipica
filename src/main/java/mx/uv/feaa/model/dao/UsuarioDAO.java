package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.entidades.Usuario;
import mx.uv.feaa.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDAO implements IGenericDAO<Usuario, String> {
    private static final String TABLE_NAME = "Usuario";
    private static final String ID_COLUMN = "idUsuario";

    @Override
    public Optional<Usuario> getById(String id) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + ID_COLUMN + " = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
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
        String sql = "SELECT * FROM " + TABLE_NAME;

        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        }
        return usuarios;
    }

    @Override
    public boolean save(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME + " (idUsuario, nombreUsuario, email, password, activo, tipoUsuario) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getIdUsuario());
            stmt.setString(2, usuario.getNombreUsuario());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getPassword());
            stmt.setBoolean(5, usuario.isActivo());
            stmt.setString(6, usuario.getTipoUsuario());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Usuario usuario) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET nombreUsuario = ?, email = ?, password = ?, activo = ? " +
                "WHERE " + ID_COLUMN + " = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNombreUsuario());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getPassword());
            stmt.setBoolean(4, usuario.isActivo());
            stmt.setString(5, usuario.getIdUsuario());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE " + ID_COLUMN + " = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE nombreUsuario = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombreUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
            }
        }
        return Optional.empty();
    }
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
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

        usuario.setTipoUsuario(rs.getString("tipoUsuario"));
        return usuario;
    }


    /**
     * Valida las credenciales de un usuario sin retornar datos sensibles
     * @param nombreUsuario el nombre de usuario
     * @param password la contraseña en texto plano
     * @return true si las credenciales son válidas, false en caso contrario
     * @throws SQLException si ocurre un error en la base de datos
     */
    public boolean validarCredenciales(String nombreUsuario, String password) throws SQLException {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            return false;
        }

        // Comparamos directamente en la consulta usando SHA2 para hashear la contraseña ingresada
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE nombreUsuario = ? AND password = SHA2(?, 256) AND activo = true";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombreUsuario.trim());
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Actualiza la fecha y hora del último acceso del usuario
     * @param idUsuario el ID del usuario
     * @return true si se actualizó correctamente, false en caso contrario
     * @throws SQLException si ocurre un error en la base de datos
     */
    public boolean actualizarUltimoAcceso(String idUsuario) throws SQLException {
        if (idUsuario == null || idUsuario.trim().isEmpty()) {
            return false;
        }

        String sql = "UPDATE " + TABLE_NAME + " SET ultimoAcceso = CURRENT_TIMESTAMP WHERE " + ID_COLUMN + " = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idUsuario);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Verifica si un usuario existe y está activo
     * @param nombreUsuario el nombre de usuario a verificar
     * @return true si existe y está activo, false en caso contrario
     * @throws SQLException si ocurre un error en la base de datos
     */
    public boolean existeUsuarioActivo(String nombreUsuario) throws SQLException {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE nombreUsuario = ? AND activo = true";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombreUsuario.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Obtiene el tipo de usuario por nombre de usuario
     * @param nombreUsuario el nombre de usuario
     * @return el tipo de usuario o null si no existe
     * @throws SQLException si ocurre un error en la base de datos
     */
    public String obtenerTipoUsuario(String nombreUsuario) throws SQLException {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT tipoUsuario FROM " + TABLE_NAME + " WHERE nombreUsuario = ? AND activo = true";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombreUsuario.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("tipoUsuario");
                }
            }
        }
        return null;
    }

}