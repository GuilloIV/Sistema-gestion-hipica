package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.entidades.Resultado;
import mx.uv.feaa.util.ConexionBD;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Implementación concreta de {@link IGenericDAO} para la entidad {@link Resultado}.
 * Esta clase proporciona operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * para gestionar los resultados de las carreras de caballos.
 * <p>
 * La clase maneja tanto los resultados principales como sus detalles asociados,
 * incluyendo posiciones y tiempos oficiales de los participantes.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 * @see IGenericDAO
 * @see Resultado
 */
public class ResultadoDAO implements IGenericDAO<Resultado, String> {

    /**
     * Recupera un resultado específico de la base de datos usando su ID.
     * Incluye la carga de todos los detalles asociados al resultado.
     *
     * @param id el identificador único del resultado
     * @return un {@link Optional} que contiene el {@link Resultado} si se encuentra,
     *         o vacío si no existe un resultado con ese ID
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en las consultas SQL
     * @see Optional
     * @see Resultado
     */
    @Override
    public Optional<Resultado> getById(String id) throws SQLException {
        String sql = "SELECT * FROM Resultado WHERE idResultado = ?";
        Resultado resultado = null;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    resultado = mapearResultado(rs);
                    // Cargar detalles del resultado
                    cargarDetallesResultado(conn, resultado);
                }
            }
        }
        return Optional.ofNullable(resultado);
    }

    /**
     * Recupera todos los resultados registrados en el sistema.
     * Incluye la carga de todos los detalles asociados a cada resultado.
     *
     * @return una {@link List} de {@link Resultado} con todos los resultados,
     *         o una lista vacía si no hay registros
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en las consultas SQL
     * @see List
     * @see Resultado
     */
    @Override
    public List<Resultado> getAll() throws SQLException {
        String sql = "SELECT * FROM Resultado";
        List<Resultado> resultados = new ArrayList<>();

        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Resultado resultado = mapearResultado(rs);
                // Cargar detalles del resultado
                cargarDetallesResultado(conn, resultado);
                resultados.add(resultado);
            }
        }
        return resultados;
    }

    /**
     * Guarda un nuevo resultado en la base de datos.
     * Incluye el guardado de todos los detalles asociados al resultado.
     *
     * @param resultado el objeto {@link Resultado} a persistir
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión, violación de restricciones únicas,
     *         o errores en las consultas SQL
     * @see Resultado
     */
    @Override
    public boolean save(Resultado resultado) throws SQLException {
        String sql = "INSERT INTO Resultado (idResultado, carrera_id, fechaRegistro) VALUES (?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, resultado.getIdResultado());
            stmt.setString(2, resultado.getIdCarrera());
            stmt.setDate(3, Date.valueOf(resultado.getFechaRegistro()));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                // Guardar detalles del resultado
                guardarDetallesResultado(conn, resultado);
                return true;
            }
        }
        return false;
    }

    /**
     * Actualiza un resultado existente en la base de datos.
     * Incluye la actualización de todos los detalles asociados al resultado.
     *
     * @param resultado el objeto {@link Resultado} con los datos actualizados
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en las consultas SQL
     * @see Resultado
     */
    @Override
    public boolean update(Resultado resultado) throws SQLException {
        String sql = "UPDATE Resultado SET carrera_id = ?, fechaRegistro = ? WHERE idResultado = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, resultado.getIdCarrera());
            stmt.setDate(2, Date.valueOf(resultado.getFechaRegistro()));
            stmt.setString(3, resultado.getIdResultado());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                // Actualizar detalles del resultado
                actualizarDetallesResultado(conn, resultado);
                return true;
            }
        }
        return false;
    }

    /**
     * Elimina un resultado de la base de datos.
     * Los detalles asociados se eliminan automáticamente por la relación de clave foránea.
     *
     * @param id el identificador único del resultado a eliminar
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     */
    @Override
    public boolean delete(String id) throws SQLException {
        // Los detalles se eliminan en cascada por la FK
        String sql = "DELETE FROM Resultado WHERE idResultado = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Convierte un registro de la base de datos (ResultSet) en un objeto {@link Resultado}.
     *
     * @param rs el {@link ResultSet} que contiene los datos del resultado
     * @return un objeto {@link Resultado} con los datos básicos mapeados
     * @throws SQLException si ocurre algún error al acceder a los datos del ResultSet
     * @see ResultSet
     * @see Resultado
     */
    private Resultado mapearResultado(ResultSet rs) throws SQLException {
        Resultado resultado = new Resultado(
                rs.getString("idResultado"),
                rs.getString("carrera_id")
        );
        resultado.setFechaRegistro(rs.getDate("fechaRegistro").toLocalDate());
        return resultado;
    }

    /**
     * Carga los detalles asociados a un resultado desde la base de datos.
     *
     * @param conn la conexión a la base de datos
     * @param resultado el objeto {@link Resultado} al que se agregarán los detalles
     * @throws SQLException si ocurre algún error al acceder a la base de datos
     * @see Resultado
     */
    private void cargarDetallesResultado(Connection conn, Resultado resultado) throws SQLException {
        String sql = "SELECT * FROM ResultadoDetalle WHERE resultado_id = ? ORDER BY posicion";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, resultado.getIdResultado());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int posicion = rs.getInt("posicion");
                    String idParticipante = rs.getString("participante_id");
                    LocalTime tiempo = rs.getTime("tiempoOficial").toLocalTime();

                    resultado.registrarPosicion(posicion, idParticipante, tiempo);
                }
            }
        }
    }

    /**
     * Guarda los detalles asociados a un resultado en la base de datos.
     *
     * @param conn la conexión a la base de datos
     * @param resultado el objeto {@link Resultado} del que se obtendrán los detalles
     * @throws SQLException si ocurre algún error al acceder a la base de datos
     * @see Resultado
     */
    private void guardarDetallesResultado(Connection conn, Resultado resultado) throws SQLException {
        String sql = "INSERT INTO ResultadoDetalle (idDetalle, resultado_id, participante_id, posicion, tiempoOficial) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Map.Entry<Integer, String> entry : resultado.getPosiciones().entrySet()) {
                int posicion = entry.getKey();
                String idParticipante = entry.getValue();
                LocalTime tiempo = resultado.getTiempos().get(idParticipante);

                stmt.setString(1, UUID.randomUUID().toString());
                stmt.setString(2, resultado.getIdResultado());
                stmt.setString(3, idParticipante);
                stmt.setInt(4, posicion);
                stmt.setTime(5, Time.valueOf(tiempo));

                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    /**
     * Actualiza los detalles asociados a un resultado en la base de datos.
     * Primero elimina los detalles existentes y luego inserta los nuevos.
     *
     * @param conn la conexión a la base de datos
     * @param resultado el objeto {@link Resultado} del que se obtendrán los nuevos detalles
     * @throws SQLException si ocurre algún error al acceder a la base de datos
     * @see Resultado
     */
    private void actualizarDetallesResultado(Connection conn, Resultado resultado) throws SQLException {
        // Primero eliminar los detalles existentes
        String deleteSql = "DELETE FROM ResultadoDetalle WHERE resultado_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            stmt.setString(1, resultado.getIdResultado());
            stmt.executeUpdate();
        }

        // Luego insertar los nuevos detalles
        guardarDetallesResultado(conn, resultado);
    }

    /**
     * Recupera el resultado asociado a una carrera específica.
     * Incluye la carga de todos los detalles asociados al resultado.
     *
     * @param idCarrera el identificador único de la carrera
     * @return un {@link Optional} que contiene el {@link Resultado} si se encuentra,
     *         o vacío si no existe un resultado para esa carrera
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en las consultas SQL
     * @see Optional
     * @see Resultado
     */
    public Optional<Resultado> getByCarreraId(String idCarrera) throws SQLException {
        String sql = "SELECT * FROM Resultado WHERE carrera_id = ?";
        Resultado resultado = null;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, idCarrera);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    resultado = mapearResultado(rs);
                    cargarDetallesResultado(conn, resultado);
                }
            }
        }
        return Optional.ofNullable(resultado);
    }
}