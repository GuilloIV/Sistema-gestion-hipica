package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.entidades.HistorialCarrera;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistorialCarreraDAO extends BaseDAOImpl<HistorialCarrera> implements IGenericDAO<HistorialCarrera> {
    private static final String TABLA = "historial_carreras";

    @Override
    public HistorialCarrera obtenerPorId(String id) throws SQLException {
        // En este caso, asumimos que el id es compuesto (caballo_id + carrera_id)
        throw new UnsupportedOperationException("Método no implementado para historial");
    }

    @Override
    public List<HistorialCarrera> obtenerTodos() throws SQLException {
        String sql = "SELECT * FROM " + TABLA;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<HistorialCarrera> historiales = new ArrayList<>();

        try {
            conn = obtenerConexion();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                historiales.add(mapearHistorial(rs));
            }
            return historiales;
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    @Override
    public boolean insertar(HistorialCarrera historial) throws SQLException {
        String sql = "INSERT INTO " + TABLA + " (carrera_id, nombre_carrera, posicion, tiempo, fecha, hipodromo, " +
                "caballo_id, jinete_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = obtenerConexion();
            stmt = conn.prepareStatement(sql);

            stmt.setString(1, historial.getCarreraId());
            stmt.setString(2, historial.getNombreCarrera());
            stmt.setInt(3, historial.getPosicion());
            stmt.setTime(4, Time.valueOf(historial.getTiempo()));
            stmt.setDate(5, Date.valueOf(historial.getFecha()));
            stmt.setString(6, historial.getHipodromo());
            stmt.setString(7, historial.getCaballoId());
            stmt.setString(8, historial.getJineteId());

            return stmt.executeUpdate() > 0;
        } finally {
            cerrarRecursos(conn, stmt, null);
        }
    }

    @Override
    public boolean actualizar(HistorialCarrera historial) throws SQLException {
        String sql = "UPDATE " + TABLA + " SET posicion = ?, tiempo = ? " +
                "WHERE carrera_id = ? AND caballo_id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = obtenerConexion();
            stmt = conn.prepareStatement(sql);

            stmt.setInt(1, historial.getPosicion());
            stmt.setTime(2, Time.valueOf(historial.getTiempo()));
            stmt.setString(3, historial.getCarreraId());
            stmt.setString(4, historial.getCaballoId());

            return stmt.executeUpdate() > 0;
        } finally {
            cerrarRecursos(conn, stmt, null);
        }
    }

    @Override
    public boolean eliminar(String id) throws SQLException {
        // En este caso, asumimos que el id es compuesto (caballo_id + carrera_id)
        throw new UnsupportedOperationException("Método no implementado para historial");
    }

    private HistorialCarrera mapearHistorial(ResultSet rs) throws SQLException {
        return new HistorialCarrera(
                rs.getString("carrera_id"),
                rs.getString("nombre_carrera"),
                rs.getInt("posicion"),
                rs.getTime("tiempo").toLocalTime(),
                rs.getDate("fecha").toLocalDate(),
                rs.getString("hipodromo")
        );
    }

    // Métodos adicionales específicos para HistorialCarrera
    public List<HistorialCarrera> obtenerPorCaballo(String caballoId) throws SQLException {
        String sql = "SELECT * FROM " + TABLA + " WHERE caballo_id = ? ORDER BY fecha DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<HistorialCarrera> historiales = new ArrayList<>();

        try {
            conn = obtenerConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, caballoId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                historiales.add(mapearHistorial(rs));
            }
            return historiales;
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    public List<HistorialCarrera> obtenerPorJinete(String jineteId) throws SQLException {
        String sql = "SELECT * FROM " + TABLA + " WHERE jinete_id = ? ORDER BY fecha DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<HistorialCarrera> historiales = new ArrayList<>();

        try {
            conn = obtenerConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, jineteId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                historiales.add(mapearHistorial(rs));
            }
            return historiales;
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    public List<HistorialCarrera> obtenerPorCarrera(String carreraId) throws SQLException {
        String sql = "SELECT * FROM " + TABLA + " WHERE carrera_id = ? ORDER BY posicion ASC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<HistorialCarrera> historiales = new ArrayList<>();

        try {
            conn = obtenerConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, carreraId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                historiales.add(mapearHistorial(rs));
            }
            return historiales;
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    public boolean existeRegistro(String carreraId, String caballoId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + TABLA + " WHERE carrera_id = ? AND caballo_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = obtenerConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, carreraId);
            stmt.setString(2, caballoId);
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