package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.entidades.Apuesta;
import mx.uv.feaa.model.entidades.ApuestaGanador;
import mx.uv.feaa.enumeracion.EstadoApuesta;
import mx.uv.feaa.enumeracion.TipoApuesta;
import mx.uv.feaa.model.entidades.ApuestaSeleccion;
import mx.uv.feaa.util.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación concreta de {@link IGenericDAO} para la entidad {@link Apuesta}.
 * Esta clase proporciona operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * para gestionar las apuestas en el sistema, incluyendo operaciones específicas
 * para apuestas con selecciones.
 * <p>
 * La clase maneja tanto los datos básicos de {@link Apuesta} como las relaciones
 * con {@link ApuestaSeleccion} cuando corresponda, asegurando la integridad transaccional.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 * @see IGenericDAO
 * @see Apuesta
 * @see ApuestaGanador
 * @see ApuestaSeleccion
 * @see EstadoApuesta
 * @see TipoApuesta
 */
public class ApuestaDAO implements IGenericDAO<Apuesta, String> {
    /**
     * Nombre de la tabla de Apuestas en la base de datos.
     */
    private static final String TABLE_NAME = "Apuesta";

    /**
     * Consulta SQL para obtener una apuesta por su ID.
     */
    private static final String SELECT_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE idApuesta = ?";

    /**
     * Consulta SQL para obtener todas las apuestas.
     */
    private static final String SELECT_ALL = "SELECT * FROM " + TABLE_NAME;

    /**
     * Consulta SQL para insertar una nueva apuesta.
     */
    private static final String INSERT = "INSERT INTO " + TABLE_NAME +
            "(idApuesta, apostador_id, carrera_id, tipoApuesta, montoApostado, fechaApuesta, estado, cuotaAplicada, montoGanado) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * Consulta SQL para actualizar una apuesta existente.
     */
    private static final String UPDATE = "UPDATE " + TABLE_NAME + " SET " +
            "apostador_id = ?, carrera_id = ?, tipoApuesta = ?, montoApostado = ?, fechaApuesta = ?, " +
            "estado = ?, cuotaAplicada = ?, montoGanado = ? WHERE idApuesta = ?";

    /**
     * Consulta SQL para eliminar una apuesta.
     */
    private static final String DELETE = "DELETE FROM " + TABLE_NAME + " WHERE idApuesta = ?";

    /**
     * Recupera una apuesta específica de la base de datos usando su ID.
     *
     * @param id el identificador único de la apuesta
     * @return un {@link Optional} que contiene la {@link Apuesta} si se encuentra,
     *         o vacío si no existe una apuesta con ese ID
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see Optional
     * @see Apuesta
     */
    @Override
    public Optional<Apuesta> getById(String id) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Apuesta apuesta = null;

        try {
            conn = ConexionBD.getConnection();
            stmt = conn.prepareStatement(SELECT_BY_ID);
            stmt.setString(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                apuesta = mapearApuesta(rs);
            }
        } finally {
            ConexionBD.cerrar(conn, stmt, rs);
        }

