package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.entidades.Caballo;
import mx.uv.feaa.enumeracion.SexoCaballo;
import mx.uv.feaa.util.ConexionBD;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación concreta de {@link IGenericDAO} para la entidad {@link Caballo}.
 * Esta clase proporciona operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * para gestionar los caballos registrados en el sistema de carreras.
 * <p>
 * La clase maneja toda la información relacionada con los caballos, incluyendo
 * sus características físicas, datos de nacimiento, criador y participación
 * en carreras.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 * @see IGenericDAO
 * @see Caballo
 * @see SexoCaballo
 */
public class CaballoDAO implements IGenericDAO<Caballo, String> {
    /**
     * Nombre de la tabla de Caballo en la base de datos.
     */
    private static final String TABLE_NAME = "Caballo";

    /**
     * Recupera un caballo específico de la base de datos usando su ID.
     *
     * @param id el identificador único del caballo
     * @return un {@link Optional} que contiene el {@link Caballo} si se encuentra,
     *         o vacío si no existe un caballo con ese ID
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see Optional
     * @see Caballo
     */
    @Override
    public Optional<Caballo> getById(String id) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE idCaballo = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearCaballo(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Recupera todos los caballos registrados en el sistema.
     *
     * @return una {@link List} de {@link Caballo} con todos los caballos,
     *         o una lista vacía si no hay registros
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see List
     * @see Caballo
     */
    @Override
    public List<Caballo> getAll() throws SQLException {
        List<Caballo> caballos = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME;

        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                caballos.add(mapearCaballo(rs));
            }
        }
        return caballos;
    }

    /**
     * Guarda un nuevo caballo en la base de datos.
     *
     * @param caballo el objeto {@link Caballo} a persistir
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión, violación de restricciones únicas,
     *         o errores en la consulta SQL
     * @see Caballo
     */
    @Override
    public boolean save(Caballo caballo) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME +
                " (idCaballo, nombre, fechaNacimiento, sexo, peso, pedigri, ultimaCarrera, criador_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, caballo.getIdCaballo());
            prepararStatementParaInsert(stmt, caballo);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Actualiza los datos de un caballo existente.
     *
     * @param caballo el objeto {@link Caballo} con los datos actualizados
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see Caballo
     */
    @Override
    public boolean update(Caballo caballo) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET " +
                "nombre = ?, fechaNacimiento = ?, sexo = ?, peso = ?, " +
                "pedigri = ?, ultimaCarrera = ?, criador_id = ? " +
                "WHERE idCaballo = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            prepararStatementParaUpdate(stmt, caballo);
            stmt.setString(8, caballo.getIdCaballo());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Elimina un caballo de la base de datos.
     *
     * @param id el identificador único del caballo a eliminar
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     */
    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE idCaballo = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Recupera todos los caballos asociados a un criador específico.
     *
     * @param criadorId el identificador único del criador
     * @return una {@link List} de {@link Caballo} asociados al criador,
     *         o una lista vacía si no hay registros
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     * @see List
     * @see Caballo
     */
    public List<Caballo> getByCriador(String criadorId) throws SQLException {
        List<Caballo> caballos = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE criador_id = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, criadorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    caballos.add(mapearCaballo(rs));
                }
            }
        }
        return caballos;
    }

    /**
     * Actualiza la fecha de la última carrera en la que participó un caballo.
     *
     * @param idCaballo el identificador único del caballo
     * @param fecha la fecha de la última carrera participada
     * @return true si la operación se completó con éxito, false si falló
     * @throws SQLException si ocurre algún error al acceder a la base de datos,
     *         incluyendo problemas de conexión o errores en la consulta SQL
     */
    public boolean actualizarUltimaCarrera(String idCaballo, LocalDate fecha) throws SQLException {
        String sql = "UPDATE " + TABLE_NAME + " SET ultimaCarrera = ? WHERE idCaballo = ?";

        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(fecha));
            stmt.setString(2, idCaballo);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Convierte un registro de la base de datos (ResultSet) en un objeto {@link Caballo}.
     * Maneja adecuadamente los valores nulos en campos como ultimaCarrera.
     *
     * @param rs el {@link ResultSet} que contiene los datos del caballo
     * @return un objeto {@link Caballo} con todos los datos mapeados
     * @throws SQLException si ocurre algún error al acceder a los datos del ResultSet
     * @see ResultSet
     * @see Caballo
     */
    private Caballo mapearCaballo(ResultSet rs) throws SQLException {
        Caballo caballo = new Caballo();
        caballo.setIdCaballo(rs.getString("idCaballo"));
        caballo.setNombre(rs.getString("nombre"));
        caballo.setFechaNacimiento(rs.getDate("fechaNacimiento").toLocalDate());
        caballo.setSexo(SexoCaballo.valueOf(rs.getString("sexo")));
        caballo.setPeso(rs.getDouble("peso"));
        caballo.setPedigri(rs.getString("pedigri"));

        Date ultimaCarrera = rs.getDate("ultimaCarrera");
        if (ultimaCarrera != null) {
            caballo.setUltimaCarrera(ultimaCarrera.toLocalDate());
        }
        // Si es null, se queda como null (valor por defecto)

        caballo.setCriadorId(rs.getString("criador_id"));

        return caballo;
    }

    /**
     * Prepara un PreparedStatement para una operación INSERT con los datos del caballo.
     * Maneja adecuadamente los valores nulos en campos como ultimaCarrera.
     *
     * @param stmt el {@link PreparedStatement} a configurar
     * @param caballo el objeto {@link Caballo} con los datos a insertar
     * @throws SQLException si ocurre algún error al configurar el statement
     * @see PreparedStatement
     * @see Caballo
     */
    private void prepararStatementParaInsert(PreparedStatement stmt, Caballo caballo) throws SQLException {
        stmt.setString(2, caballo.getNombre());
        stmt.setDate(3, Date.valueOf(caballo.getFechaNacimiento()));
        stmt.setString(4, caballo.getSexo().name());
        stmt.setDouble(5, caballo.getPeso());
        stmt.setString(6, caballo.getPedigri());

        // Manejar ultimaCarrera que puede ser null
        if (caballo.getUltimaCarrera() != null) {
            stmt.setDate(7, Date.valueOf(caballo.getUltimaCarrera()));
        } else {
            stmt.setNull(7, Types.DATE);
        }

        stmt.setString(8, caballo.getCriadorId());
    }

    /**
     * Prepara un PreparedStatement para una operación UPDATE con los datos del caballo.
     * Maneja adecuadamente los valores nulos en campos como ultimaCarrera.
     *
     * @param stmt el {@link PreparedStatement} a configurar
     * @param caballo el objeto {@link Caballo} con los datos a actualizar
     * @throws SQLException si ocurre algún error al configurar el statement
     * @see PreparedStatement
     * @see Caballo
     */
    private void prepararStatementParaUpdate(PreparedStatement stmt, Caballo caballo) throws SQLException {
        stmt.setString(1, caballo.getNombre());
        stmt.setDate(2, Date.valueOf(caballo.getFechaNacimiento()));
        stmt.setString(3, caballo.getSexo().name());
        stmt.setDouble(4, caballo.getPeso());
        stmt.setString(5, caballo.getPedigri());

        // Manejar ultimaCarrera que puede ser null
        if (caballo.getUltimaCarrera() != null) {
            stmt.setDate(6, Date.valueOf(caballo.getUltimaCarrera()));
        } else {
            stmt.setNull(6, Types.DATE);
        }

        stmt.setString(7, caballo.getCriadorId());
    }
}