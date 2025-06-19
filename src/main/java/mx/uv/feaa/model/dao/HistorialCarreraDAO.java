package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.entidades.HistorialCarrera;
import mx.uv.feaa.util.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación concreta de {@link IGenericDAO} para la entidad {@link HistorialCarrera}.
 * Esta clase proporciona operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * para gestionar el historial de participación en carreras de caballos.
 * <p>
 * La clase maneja registros históricos de carreras, incluyendo información sobre
 * caballos, jinetes, posiciones, tiempos y ubicaciones.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 * @see IGenericDAO
 * @see HistorialCarrera
 */
public class HistorialCarreraDAO implements IGenericDAO<HistorialCarrera, String> {

    /**
     * Nombre de la tabla de HistorialCarrera en la base de datos.
     */
    private static final String TABLE_NAME = "HistorialCarrera";

    /**
     * Consulta SQL para obtener un registro por ID.
     */
    private static final String SELECT_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE idHistorial = ?";

    /**
     * Consulta SQL para obtener todos los registros.
     */
    private static final String SELECT_ALL = "SELECT * FROM " + TABLE_NAME;

    /**
     * Consulta SQL para insertar un nuevo registro.
     */
    private static final String INSERT = "INSERT INTO " + TABLE_NAME +
            "(idHistorial, carrera_id, caballo_id, jinete_id, posicion, tiempo, fecha, hipodromo) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * Consulta SQL para actualizar un registro existente.
     */
    private static final String UPDATE = "UPDATE " + TABLE_NAME + " SET " +
            "carrera_id = ?, caballo_id = ?, jinete_id = ?, posicion = ?, " +
            "tiempo = ?, fecha = ?, hipodromo = ? WHERE idHistorial = ?";

    /**
     * Consulta SQL para eliminar un registro.
     */
    private static final String DELETE = "DELETE FROM " + TABLE_NAME + " WHERE idHistorial = ?";

    /**
     * Recupera un registro específico del historial de carreras usando su ID.
     *
     * @param id el identificador único del registro histórico
     * @return un {@link Optional} que contiene el {@link HistorialCarrera} si se encuentra,
     *         o vacío si no existe un registro con ese ID
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see Optional
     * @see HistorialCarrera
     */
    @Override
    public Optional<HistorialCarrera> getById(String id) throws SQLException {
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearHistorial(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Recupera todos los registros del historial de carreras.
     *
     * @return una {@link List} de {@link HistorialCarrera} con todos los registros,
     *         o una lista vacía si no hay registros
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see List
     * @see HistorialCarrera
     */
    @Override
    public List<HistorialCarrera> getAll() throws SQLException {
        List<HistorialCarrera> historiales = new ArrayList<>();

        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL)) {

            while (rs.next()) {
                historiales.add(mapearHistorial(rs));
            }
        }
        return historiales;
    }

    /**
     * Guarda un nuevo registro en el historial de carreras.
     *
     * @param historial el objeto {@link HistorialCarrera} a persistir
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión, violación de restricciones únicas,
     *         o errores en la consulta SQL
     * @see HistorialCarrera
     */
    @Override
    public boolean save(HistorialCarrera historial) throws SQLException {
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT)) {

            prepararStatementParaInsertUpdate(stmt, historial);
            stmt.setString(1, historial.getIdHistorial());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Actualiza un registro existente en el historial de carreras.
     *
     * @param historial el objeto {@link HistorialCarrera} con los datos actualizados
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see HistorialCarrera
     */
    @Override
    public boolean update(HistorialCarrera historial) throws SQLException {
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE)) {

            prepararStatementParaInsertUpdate(stmt, historial);
            stmt.setString(8, historial.getIdHistorial());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Elimina un registro del historial de carreras.
     *
     * @param id el identificador único del registro a eliminar
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     */
    @Override
    public boolean delete(String id) throws SQLException {
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE)) {

            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Convierte un registro de la base de datos (ResultSet) en un objeto {@link HistorialCarrera}.
     * <p>
     * Maneja adecuadamente los valores nulos en campos como tiempo y fecha.
     * </p>
     *
     * @param rs el {@link ResultSet} que contiene los datos del historial
     * @return un objeto {@link HistorialCarrera} con todos los datos mapeados
     * @throws SQLException si ocurre algún error al acceder a los datos del ResultSet
     * @see ResultSet
     * @see HistorialCarrera
     */
    private HistorialCarrera mapearHistorial(ResultSet rs) throws SQLException {
        HistorialCarrera historial = new HistorialCarrera();

        historial.setIdHistorial(rs.getString("idHistorial"));
        historial.setCarreraById(rs.getString("carrera_id"));
        historial.setCaballoById(rs.getString("caballo_id"));
        historial.setJineteById(rs.getString("jinete_id"));
        historial.setPosicion(rs.getInt("posicion"));

        Time tiempo = rs.getTime("tiempo");
        if (tiempo != null) {
            historial.setTiempo(tiempo.toLocalTime());
        }

        Date fecha = rs.getDate("fecha");
        if (fecha != null) {
            historial.setFecha(fecha.toLocalDate());
        }

        historial.setHipodromo(rs.getString("hipodromo"));

        return historial;
    }

    /**
     * Configura un PreparedStatement con los datos del historial de carrera.
     * <p>
     * Maneja adecuadamente los valores nulos en campos como tiempo y fecha.
     * </p>
     *
     * @param stmt el {@link PreparedStatement} a configurar
     * @param historial el objeto {@link HistorialCarrera} con los datos
     * @throws SQLException si ocurre algún error al configurar el statement
     * @see PreparedStatement
     * @see HistorialCarrera
     */
    private void prepararStatementParaInsertUpdate(PreparedStatement stmt, HistorialCarrera historial)
            throws SQLException {

        stmt.setString(2, historial.getIdCarrera());
        stmt.setString(3, historial.getIdCaballo());
        stmt.setString(4, historial.getIdJinete());
        stmt.setInt(5, historial.getPosicion());

        if (historial.getTiempo() != null) {
            stmt.setTime(6, Time.valueOf(historial.getTiempo()));
        } else {
            stmt.setNull(6, Types.TIME);
        }

        if (historial.getFecha() != null) {
            stmt.setDate(7, Date.valueOf(historial.getFecha()));
        } else {
            stmt.setNull(7, Types.DATE);
        }

        stmt.setString(8, historial.getHipodromo());
    }

    /**
     * Recupera todos los registros de historial asociados a un caballo específico.
     *
     * @param idCaballo el identificador único del caballo
     * @return una {@link List} de {@link HistorialCarrera} asociados al caballo,
     *         o una lista vacía si no hay registros
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see List
     * @see HistorialCarrera
     */
    public List<HistorialCarrera> getByCaballoId(String idCaballo) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE caballo_id = ?";
        List<HistorialCarrera> historiales = new ArrayList<>();

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idCaballo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    historiales.add(mapearHistorial(rs));
                }
            }
        }
        return historiales;
    }

    /**
     * Recupera todos los registros de historial asociados a un jinete específico.
     *
     * @param idJinete el identificador único del jinete
     * @return una {@link List} de {@link HistorialCarrera} asociados al jinete,
     *         o una lista vacía si no hay registros
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see List
     * @see HistorialCarrera
     */
    public List<HistorialCarrera> getByJineteId(String idJinete) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE jinete_id = ?";
        List<HistorialCarrera> historiales = new ArrayList<>();

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idJinete);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    historiales.add(mapearHistorial(rs));
                }
            }
        }
        return historiales;
    }
}