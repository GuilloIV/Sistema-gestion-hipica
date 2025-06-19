package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.entidades.ApuestaSeleccion;
import mx.uv.feaa.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación concreta de {@link IGenericDAO} para la entidad {@link ApuestaSeleccion}.
 * Esta clase proporciona operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * para gestionar las selecciones asociadas a apuestas en el sistema.
 * <p>
 * La clase maneja la relación entre apuestas y participantes, manteniendo
 * el orden de selección para cada apuesta.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 * @see IGenericDAO
 * @see ApuestaSeleccion
 */
public class ApuestaSeleccionDAO implements IGenericDAO<ApuestaSeleccion, String> {
    /**
     * Nombre de la tabla de ApuestaSeleccion en la base de datos.
     */
    private static final String TABLE = "ApuestaSeleccion";

    /**
     * Recupera una selección específica de la base de datos usando su ID.
     *
     * @param id el identificador único de la selección
     * @return un {@link Optional} que contiene la {@link ApuestaSeleccion} si se encuentra,
     *         o vacío si no existe una selección con ese ID
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see Optional
     * @see ApuestaSeleccion
     */
    @Override
    public Optional<ApuestaSeleccion> getById(String id) throws SQLException {
        String sql = "SELECT * FROM " + TABLE + " WHERE idSeleccion = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearSeleccion(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Recupera todas las selecciones de apuestas registradas en el sistema.
     *
     * @return una {@link List} de {@link ApuestaSeleccion} con todas las selecciones,
     *         o una lista vacía si no hay registros
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see List
     * @see ApuestaSeleccion
     */
    @Override
    public List<ApuestaSeleccion> getAll() throws SQLException {
        List<ApuestaSeleccion> selecciones = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE;

        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                selecciones.add(mapearSeleccion(rs));
            }
        }
        return selecciones;
    }

    /**
     * Guarda una nueva selección de apuesta en la base de datos.
     *
     * @param seleccion el objeto {@link ApuestaSeleccion} a persistir
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión, violación de restricciones únicas,
     *         o errores en la consulta SQL
     * @see ApuestaSeleccion
     */
    @Override
    public boolean save(ApuestaSeleccion seleccion) throws SQLException {
        String sql = "INSERT INTO " + TABLE + " (idSeleccion, apuesta_id, participante_id, ordenSeleccion) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, seleccion.getIdSeleccion());
            stmt.setString(2, seleccion.getApuestaId());
            stmt.setString(3, seleccion.getParticipanteId());
            stmt.setInt(4, seleccion.getOrdenSeleccion());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Actualiza los datos de una selección de apuesta existente.
     *
     * @param seleccion el objeto {@link ApuestaSeleccion} con los datos actualizados
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see ApuestaSeleccion
     */
    @Override
    public boolean update(ApuestaSeleccion seleccion) throws SQLException {
        String sql = "UPDATE " + TABLE + " SET apuesta_id = ?, participante_id = ?, ordenSeleccion = ? " +
                "WHERE idSeleccion = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, seleccion.getApuestaId());
            stmt.setString(2, seleccion.getParticipanteId());
            stmt.setInt(3, seleccion.getOrdenSeleccion());
            stmt.setString(4, seleccion.getIdSeleccion());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Elimina una selección de apuesta de la base de datos.
     *
     * @param id el identificador único de la selección a eliminar
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     */
    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM " + TABLE + " WHERE idSeleccion = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Recupera todas las selecciones asociadas a una apuesta específica.
     * Las selecciones se ordenan según el campo ordenSeleccion.
     *
     * @param apuestaId el identificador único de la apuesta
     * @return una {@link List} de {@link ApuestaSeleccion} asociadas a la apuesta,
     *         ordenadas por su orden de selección, o una lista vacía si no hay registros
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see List
     * @see ApuestaSeleccion
     */
    public List<ApuestaSeleccion> getByApuestaId(String apuestaId) throws SQLException {
        List<ApuestaSeleccion> selecciones = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE + " WHERE apuesta_id = ? ORDER BY ordenSeleccion";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, apuestaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    selecciones.add(mapearSeleccion(rs));
                }
            }
        }
        return selecciones;
    }

    /**
     * Elimina todas las selecciones asociadas a una apuesta específica.
     *
     * @param apuestaId el identificador único de la apuesta
     * @return true si la operación se completó con éxito (aunque no se hayan eliminado registros),
     *         false si ocurrió un error durante la operación
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     */
    public boolean deleteByApuestaId(String apuestaId) throws SQLException {
        String sql = "DELETE FROM " + TABLE + " WHERE apuesta_id = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, apuestaId);
            return stmt.executeUpdate() >= 0; // Devuelve true incluso si no se eliminaron registros
        }
    }

    /**
     * Convierte un registro de la base de datos (ResultSet) en un objeto {@link ApuestaSeleccion}.
     *
     * @param rs el {@link ResultSet} que contiene los datos de la selección
     * @return un objeto {@link ApuestaSeleccion} con todos los datos mapeados
     * @throws SQLException si ocurre algún error al acceder a los datos del ResultSet
     * @see ResultSet
     * @see ApuestaSeleccion
     */
    private ApuestaSeleccion mapearSeleccion(ResultSet rs) throws SQLException {
        ApuestaSeleccion seleccion = new ApuestaSeleccion();
        seleccion.setIdSeleccion(rs.getString("idSeleccion"));
        seleccion.setApuestaId(rs.getString("apuesta_id"));
        seleccion.setParticipanteId(rs.getString("participante_id"));
        seleccion.setOrdenSeleccion(rs.getInt("ordenSeleccion"));
        return seleccion;
    }
}