        return Optional.ofNullable(apuesta);
    }

    /**
     * Recupera todas las apuestas registradas en el sistema.
     *
     * @return una {@link List} de {@link Apuesta} con todas las apuestas,
     *         o una lista vacía si no hay registros
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see List
     * @see Apuesta
     */
    @Override
    public List<Apuesta> getAll() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Apuesta> apuestas = new ArrayList<>();

        try {
            conn = ConexionBD.getConnection();
            stmt = conn.prepareStatement(SELECT_ALL);
            rs = stmt.executeQuery();

            while (rs.next()) {
                apuestas.add(mapearApuesta(rs));
            }
        } finally {
            ConexionBD.cerrar(conn, stmt, rs);
        }

        return apuestas;
    }

    /**
     * Guarda una nueva apuesta en la base de datos.
     *
     * @param entity el objeto {@link Apuesta} a persistir
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión, violación de restricciones únicas,
     *         o errores en la consulta SQL
     * @see Apuesta
     */
    @Override
    public boolean save(Apuesta entity) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean saved = false;

        try {
            conn = ConexionBD.getConnection();
            stmt = conn.prepareStatement(INSERT);

            stmt.setString(1, entity.getId());
            stmt.setString(2, entity.getIdUsuario());
            stmt.setString(3, entity.getIdCarrera());
            stmt.setString(4, entity.getTipoApuesta().name());
            stmt.setDouble(5, entity.getMontoApostado());
            stmt.setTimestamp(6, Timestamp.valueOf(entity.getFechaApuesta()));
            stmt.setString(7, entity.getEstado().name());
            stmt.setDouble(8, entity.getCuotaAplicada());
            stmt.setDouble(9, entity.getMontoGanado());

            saved = stmt.executeUpdate() > 0;
        } finally {
            ConexionBD.cerrar(conn, stmt);
        }

        return saved;
    }

    /**
     * Actualiza los datos de una apuesta existente.
     *
     * @param entity el objeto {@link Apuesta} con los datos actualizados
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see Apuesta
     */
    @Override
    public boolean update(Apuesta entity) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean updated = false;

        try {
            conn = ConexionBD.getConnection();
            stmt = conn.prepareStatement(UPDATE);

            stmt.setString(1, entity.getIdUsuario());
            stmt.setString(2, entity.getIdCarrera());
            stmt.setString(3, entity.getTipoApuesta().name());
            stmt.setDouble(4, entity.getMontoApostado());
            stmt.setTimestamp(5, Timestamp.valueOf(entity.getFechaApuesta()));
            stmt.setString(6, entity.getEstado().name());
            stmt.setDouble(7, entity.getCuotaAplicada());
            stmt.setDouble(8, entity.getMontoGanado());
            stmt.setString(9, entity.getId());

            updated = stmt.executeUpdate() > 0;
        } finally {
            ConexionBD.cerrar(conn, stmt);
        }

        return updated;
    }

    /**
     * Elimina una apuesta de la base de datos.
     *
     * @param id el identificador único de la apuesta a eliminar
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     */
    @Override
    public boolean delete(String id) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean deleted = false;

        try {
            conn = ConexionBD.getConnection();
            stmt = conn.prepareStatement(DELETE);
            stmt.setString(1, id);

            deleted = stmt.executeUpdate() > 0;
        } finally {
            ConexionBD.cerrar(conn, stmt);
        }

        return deleted;
    }

    /**
     * Convierte un registro de la base de datos (ResultSet) en un objeto {@link Apuesta}.
     * <p>
     * Actualmente mapea como {@link ApuestaGanador} por defecto.
     * </p>
     *
     * @param rs el {@link ResultSet} que contiene los datos de la apuesta
     * @return un objeto {@link Apuesta} con todos los datos mapeados
     * @throws SQLException si ocurre algún error al acceder a los datos del ResultSet
     * @see ResultSet
     * @see Apuesta
     * @see ApuestaGanador
     */
    private Apuesta mapearApuesta(ResultSet rs) throws SQLException {
        Apuesta apuesta = new ApuestaGanador(
                rs.getString("idApuesta"),
                rs.getString("apostador_id"),
                rs.getString("carrera_id"),
                TipoApuesta.valueOf(rs.getString("tipoApuesta")),
                rs.getDouble("montoApostado")
        );

        apuesta.setFechaApuesta(rs.getTimestamp("fechaApuesta").toLocalDateTime());
        apuesta.setEstado(EstadoApuesta.valueOf(rs.getString("estado")));
        apuesta.setCuotaAplicada(rs.getDouble("cuotaAplicada"));
        apuesta.setMontoGanado(rs.getDouble("montoGanado"));

        return apuesta;
    }

    /**
     * Recupera todas las apuestas realizadas por un apostador específico.
     *
     * @param apostadorId el identificador único del apostador
     * @return una {@link List} de {@link Apuesta} asociadas al apostador,
     *         o una lista vacía si no hay registros
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see List
     * @see Apuesta
     */
    public List<Apuesta> getByApostadorId(String apostadorId) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Apuesta> apuestas = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE apostador_id = ?";

        try {
            conn = ConexionBD.getConnection();
            stmt = conn.prepareStatement(query);
            stmt.setString(1, apostadorId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                apuestas.add(mapearApuesta(rs));
            }
        } finally {
            ConexionBD.cerrar(conn, stmt, rs);
        }

        return apuestas;
    }

    /**
     * Recupera todas las apuestas con un estado específico.
     *
     * @param estado el {@link EstadoApuesta} por el cual filtrar
     * @return una {@link List} de {@link Apuesta} con el estado especificado,
     *         o una lista vacía si no hay registros
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see List
     * @see Apuesta
     * @see EstadoApuesta
     */
    public List<Apuesta> getByEstado(EstadoApuesta estado) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Apuesta> apuestas = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE estado = ?";

        try {
            conn = ConexionBD.getConnection();
            stmt = conn.prepareStatement(query);
            stmt.setString(1, estado.name());
            rs = stmt.executeQuery();

            while (rs.next()) {
                apuestas.add(mapearApuesta(rs));
            }
        } finally {
            ConexionBD.cerrar(conn, stmt, rs);
        }

        return apuestas;
    }

    /**
     * Guarda una apuesta junto con sus selecciones asociadas en una transacción.
     * <p>
     * Este método asegura que:
     * 1. Se guarde la apuesta principal
     * 2. Se guarden todas las selecciones asociadas
     * </p>
     * Si cualquier operación falla, se realiza rollback de toda la transacción.
     *
     * @param apuesta la {@link Apuesta} principal a guardar
     * @param selecciones la {@link List} de {@link ApuestaSeleccion} asociadas
     * @return true si todas las operaciones se completaron con éxito, false si alguna falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en las consultas SQL
     * @see Apuesta
     * @see ApuestaSeleccion
     * @see ApuestaSeleccionDAO
     */
    public boolean saveWithSelections(Apuesta apuesta, List<ApuestaSeleccion> selecciones) throws SQLException {
        Connection conn = null;
        try {
            conn = ConexionBD.getConnection();
            conn.setAutoCommit(false);  // Iniciar transacción

            // 1. Guardar la apuesta principal
            if (!save(apuesta)) {
                conn.rollback();
                return false;
            }

            // 2. Guardar las selecciones
            ApuestaSeleccionDAO seleccionDAO = new ApuestaSeleccionDAO();
            for (ApuestaSeleccion seleccion : selecciones) {
                if (!seleccionDAO.save(seleccion)) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);  // Restaurar auto-commit
                conn.close();
            }
        }
    }

    /**
     * Actualiza una apuesta junto con sus selecciones asociadas en una transacción.
     * <p>
     * Este método asegura que:
     * 1. Se actualice la apuesta principal
     * 2. Se eliminen las selecciones antiguas
     * 3. Se guarden las nuevas selecciones
     * </p>
     * Si cualquier operación falla, se realiza rollback de toda la transacción.
     *
     * @param apuesta la {@link Apuesta} principal a actualizar
     * @param selecciones la nueva {@link List} de {@link ApuestaSeleccion} asociadas
     * @return true si todas las operaciones se completaron con éxito, false si alguna falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en las consultas SQL
     * @see Apuesta
     * @see ApuestaSeleccion
     * @see ApuestaSeleccionDAO
     */
    public boolean updateWithSelections(Apuesta apuesta, List<ApuestaSeleccion> selecciones) throws SQLException {
        Connection conn = null;
        try {
            conn = ConexionBD.getConnection();
            conn.setAutoCommit(false);

            // 1. Actualizar apuesta principal
            if (!update(apuesta)) {
                conn.rollback();
                return false;
            }

            // 2. Eliminar selecciones antiguas
            ApuestaSeleccionDAO seleccionDAO = new ApuestaSeleccionDAO();
            if (!seleccionDAO.deleteByApuestaId(apuesta.getId())) {
                conn.rollback();
                return false;
            }

            // 3. Guardar nuevas selecciones
            for (ApuestaSeleccion seleccion : selecciones) {
                if (!seleccionDAO.save(seleccion)) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
}