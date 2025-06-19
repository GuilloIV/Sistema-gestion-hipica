package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.entidades.Jinete;
import mx.uv.feaa.util.ConexionBD;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación concreta de {@link IGenericDAO} para la entidad {@link Jinete}.
 * Esta clase proporciona operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * para gestionar los jinetes registrados en el sistema de carreras.
 * <p>
 * La clase maneja toda la información relacionada con los jinetes, incluyendo
 * sus datos personales, características físicas y licencias.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 * @see IGenericDAO
 * @see Jinete
 */
public class JineteDAO implements IGenericDAO<Jinete, String> {

    /**
     * Nombre de la tabla de Jinetes en la base de datos.
     */
    private static final String TABLE = "Jinete";

    /**
     * Columnas de la tabla Jinete utilizadas en las operaciones.
     */
    private static final String[] COLUMNS = {
            "idJinete", "nombre", "fechaNacimiento", "peso",
            "licencia", "fechaVigenciaLicencia"
    };

    /**
     * Recupera un jinete específico de la base de datos usando su ID.
     *
     * @param id el identificador único del jinete
     * @return un {@link Optional} que contiene el {@link Jinete} si se encuentra,
     *         o vacío si no existe un jinete con ese ID
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see Optional
     * @see Jinete
     */
    @Override
    public Optional<Jinete> getById(String id) throws SQLException {
        final String SQL = "SELECT * FROM " + TABLE + " WHERE idJinete = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearJinete(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Recupera todos los jinetes registrados en el sistema.
     *
     * @return una {@link List} de {@link Jinete} con todos los jinetes,
     *         o una lista vacía si no hay registros
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see List
     * @see Jinete
     */
    @Override
    public List<Jinete> getAll() throws SQLException {
        List<Jinete> jinetes = new ArrayList<>();
        final String SQL = "SELECT * FROM " + TABLE;

        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {

            while (rs.next()) {
                jinetes.add(mapearJinete(rs));
            }
        }
        return jinetes;
    }

    /**
     * Guarda un nuevo jinete en la base de datos.
     *
     * @param jinete el objeto {@link Jinete} a persistir
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión, violación de restricciones únicas,
     *         o errores en la consulta SQL
     * @see Jinete
     */
    @Override
    public boolean save(Jinete jinete) throws SQLException {
        final String SQL = "INSERT INTO " + TABLE + " (" +
                String.join(", ", COLUMNS) + ") VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, jinete.getIdJinete());
            pstmt.setString(2, jinete.getNombre());
            pstmt.setDate(3, Date.valueOf(jinete.getFechaNacimiento()));
            pstmt.setDouble(4, jinete.getPeso());
            pstmt.setString(5, jinete.getLicencia());
            pstmt.setDate(6, Date.valueOf(jinete.getFechaVigenciaLicencia()));

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Actualiza los datos de un jinete existente.
     *
     * @param jinete el objeto {@link Jinete} con los datos actualizados
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see Jinete
     */
    @Override
    public boolean update(Jinete jinete) throws SQLException {
        final String SQL = "UPDATE " + TABLE + " SET " +
                "nombre = ?, fechaNacimiento = ?, peso = ?, " +
                "licencia = ?, fechaVigenciaLicencia = ? " +
                "WHERE idJinete = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, jinete.getNombre());
            pstmt.setDate(2, Date.valueOf(jinete.getFechaNacimiento()));
            pstmt.setDouble(3, jinete.getPeso());
            pstmt.setString(4, jinete.getLicencia());
            pstmt.setDate(5, Date.valueOf(jinete.getFechaVigenciaLicencia()));
            pstmt.setString(6, jinete.getIdJinete());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Elimina un jinete de la base de datos.
     *
     * @param id el identificador único del jinete a eliminar
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     */
    @Override
    public boolean delete(String id) throws SQLException {
        final String SQL = "DELETE FROM " + TABLE + " WHERE idJinete = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Convierte un registro de la base de datos (ResultSet) en un objeto {@link Jinete}.
     *
     * @param rs el {@link ResultSet} que contiene los datos del jinete
     * @return un objeto {@link Jinete} con todos los datos mapeados
     * @throws SQLException si ocurre algún error al acceder a los datos del ResultSet
     * @see ResultSet
     * @see Jinete
     */
    private Jinete mapearJinete(ResultSet rs) throws SQLException {
        Jinete jinete = new Jinete();
        jinete.setIdJinete(rs.getString("idJinete"));
        jinete.setNombre(rs.getString("nombre"));
        jinete.setFechaNacimiento(rs.getDate("fechaNacimiento").toLocalDate());
        jinete.setPeso(rs.getDouble("peso"));
        jinete.setLicencia(rs.getString("licencia"));
        jinete.setFechaVigenciaLicencia(rs.getDate("fechaVigenciaLicencia").toLocalDate());
        return jinete;
    }
}