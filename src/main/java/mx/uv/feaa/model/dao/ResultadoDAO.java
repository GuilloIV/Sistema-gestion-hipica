package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.entidades.Resultado;
import mx.uv.feaa.model.entidades.Participante;
import java.sql.*;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class ResultadoDAO extends BaseDAOImpl<Resultado> implements IGenericDAO<Resultado> {
    private static final String TABLA = "resultados";
    private final ParticipanteDAO participanteDAO = new ParticipanteDAO();
    private final CarreraDAO carreraDAO = new CarreraDAO();

    @Override
    public Resultado obtenerPorId(String id) throws SQLException {
        // En este caso, el id es el mismo que el de la carrera
        return obtenerPorCarrera(id);
    }

    public Resultado obtenerPorCarrera(String carreraId) throws SQLException {
        String sql = "SELECT * FROM " + TABLA + " WHERE carrera_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = obtenerConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, carreraId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Resultado resultado = new Resultado(carreraDAO.obtenerPorId(carreraId));
                resultado.setFechaRegistro(rs.getDate("fecha_registro").toLocalDate());

                // Cargar posiciones y tiempos
                cargarPosiciones(resultado);
                cargarTiempos(resultado);

                return resultado;
            }
            return null;
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    @Override
    public List<Resultado> obtenerTodos() throws SQLException {
        String sql = "SELECT * FROM " + TABLA;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Resultado> resultados = new ArrayList<>();

        try {
            conn = obtenerConexion();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String carreraId = rs.getString("carrera_id");
                Resultado resultado = new Resultado(carreraDAO.obtenerPorId(carreraId));
                resultado.setFechaRegistro(rs.getDate("fecha_registro").toLocalDate());

                // Cargar posiciones y tiempos
                cargarPosiciones(resultado);
                cargarTiempos(resultado);

                resultados.add(resultado);
            }
            return resultados;
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    @Override
    public boolean insertar(Resultado resultado) throws SQLException {
        String sql = "INSERT INTO " + TABLA + " (carrera_id, fecha_registro) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = obtenerConexion();
            conn.setAutoCommit(false); // Iniciar transacción

            // Insertar resultado principal
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, resultado.getCarrera().getIdCarrera());
            stmt.setDate(2, Date.valueOf(resultado.getFechaRegistro()));
            stmt.executeUpdate();

            // Insertar posiciones
            insertarPosiciones(conn, resultado);

            // Insertar tiempos
            insertarTiempos(conn, resultado);

            conn.commit(); // Confirmar transacción
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Revertir en caso de error
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
            }
            cerrarRecursos(conn, stmt, null);
        }
    }

    @Override
    public boolean actualizar(Resultado resultado) throws SQLException {
        // Para actualizar, primero eliminamos los registros existentes y luego insertamos los nuevos
        eliminarPosiciones(resultado.getCarrera().getIdCarrera());
        eliminarTiempos(resultado.getCarrera().getIdCarrera());

        // Luego insertamos los nuevos datos
        return insertar(resultado);
    }

    @Override
    public boolean eliminar(String id) throws SQLException {
        // Primero eliminamos posiciones y tiempos
        eliminarPosiciones(id);
        eliminarTiempos(id);

        // Luego eliminamos el resultado principal
        String sql = "DELETE FROM " + TABLA + " WHERE carrera_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = obtenerConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);

            return stmt.executeUpdate() > 0;
        } finally {
            cerrarRecursos(conn, stmt, null);
        }
    }

    // Métodos auxiliares para manejar posiciones y tiempos
    private void cargarPosiciones(Resultado resultado) throws SQLException {
        String sql = "SELECT * FROM resultado_posiciones WHERE carrera_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = obtenerConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, resultado.getCarrera().getIdCarrera());
            rs = stmt.executeQuery();

            Map<Integer, Participante> posiciones = new HashMap<>();
            while (rs.next()) {
                int posicion = rs.getInt("posicion");
                Participante participante = participanteDAO.obtenerPorCarreraYNumero(
                        resultado.getCarrera().getIdCarrera(), rs.getInt("numero_competidor"));

                if (participante != null) {
                    posiciones.put(posicion, participante);
                }
            }
            resultado.setPosiciones(posiciones);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    private void cargarTiempos(Resultado resultado) throws SQLException {
        String sql = "SELECT * FROM resultado_tiempos WHERE carrera_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = obtenerConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, resultado.getCarrera().getIdCarrera());
            rs = stmt.executeQuery();

            Map<Participante, LocalTime> tiempos = new HashMap<>();
            while (rs.next()) {
                Participante participante = participanteDAO.obtenerPorCarreraYNumero(
                        resultado.getCarrera().getIdCarrera(), rs.getInt("numero_competidor"));

                if (participante != null) {
                    tiempos.put(participante, rs.getTime("tiempo").toLocalTime());
                }
            }
            resultado.setTiemposOficiales(tiempos);
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    private void insertarPosiciones(Connection conn, Resultado resultado) throws SQLException {
        String sql = "INSERT INTO resultado_posiciones (carrera_id, posicion, numero_competidor) VALUES (?, ?, ?)";
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);

            for (Map.Entry<Integer, Participante> entry : resultado.getPosiciones().entrySet()) {
                stmt.setString(1, resultado.getCarrera().getIdCarrera());
                stmt.setInt(2, entry.getKey());
                stmt.setInt(3, entry.getValue().getNumeroCompetidor());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    private void insertarTiempos(Connection conn, Resultado resultado) throws SQLException {
        String sql = "INSERT INTO resultado_tiempos (carrera_id, numero_competidor, tiempo) VALUES (?, ?, ?)";
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);

            for (Map.Entry<Participante, LocalTime> entry : resultado.getTiemposOficiales().entrySet()) {
                stmt.setString(1, resultado.getCarrera().getIdCarrera());
                stmt.setInt(2, entry.getKey().getNumeroCompetidor());
                stmt.setTime(3, Time.valueOf(entry.getValue()));
                stmt.addBatch();
            }
            stmt.executeBatch();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    private void eliminarPosiciones(String carreraId) throws SQLException {
        String sql = "DELETE FROM resultado_posiciones WHERE carrera_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = obtenerConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, carreraId);
            stmt.executeUpdate();
        } finally {
            cerrarRecursos(conn, stmt, null);
        }
    }

    private void eliminarTiempos(String carreraId) throws SQLException {
        String sql = "DELETE FROM resultado_tiempos WHERE carrera_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = obtenerConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, carreraId);
            stmt.executeUpdate();
        } finally {
            cerrarRecursos(conn, stmt, null);
        }
    }

    // Métodos adicionales específicos para Resultado
    public boolean existeResultadoParaCarrera(String carreraId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + TABLA + " WHERE carrera_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = obtenerConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, carreraId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }
}