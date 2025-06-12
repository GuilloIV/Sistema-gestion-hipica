package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.entidades.EstadisticasRendimiento;

import java.sql.*;
import java.util.Optional;

public class EstadisticasRendimientoDAO extends BaseDAOImpl<EstadisticasRendimiento> implements IGenericDAO<EstadisticasRendimiento> {
    private static final String TABLA = "estadisticas_rendimiento";

    @Override
    public EstadisticasRendimiento obtenerPorId(String id) throws SQLException {
        String sql = "SELECT * FROM " + TABLA + " WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = obtenerConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearEstadisticas(rs);
            }
            return null;
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    @Override
    public List<EstadisticasRendimiento> obtenerTodos() throws SQLException {
        String sql = "SELECT * FROM " + TABLA;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<EstadisticasRendimiento> estadisticas = new ArrayList<>();

        try {
            conn = obtenerConexion();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                estadisticas.add(mapearEstadisticas(rs));
            }
            return estadisticas;
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    @Override
    public boolean insertar(EstadisticasRendimiento estadisticas) throws SQLException {
        String sql = "INSERT INTO " + TABLA + " (id, entidad_id, tipo_entidad, total_carreras, " +
                "victorias, colocaciones, promedio_tiempo, porcentaje_victorias) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = obtenerConexion();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, estadisticas.getidparticipante());
            stmt.setString(2, estadisticas.getEntidadId());
            stmt.setString(3, estadisticas.getTipoEntidad());
            stmt.setInt(4, estadisticas.getTotalCarreras());
            stmt.setInt(5, estadisticas.getVictorias());
            stmt.setInt(6, estadisticas.getColocaciones());
            stmt.setTime(7, estadisticas.getPromedioTiempo() != null ?
                    Time.valueOf(estadisticas.getPromedioTiempo()) : null);
            stmt.setDouble(8, estadisticas.getPorcentajeVictorias());

            return stmt.executeUpdate() > 0;
        } finally {
            cerrarRecursos(conn, stmt, null);
        }
    }

    @Override
    public boolean actualizar(EstadisticasRendimiento estadisticas) throws SQLException {
        String sql = "UPDATE " + TABLA + " SET total_carreras = ?, victorias = ?, colocaciones = ?, " +
                "promedio_tiempo = ?, porcentaje_victorias = ? WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = obtenerConexion();
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, estadisticas.getTotalCarreras());
            stmt.setInt(2, estadisticas.getVictorias());
            stmt.setInt(3, estadisticas.getColocaciones());
            stmt.setTime(4, estadisticas.getPromedioTiempo() != null ?
                    Time.valueOf(estadisticas.getPromedioTiempo()) : null);
            stmt.setDouble(5, estadisticas.getPorcentajeVictorias());
            stmt.setString(6, estadisticas.getId());

            return stmt.executeUpdate() > 0;
        } finally {
            cerrarRecursos(conn, stmt, null);
        }
    }

    @Override
    public boolean eliminar(String id) throws SQLException {
        String sql = "DELETE FROM " + TABLA + " WHERE id = ?";
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

    // Método específico para obtener estadísticas por entidad (caballo o jinete)
    public Optional<EstadisticasRendimiento> obtenerPorEntidad(String entidadId, String tipoEntidad) throws SQLException {
        String sql = "SELECT * FROM " + TABLA + " WHERE entidad_id = ? AND tipo_entidad = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = obtenerConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, entidadId);
            stmt.setString(2, tipoEntidad);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapearEstadisticas(rs));
            }
            return Optional.empty();
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    // Método para eliminar estadísticas por entidad
    public boolean eliminarPorEntidad(String entidadId, String tipoEntidad) throws SQLException {
        String sql = "DELETE FROM " + TABLA + " WHERE entidad_id = ? AND tipo_entidad = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = obtenerConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, entidadId);
            stmt.setString(2, tipoEntidad);

            return stmt.executeUpdate() > 0;
        } finally {
            cerrarRecursos(conn, stmt, null);
        }
    }

    // Método para actualizar estadísticas basado en el historial de carreras
    public boolean actualizarDesdeHistorial(String entidadId, String tipoEntidad) throws SQLException {
        String columnaFiltro = tipoEntidad.equals("CABALLO") ? "caballo_id" : "jinete_id";

        String sql = """
        INSERT INTO estadisticas_rendimiento (id, entidad_id, tipo_entidad, total_carreras, victorias, colocaciones, porcentaje_victorias)
        SELECT 
            CONCAT(?, '_STATS'),
            ?,
            ?,
            COUNT(*) as total_carreras,
            SUM(CASE WHEN posicion = 1 THEN 1 ELSE 0 END) as victorias,
            SUM(CASE WHEN posicion <= 3 THEN 1 ELSE 0 END) as colocaciones,
            CASE 
                WHEN COUNT(*) = 0 THEN 0 
                ELSE (SUM(CASE WHEN posicion = 1 THEN 1 ELSE 0 END) * 100.0 / COUNT(*)) 
            END as porcentaje_victorias
        FROM historial_carreras
        WHERE """ + columnaFiltro + """ = ?
        ON DUPLICATE KEY UPDATE 
            total_carreras = VALUES(total_carreras), 
            victorias = VALUES(victorias), 
            colocaciones = VALUES(colocaciones), 
            porcentaje_victorias = VALUES(porcentaje_victorias)
        """;

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = obtenerConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, entidadId);  // Para CONCAT
            stmt.setString(2, entidadId);  // Para entidad_id
            stmt.setString(3, tipoEntidad); // Para tipo_entidad
            stmt.setString(4, entidadId);  // Para WHERE clause

            return stmt.executeUpdate() > 0;
        } finally {
            cerrarRecursos(conn, stmt, null);
        }
    }

    private EstadisticasRendimiento mapearEstadisticas(ResultSet rs) throws SQLException {
        EstadisticasRendimiento stats = new EstadisticasRendimiento();
        stats.setId(rs.getString("id"));
        stats.setEntidadId(rs.getString("entidad_id"));
        stats.setTipoEntidad(rs.getString("tipo_entidad"));
        stats.setTotalCarreras(rs.getInt("total_carreras"));
        stats.setVictorias(rs.getInt("victorias"));
        stats.setColocaciones(rs.getInt("colocaciones"));

        Time tiempo = rs.getTime("promedio_tiempo");
        if (tiempo != null) {
            stats.setPromedioTiempo(tiempo.toLocalTime());
        }

        stats.setPorcentajeVictorias(rs.getDouble("porcentaje_victorias"));

        return stats;
    }
}