package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.entidades.Apostador;
import mx.uv.feaa.model.entidades.Usuario;
import mx.uv.feaa.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación concreta de {@link IGenericDAO} para la entidad {@link Apostador}.
 * Esta clase proporciona operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * para gestionar los apostadores en el sistema, interactuando con la base de datos.
 * <p>
 * La clase maneja tanto los datos básicos de {@link Usuario} como los específicos
 * de {@link Apostador}, manteniendo la integridad referencial entre ambas tablas.
 * </p>
 *
 * @author [Nombre del autor o equipo]
 * @version 1.0
 * @since 1.0
 * @see IGenericDAO
 * @see Apostador
 * @see Usuario
 * @see UsuarioDAO
 */
public class ApostadorDAO implements IGenericDAO<Apostador, String> {
    /**
     * Nombre de la tabla de Apostadores en la base de datos.
     */
    private static final String TABLE_NAME = "Apostador";

    /**
     * Nombre de la columna que actúa como clave primaria.
     */
    private static final String ID_COLUMN = "idUsuario";

    /**
     * Recupera un apostador específico de la base de datos usando su ID.
     * Realiza un JOIN con la tabla Usuario para obtener todos los datos relacionados.
     *
     * @param id el identificador único del apostador (corresponde a idUsuario)
     * @return un {@link Optional} que contiene el {@link Apostador} si se encuentra,
     *         o vacío si no existe un apostador con ese ID
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see Optional
     * @see Apostador
     */
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

    /**
     * Recupera todos los apostadores registrados en el sistema.
     * Realiza un JOIN con la tabla Usuario para obtener los datos completos.
     *
     * @return una {@link List} de {@link Apostador} con todos los apostadores,
     *         o una lista vacía si no hay registros
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see List
     * @see Apostador
     */
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

    /**
     * Guarda un nuevo apostador en la base de datos.
     * <p>
     * Este método realiza dos operaciones:
     * 1. Guarda los datos básicos en la tabla Usuario a través de {@link UsuarioDAO}
     * 2. Guarda los datos específicos del apostador en la tabla Apostador
     * </p>
     *
     * @param apostador el objeto {@link Apostador} a persistir en la base de datos
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión, violación de restricciones únicas,
     *         o errores en la consulta SQL
     * @see Apostador
     * @see UsuarioDAO
     */
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

    /**
     * Actualiza los datos de un apostador existente.
     * <p>
     * Este método realiza dos operaciones:
     * 1. Actualiza los datos básicos en la tabla Usuario a través de {@link UsuarioDAO}
     * 2. Actualiza los datos específicos del apostador en la tabla Apostador
     * </p>
     *
     * @param apostador el objeto {@link Apostador} con los datos actualizados
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see Apostador
     * @see UsuarioDAO
     */
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

    /**
     * Elimina un apostador de la base de datos.
     * <p>
     * Este método aprovecha la relación DELETE CASCADE configurada en la base de datos,
     * por lo que solo necesita eliminar el registro de Usuario y automáticamente
     * se eliminarán los registros relacionados en la tabla Apostador.
     * </p>
     *
     * @param id el identificador único del apostador a eliminar
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
     * Actualiza el saldo de un apostador específico.
     *
     * @param idUsuario el identificador único del apostador
     * @param nuevoSaldo el nuevo valor del saldo a establecer
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     */
    public boolean actualizarSaldo(String idUsuario, double nuevoSaldo) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET saldo = ? WHERE " + ID_COLUMN + " = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, nuevoSaldo);
            stmt.setString(2, idUsuario);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Convierte un registro de la base de datos (ResultSet) en un objeto {@link Apostador}.
     * <p>
     * Este método privado realiza el mapeo de:
     * 1. Los datos básicos de {@link Usuario}
     * 2. Los datos específicos de {@link Apostador}
     * </p>
     *
     * @param rs el {@link ResultSet} que contiene los datos del apostador
     * @return un objeto {@link Apostador} con todos los datos mapeados
     * @throws SQLException si ocurre algún error al acceder a los datos del ResultSet
     * @see ResultSet
     * @see Apostador
     * @see Usuario
     */
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