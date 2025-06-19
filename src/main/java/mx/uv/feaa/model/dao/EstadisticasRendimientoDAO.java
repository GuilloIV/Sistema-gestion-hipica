package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.entidades.EstadisticasRendimiento;
import mx.uv.feaa.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación concreta de {@link IGenericDAO} para la entidad {@link EstadisticasRendimiento}.
 * Esta clase proporciona operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * para gestionar las estadísticas de rendimiento de caballos y jinetes en el sistema.
 * <p>
 * La clase maneja tanto estadísticas para caballos como para jinetes, diferenciando
 * entre ellos mediante el campo 'tipoEntidad'.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 * @see IGenericDAO
 * @see EstadisticasRendimiento
 */
public class EstadisticasRendimientoDAO implements IGenericDAO<EstadisticasRendimiento, String> {

    /**
     * Nombre de la tabla de estadísticas de rendimiento en la base de datos.
     */
    private static final String TABLA = "EstadisticasRendimiento";

    /**
     * Recupera estadísticas de rendimiento específicas usando su ID.
     *
     * @param id el identificador único de las estadísticas
     * @return un {@link Optional} que contiene las {@link EstadisticasRendimiento} si se encuentran,
     *         o vacío si no existen estadísticas con ese ID
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see Optional
     * @see EstadisticasRendimiento
     */
    @Override
    public Optional<EstadisticasRendimiento> getById(String id) throws SQLException {
        String sql = String.format("SELECT * FROM %s WHERE idEstadistica = ?", TABLA);
        EstadisticasRendimiento estadistica = null;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    estadistica = mapearEstadistica(rs);
                }
            }
        }

        return Optional.ofNullable(estadistica);
    }

    /**
     * Recupera todas las estadísticas de rendimiento registradas en el sistema.
     *
     * @return una {@link List} de {@link EstadisticasRendimiento} con todas las estadísticas,
     *         o una lista vacía si no hay registros
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see List
     * @see EstadisticasRendimiento
     */
    @Override
    public List<EstadisticasRendimiento> getAll() throws SQLException {
        String sql = String.format("SELECT * FROM %s", TABLA);
        List<EstadisticasRendimiento> estadisticas = new ArrayList<>();

        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                estadisticas.add(mapearEstadistica(rs));
            }
        }

        return estadisticas;
    }

    /**
     * Guarda nuevas estadísticas de rendimiento en la base de datos.
     *
     * @param estadistica el objeto {@link EstadisticasRendimiento} a persistir
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión, violación de restricciones únicas,
     *         o errores en la consulta SQL
     * @see EstadisticasRendimiento
     */
    @Override
    public boolean save(EstadisticasRendimiento estadistica) throws SQLException {
        String sql = String.format("INSERT INTO %s (idEstadistica, caballo_id, jinete_id, totalCarreras, victorias, " +
                "colocaciones, promedioTiempo, porcentajeVictorias) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", TABLA);

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            configurarStatement(stmt, estadistica);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Actualiza las estadísticas de rendimiento existentes.
     *
     * @param estadistica el objeto {@link EstadisticasRendimiento} con los datos actualizados
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see EstadisticasRendimiento
     */
    @Override
    public boolean update(EstadisticasRendimiento estadistica) throws SQLException {
        String sql = String.format("UPDATE %s SET caballo_id = ?, jinete_id = ?, totalCarreras = ?, victorias = ?, " +
                "colocaciones = ?, promedioTiempo = ?, porcentajeVictorias = ? WHERE idEstadistica = ?", TABLA);

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            configurarStatement(stmt, estadistica);
            stmt.setString(8, estadistica.getIdEstadistica());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Elimina estadísticas de rendimiento de la base de datos.
     *
     * @param id el identificador único de las estadísticas a eliminar
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     */
    @Override
    public boolean delete(String id) throws SQLException {
        String sql = String.format("DELETE FROM %s WHERE idEstadistica = ?", TABLA);

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Recupera las estadísticas de rendimiento asociadas a un caballo específico.
     *
     * @param caballoId el identificador único del caballo
     * @return un {@link Optional} que contiene las {@link EstadisticasRendimiento} si se encuentran,
     *         o vacío si no existen estadísticas para ese caballo
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see Optional
     * @see EstadisticasRendimiento
     */
    public Optional<EstadisticasRendimiento> getByCaballoId(String caballoId) throws SQLException {
        String sql = String.format("SELECT * FROM %s WHERE caballo_id = ?", TABLA);
        EstadisticasRendimiento estadistica = null;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, caballoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    estadistica = mapearEstadistica(rs);
                }
            }
        }

        return Optional.ofNullable(estadistica);
    }

    /**
     * Recupera las estadísticas de rendimiento asociadas a un jinete específico.
     *
     * @param jineteId el identificador único del jinete
     * @return un {@link Optional} que contiene las {@link EstadisticasRendimiento} si se encuentran,
     *         o vacío si no existen estadísticas para ese jinete
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see Optional
     * @see EstadisticasRendimiento
     */
    public Optional<EstadisticasRendimiento> getByJineteId(String jineteId) throws SQLException {
        String sql = String.format("SELECT * FROM %s WHERE jinete_id = ?", TABLA);
        EstadisticasRendimiento estadistica = null;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, jineteId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    estadistica = mapearEstadistica(rs);
                }
            }
        }

        return Optional.ofNullable(estadistica);
    }

    /**
     * Convierte un registro de la base de datos (ResultSet) en un objeto {@link EstadisticasRendimiento}.
     * <p>
     * Determina automáticamente si las estadísticas pertenecen a un caballo o jinete
     * basándose en los campos no nulos en la base de datos.
     * </p>
     *
     * @param rs el {@link ResultSet} que contiene los datos de las estadísticas
     * @return un objeto {@link EstadisticasRendimiento} con todos los datos mapeados
     * @throws SQLException si ocurre algún error al acceder a los datos del ResultSet
     * @see ResultSet
     * @see EstadisticasRendimiento
     */
    private EstadisticasRendimiento mapearEstadistica(ResultSet rs) throws SQLException {
        EstadisticasRendimiento estadistica = new EstadisticasRendimiento();

        estadistica.setIdEstadistica(rs.getString("idEstadistica"));
        estadistica.setIdEntidad(rs.getString("caballo_id") != null ? rs.getString("caballo_id") : rs.getString("jinete_id"));
        estadistica.setTipoEntidad(rs.getString("caballo_id") != null ? "CABALLO" : "JINETE");
        estadistica.setTotalCarreras(rs.getInt("totalCarreras"));
        estadistica.setVictorias(rs.getInt("victorias"));
        estadistica.setColocaciones(rs.getInt("colocaciones"));
        estadistica.setPromedioTiempo(rs.getTime("promedioTiempo") != null ? rs.getTime("promedioTiempo").toLocalTime() : null);
        estadistica.setPorcentajeVictorias(rs.getDouble("porcentajeVictorias"));

        return estadistica;
    }

    /**
     * Configura un PreparedStatement con los datos de las estadísticas de rendimiento.
     * <p>
     * Maneja adecuadamente los valores nulos y diferencia entre estadísticas
     * de caballos y jinetes.
     * </p>
     *
     * @param stmt el {@link PreparedStatement} a configurar
     * @param estadistica el objeto {@link EstadisticasRendimiento} con los datos
     * @throws SQLException si ocurre algún error al configurar el statement
     * @see PreparedStatement
     * @see EstadisticasRendimiento
     */
    private void configurarStatement(PreparedStatement stmt, EstadisticasRendimiento estadistica) throws SQLException {
        stmt.setString(1, estadistica.getIdEstadistica());

        if (estadistica.getTipoEntidad().equals("CABALLO")) {
            stmt.setString(2, estadistica.getIdEntidad());
            stmt.setNull(3, Types.VARCHAR);
        } else {
            stmt.setNull(2, Types.VARCHAR);
            stmt.setString(3, estadistica.getIdEntidad());
        }

        stmt.setInt(4, estadistica.getTotalCarreras());
        stmt.setInt(5, estadistica.getVictorias());
        stmt.setInt(6, estadistica.getColocaciones());

        if (estadistica.getPromedioTiempo() != null) {
            stmt.setTime(7, Time.valueOf(estadistica.getPromedioTiempo()));
        } else {
            stmt.setNull(7, Types.TIME);
        }

        stmt.setDouble(8, estadistica.getPorcentajeVictorias());
    }
}