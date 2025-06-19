package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.entidades.Carrera;
import mx.uv.feaa.enumeracion.EstadoCarrera;
import mx.uv.feaa.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación concreta de {@link IGenericDAO} para la entidad {@link Carrera}.
 * Esta clase proporciona operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * para gestionar las carreras de caballos en el sistema.
 * <p>
 * La clase maneja toda la información relacionada con las carreras, incluyendo
 * sus fechas, horarios, estados y requisitos de participación.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 * @see IGenericDAO
 * @see Carrera
 * @see EstadoCarrera
 */
public class CarreraDAO implements IGenericDAO<Carrera, String> {
    /**
     * Nombre de la tabla de Carrera en la base de datos.
     */
    private static final String TABLE = "Carrera";

    /**
     * Columnas de la tabla Carrera utilizadas en las operaciones.
     */
    private static final String[] COLUMNS = {"idCarrera", "nombre", "fecha", "hora",
            "distancia", "estado", "minimoParticipantes", "maximoParticipantes"};

    /**
     * Recupera una carrera específica de la base de datos usando su ID.
     *
     * @param id el identificador único de la carrera
     * @return un {@link Optional} que contiene la {@link Carrera} si se encuentra,
     *         o vacío si no existe una carrera con ese ID
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see Optional
     * @see Carrera
     */
    @Override
    public Optional<Carrera> getById(String id) throws SQLException {
        final String SQL = "SELECT * FROM " + TABLE + " WHERE idCarrera = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearCarrera(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Recupera todas las carreras registradas en el sistema.
     *
     * @return una {@link List} de {@link Carrera} con todas las carreras,
     *         o una lista vacía si no hay registros
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see List
     * @see Carrera
     */
    @Override
    public List<Carrera> getAll() throws SQLException {
        List<Carrera> carreras = new ArrayList<>();
        final String SQL = "SELECT * FROM " + TABLE;

        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {

            while (rs.next()) {
                carreras.add(mapearCarrera(rs));
            }
        }
        return carreras;
    }

    /**
     * Guarda una nueva carrera en la base de datos.
     *
     * @param carrera el objeto {@link Carrera} a persistir
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión, violación de restricciones únicas,
     *         o errores en la consulta SQL
     * @see Carrera
     */
    @Override
    public boolean save(Carrera carrera) throws SQLException {
        final String SQL = "INSERT INTO " + TABLE + " (" +
                String.join(", ", COLUMNS) + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, carrera.getIdCarrera());
            pstmt.setString(2, carrera.getNombre());
            pstmt.setDate(3, Date.valueOf(carrera.getFecha()));
            pstmt.setTime(4, Time.valueOf(carrera.getHora()));
            pstmt.setString(5, carrera.getDistancia());
            pstmt.setString(6, carrera.getEstado().name());
            pstmt.setInt(7, carrera.getMinimoParticipantes());
            pstmt.setInt(8, carrera.getMaximoParticipantes());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Actualiza los datos de una carrera existente.
     *
     * @param carrera el objeto {@link Carrera} con los datos actualizados
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see Carrera
     */
    @Override
    public boolean update(Carrera carrera) throws SQLException {
        final String SQL = "UPDATE " + TABLE + " SET " +
                "nombre = ?, fecha = ?, hora = ?, distancia = ?, " +
                "estado = ?, minimoParticipantes = ?, maximoParticipantes = ? " +
                "WHERE idCarrera = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, carrera.getNombre());
            pstmt.setDate(2, Date.valueOf(carrera.getFecha()));
            pstmt.setTime(3, Time.valueOf(carrera.getHora()));
            pstmt.setString(4, carrera.getDistancia());
            pstmt.setString(5, carrera.getEstado().name());
            pstmt.setInt(6, carrera.getMinimoParticipantes());
            pstmt.setInt(7, carrera.getMaximoParticipantes());
            pstmt.setString(8, carrera.getIdCarrera());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Elimina una carrera de la base de datos.
     *
     * @param id el identificador único de la carrera a eliminar
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     */
    @Override
    public boolean delete(String id) throws SQLException {
        final String SQL = "DELETE FROM " + TABLE + " WHERE idCarrera = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Recupera todas las carreras con un estado específico.
     *
     * @param estado el {@link EstadoCarrera} por el cual filtrar
     * @return una {@link List} de {@link Carrera} con el estado especificado,
     *         o una lista vacía si no hay registros
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see List
     * @see Carrera
     * @see EstadoCarrera
     */
    public List<Carrera> getByEstado(EstadoCarrera estado) throws SQLException {
        List<Carrera> carreras = new ArrayList<>();
        final String SQL = "SELECT * FROM " + TABLE + " WHERE estado = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, estado.name());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    carreras.add(mapearCarrera(rs));
                }
            }
        }
        return carreras;
    }

    /**
     * Convierte un registro de la base de datos (ResultSet) en un objeto {@link Carrera}.
     *
     * @param rs el {@link ResultSet} que contiene los datos de la carrera
     * @return un objeto {@link Carrera} con todos los datos mapeados
     * @throws SQLException si ocurre algún error al acceder a los datos del ResultSet
     * @see ResultSet
     * @see Carrera
     */
    private Carrera mapearCarrera(ResultSet rs) throws SQLException {
        Carrera carrera = new Carrera(
                rs.getString("idCarrera"),
                rs.getString("nombre"),
                rs.getDate("fecha").toLocalDate(),
                rs.getTime("hora").toLocalTime(),
                rs.getString("distancia")
        );
        carrera.setEstado(EstadoCarrera.valueOf(rs.getString("estado")));
        carrera.setMinimoParticipantes(rs.getInt("minimoParticipantes"));
        carrera.setMaximoParticipantes(rs.getInt("maximoParticipantes"));
        return carrera;
    }
}