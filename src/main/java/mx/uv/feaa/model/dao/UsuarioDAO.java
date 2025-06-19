package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.entidades.Usuario;
import mx.uv.feaa.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación concreta de {@link IGenericDAO} para la entidad {@link Usuario}.
 * Esta clase proporciona operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * para gestionar los usuarios del sistema, incluyendo métodos específicos para
 * autenticación y gestión de acceso.
 * <p>
 * La clase maneja toda la información relacionada con usuarios, incluyendo
 * credenciales de acceso, estado de activación y tipos de usuario.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 * @see IGenericDAO
 * @see Usuario
 */
public class UsuarioDAO implements IGenericDAO<Usuario, String> {
    /**
     * Nombre de la tabla de Usuarios en la base de datos.
     */
    private static final String TABLE_NAME = "Usuario";

    /**
     * Nombre de la columna que actúa como clave primaria.
     */
    private static final String ID_COLUMN = "idUsuario";

    /**
     * Recupera un usuario específico de la base de datos usando su ID.
     *
     * @param id el identificador único del usuario
     * @return un {@link Optional} que contiene el {@link Usuario} si se encuentra,
     *         o vacío si no existe un usuario con ese ID
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see Optional
     * @see Usuario
     */
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

    /**
     * Recupera todos los usuarios registrados en el sistema.
     *
     * @return una {@link List} de {@link Usuario} con todos los usuarios,
     *         o una lista vacía si no hay registros
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see List
     * @see Usuario
     */
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

    /**
     * Guarda un nuevo usuario en la base de datos.
     *
     * @param usuario el objeto {@link Usuario} a persistir
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión, violación de restricciones únicas,
     *         o errores en la consulta SQL
     * @see Usuario
     */
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

    /**
     * Actualiza los datos de un usuario existente.
     *
     * @param usuario el objeto {@link Usuario} con los datos actualizados
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see Usuario
     */
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

    /**
     * Elimina un usuario de la base de datos.
     *
     * @param id el identificador único del usuario a eliminar
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     */
    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE " + ID_COLUMN + " = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param nombreUsuario el nombre de usuario a buscar
     * @return un {@link Optional} que contiene el {@link Usuario} si se encuentra,
     *         o vacío si no existe un usuario con ese nombre
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see Optional
     * @see Usuario
     */
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

    /**
     * Convierte un registro de la base de datos (ResultSet) en un objeto {@link Usuario}.
     *
     * @param rs el {@link ResultSet} que contiene los datos del usuario
     * @return un objeto {@link Usuario} con todos los datos mapeados
     * @throws SQLException si ocurre algún error al acceder a los datos del ResultSet
     * @see ResultSet
     * @see Usuario
     */
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
     * Valida las credenciales de un usuario sin retornar datos sensibles.
     * Compara la contraseña hasheada con SHA-256 almacenada en la base de datos.
     *
     * @param nombreUsuario el nombre de usuario
     * @param password la contraseña en texto plano
     * @return true si las credenciales son válidas y el usuario está activo, false en caso contrario
     * @throws SQLException si ocurre un error en la base de datos
     * @throws IllegalArgumentException si nombreUsuario o password son nulos o vacíos
     */
    public boolean validarCredenciales(String nombreUsuario, String password) throws SQLException {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre de usuario y contraseña no pueden estar vacíos");
        }

        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME +
                " WHERE nombreUsuario = ? AND password = SHA2(?, 256) AND activo = true";

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
     * Actualiza la fecha y hora del último acceso del usuario.
     *
     * @param idUsuario el ID del usuario
     * @return true si se actualizó correctamente, false en caso contrario
     * @throws SQLException si ocurre un error en la base de datos
     * @throws IllegalArgumentException si idUsuario es nulo o vacío
     */
    public boolean actualizarUltimoAcceso(String idUsuario) throws SQLException {
        if (idUsuario == null || idUsuario.trim().isEmpty()) {
            throw new IllegalArgumentException("ID de usuario no puede estar vacío");
        }

        String sql = "UPDATE " + TABLE_NAME +
                " SET ultimoAcceso = CURRENT_TIMESTAMP WHERE " + ID_COLUMN + " = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idUsuario);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Verifica si un usuario existe y está activo en el sistema.
     *
     * @param nombreUsuario el nombre de usuario a verificar
     * @return true si existe y está activo, false en caso contrario
     * @throws SQLException si ocurre un error en la base de datos
     * @throws IllegalArgumentException si nombreUsuario es nulo o vacío
     */
    public boolean existeUsuarioActivo(String nombreUsuario) throws SQLException {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre de usuario no puede estar vacío");
        }

        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME +
                " WHERE nombreUsuario = ? AND activo = true";

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
     * Obtiene el tipo de usuario por nombre de usuario.
     *
     * @param nombreUsuario el nombre de usuario
     * @return el tipo de usuario o null si no existe o no está activo
     * @throws SQLException si ocurre un error en la base de datos
     * @throws IllegalArgumentException si nombreUsuario es nulo o vacío
     */
    public String obtenerTipoUsuario(String nombreUsuario) throws SQLException {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre de usuario no puede estar vacío");
        }

        String sql = "SELECT tipoUsuario FROM " + TABLE_NAME +
                " WHERE nombreUsuario = ? AND activo = true";

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