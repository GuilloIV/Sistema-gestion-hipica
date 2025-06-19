package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.entidades.*;
import mx.uv.feaa.enumeracion.EstadoParticipante;
import mx.uv.feaa.util.ConexionBD;
import java.sql.*;
import java.util.*;

/**
 * Implementación concreta de {@link IGenericDAO} para la entidad {@link Participante}.
 * Esta clase proporciona operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * para gestionar los participantes en carreras de caballos.
 * <p>
 * La clase maneja la relación entre carreras, caballos y jinetes, manteniendo
 * información sobre el estado de participación y características de competencia.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 * @see IGenericDAO
 * @see Participante
 * @see CaballoDAO
 * @see JineteDAO
 * @see CarreraDAO
 * @see EstadoParticipante
 */
public class ParticipanteDAO implements IGenericDAO<Participante, String> {
    private final CaballoDAO caballoDAO;
    private final JineteDAO jineteDAO;
    private final CarreraDAO carreraDAO;

    /**
     * Constructor que inicializa las dependencias DAO necesarias.
     */
    public ParticipanteDAO() {
        this.caballoDAO = new CaballoDAO();
        this.jineteDAO = new JineteDAO();
        this.carreraDAO = new CarreraDAO();
    }

    /**
     * Recupera un participante específico de la base de datos usando su ID.
     *
     * @param id el identificador único del participante
     * @return un {@link Optional} que contiene el {@link Participante} si se encuentra,
     *         o vacío si no existe un participante con ese ID
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see Optional
     * @see Participante
     */
    @Override
    public Optional<Participante> getById(String id) throws SQLException {
        String sql = "SELECT * FROM Participante WHERE idParticipante = ?";
        Participante participante = null;

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    participante = mapearParticipante(rs);
                }
            }
        }
        return Optional.ofNullable(participante);
    }

    /**
     * Recupera todos los participantes registrados en el sistema.
     *
     * @return una {@link List} de {@link Participante} con todos los participantes,
     *         o una lista vacía si no hay registros
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see List
     * @see Participante
     */
    @Override
    public List<Participante> getAll() throws SQLException {
        String sql = "SELECT * FROM Participante";
        List<Participante> participantes = new ArrayList<>();

        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                participantes.add(mapearParticipante(rs));
            }
        }
        return participantes;
    }

    /**
     * Guarda un nuevo participante en la base de datos.
     *
     * @param participante el objeto {@link Participante} a persistir
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión, violación de restricciones únicas,
     *         o errores en la consulta SQL
     * @see Participante
     */
    @Override
    public boolean save(Participante participante) throws SQLException {
        String sql = "INSERT INTO Participante (idParticipante, carrera_id, numeroCompetidor, " +
                "pesoAsignado, caballo_id, jinete_id, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, participante.getIdParticipante());
            stmt.setString(2, participante.getCarrera().getIdCarrera());
            stmt.setInt(3, participante.getNumeroCompetidor());
            stmt.setDouble(4, participante.getPesoAsignado());
            stmt.setString(5, participante.getCaballo().getIdCaballo());
            stmt.setString(6, participante.getJinete().getIdJinete());
            stmt.setString(7, participante.getEstado().name());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Actualiza los datos de un participante existente.
     *
     * @param participante el objeto {@link Participante} con los datos actualizados
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see Participante
     */
    @Override
    public boolean update(Participante participante) throws SQLException {
        String sql = "UPDATE Participante SET carrera_id = ?, numeroCompetidor = ?, pesoAsignado = ?, " +
                "caballo_id = ?, jinete_id = ?, estado = ? WHERE idParticipante = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, participante.getCarrera().getIdCarrera());
            stmt.setInt(2, participante.getNumeroCompetidor());
            stmt.setDouble(3, participante.getPesoAsignado());
            stmt.setString(4, participante.getCaballo().getIdCaballo());
            stmt.setString(5, participante.getJinete().getIdJinete());
            stmt.setString(6, participante.getEstado().name());
            stmt.setString(7, participante.getIdParticipante());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Elimina un participante de la base de datos.
     *
     * @param id el identificador único del participante a eliminar
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     */
    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM Participante WHERE idParticipante = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Convierte un registro de la base de datos (ResultSet) en un objeto {@link Participante}.
     * <p>
     * Este método utiliza otros DAOs ({@link CaballoDAO}, {@link JineteDAO}, {@link CarreraDAO})
     * para obtener los objetos relacionados.
     * </p>
     *
     * @param rs el {@link ResultSet} que contiene los datos del participante
     * @return un objeto {@link Participante} con todos los datos mapeados
     * @throws SQLException si ocurre algún error al acceder a los datos del ResultSet
     * @see ResultSet
     * @see Participante
     */
    private Participante mapearParticipante(ResultSet rs) throws SQLException {
        Participante participante = new Participante(
                rs.getString("idParticipante"),
                rs.getInt("numeroCompetidor"),
                rs.getDouble("pesoAsignado"),
                caballoDAO.getById(rs.getString("caballo_id")).orElse(null),
                jineteDAO.getById(rs.getString("jinete_id")).orElse(null)
        );

        participante.setEstado(EstadoParticipante.valueOf(rs.getString("estado")));
        participante.setCarrera(carreraDAO.getById(rs.getString("carrera_id")).orElse(null));

        return participante;
    }

    /**
     * Recupera todos los participantes asociados a una carrera específica.
     * Los resultados se ordenan por número de competidor.
     *
     * @param carreraId el identificador único de la carrera
     * @return una {@link List} de {@link Participante} asociados a la carrera,
     *         ordenados por número de competidor, o una lista vacía si no hay registros
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see List
     * @see Participante
     */
    public List<Participante> getByCarreraId(String carreraId) throws SQLException {
        String sql = "SELECT * FROM Participante WHERE carrera_id = ? ORDER BY numeroCompetidor";
        List<Participante> participantes = new ArrayList<>();

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, carreraId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    participantes.add(mapearParticipante(rs));
                }
            }
        }
        return participantes;
    }

    /**
     * Actualiza el estado de un participante específico.
     *
     * @param idParticipante el identificador único del participante
     * @param nuevoEstado el nuevo {@link EstadoParticipante} a asignar
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see EstadoParticipante
     */
    public boolean updateEstado(String idParticipante, EstadoParticipante nuevoEstado) throws SQLException {
        String sql = "UPDATE Participante SET estado = ? WHERE idParticipante = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevoEstado.name());
            stmt.setString(2, idParticipante);

            return stmt.executeUpdate() > 0;
        }
    }
}