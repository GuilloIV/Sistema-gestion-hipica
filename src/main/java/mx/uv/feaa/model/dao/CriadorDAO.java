package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.entidades.Criador;
import mx.uv.feaa.model.entidades.Usuario;
import mx.uv.feaa.util.ConexionBD;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación concreta de {@link IGenericDAO} para la entidad {@link Criador}.
 * Esta clase proporciona operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * para gestionar los criadores de caballos en el sistema.
 * <p>
 * La clase maneja tanto los datos básicos de {@link Usuario} como los específicos
 * de {@link Criador}, manteniendo la integridad referencial entre ambas tablas.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 * @see IGenericDAO
 * @see Criador
 * @see Usuario
 * @see UsuarioDAO
 */
public class CriadorDAO implements IGenericDAO<Criador, String> {
    /**
     * Nombre de la tabla de Criadores en la base de datos.
     */
    private static final String TABLE_NAME = "Criador";

    /**
     * Nombre de la columna que actúa como clave primaria y foránea.
     */
    private static final String ID_COLUMN = "idUsuario";

    /**
     * Recupera un criador específico de la base de datos usando su ID.
     * Realiza un JOIN con la tabla Usuario para obtener todos los datos relacionados.
     *
     * @param id el identificador único del criador (corresponde a idUsuario)
     * @return un {@link Optional} que contiene el {@link Criador} si se encuentra,
     *         o vacío si no existe un criador con ese ID
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see Optional
     * @see Criador
     */
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

    /**
     * Recupera todos los criadores registrados en el sistema.
     * Realiza un JOIN con la tabla Usuario para obtener los datos completos.
     *
     * @return una {@link List} de {@link Criador} con todos los criadores,
     *         o una lista vacía si no hay registros
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see List
     * @see Criador
     */
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

    /**
     * Guarda un nuevo criador en la base de datos.
     * <p>
     * Este método realiza dos operaciones:
     * 1. Guarda los datos básicos en la tabla Usuario a través de {@link UsuarioDAO}
     * 2. Guarda los datos específicos del criador en la tabla Criador
     * </p>
     *
     * @param criador el objeto {@link Criador} a persistir
     * @return true si ambas operaciones se completaron con éxito, false si alguna falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión, violación de restricciones únicas,
     *         o errores en las consultas SQL
     * @see Criador
     * @see UsuarioDAO
     */
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

    /**
     * Actualiza los datos de un criador existente.
     * <p>
     * Este método realiza dos operaciones:
     * 1. Actualiza los datos básicos en la tabla Usuario a través de {@link UsuarioDAO}
     * 2. Actualiza los datos específicos del criador en la tabla Criador
     * </p>
     *
     * @param criador el objeto {@link Criador} con los datos actualizados
     * @return true si ambas operaciones se completaron con éxito, false si alguna falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en las consultas SQL
     * @see Criador
     * @see UsuarioDAO
     */
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

    /**
     * Elimina un criador de la base de datos.
     * <p>
     * Este método aprovecha la relación DELETE CASCADE configurada en la base de datos,
     * por lo que solo necesita eliminar el registro de Usuario y automáticamente
     * se eliminarán los registros relacionados en la tabla Criador.
     * </p>
     *
     * @param id el identificador único del criador a eliminar
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see UsuarioDAO
     */
    @Override
    public boolean delete(String id) throws SQLException {
        // Al tener DELETE CASCADE en la FK, solo necesitamos borrar el usuario
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        return usuarioDAO.delete(id);
    }

    /**
     * Renueva la licencia de un criador actualizando su fecha de vigencia.
     *
     * @param idCriador el identificador único del criador
     * @param nuevaFechaVigencia la nueva fecha de vigencia de la licencia
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     */
    public boolean renovarLicencia(String idCriador, LocalDate nuevaFechaVigencia) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET fechaVigenciaLicencia = ? WHERE " + ID_COLUMN + " = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(nuevaFechaVigencia));
            stmt.setString(2, idCriador);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Convierte un registro de la base de datos (ResultSet) en un objeto {@link Criador}.
     * <p>
     * Este método privado realiza el mapeo de:
     * 1. Los datos básicos de {@link Usuario}
     * 2. Los datos específicos de {@link Criador}
     * </p>
     *
     * @param rs el {@link ResultSet} que contiene los datos del criador
     * @return un objeto {@link Criador} con todos los datos mapeados
     * @throws SQLException si ocurre algún error al acceder a los datos del ResultSet
     * @see ResultSet
     * @see Criador
     * @see Usuario
     */
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