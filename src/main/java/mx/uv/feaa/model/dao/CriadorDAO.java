package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.entidades.Criador;
import mx.uv.feaa.util.DatabaseManager;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CriadorDAO extends BaseDAOImpl<Criador> implements IGenericDAO<Criador> {
    private static final String TABLA = "criadores";
    private final CaballoDAO caballoDAO = new CaballoDAO();

    @Override
    public Connection obtenerConexion() throws SQLException {
        return DatabaseManager.getConnection();
    }

    @Override
    public Criador obtenerPorId(String id) throws SQLException {
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
                Criador criador = mapearCriador(rs);
                criador.setCaballos(caballoDAO.obtenerPorCriador(id));
                return criador;
            }
            return null;
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    @Override
    public List<Criador> obtenerTodos() throws SQLException {
        String sql = "SELECT * FROM " + TABLA;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Criador> criadores = new ArrayList<>();

        try {
            conn = obtenerConexion();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Criador criador = mapearCriador(rs);
                criador.setCaballos(caballoDAO.obtenerPorCriador(criador.getIdUsuario()));
                criadores.add(criador);
            }
            return criadores;
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    @Override
    public boolean insertar(Criador criador) throws SQLException {
        String sql = "INSERT INTO " + TABLA + " (id, nombre_usuario, email, password, activo, fecha_registro, " +
                "ultimo_acceso, tipo_usuario, licencia_criador, fecha_vigencia_licencia, direccion, " +
                "telefono, nombre_haras, caballos_registrados, caballos_activos) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = obtenerConexion();
            stmt = conn.prepareStatement(sql);

            llenarParametrosInsert(stmt, criador);

            return stmt.executeUpdate() > 0;
        } finally {
            cerrarRecursos(conn, stmt, null);
        }
    }

    @Override
    public boolean actualizar(Criador criador) throws SQLException {
        String sql = "UPDATE " + TABLA + " SET nombre_usuario = ?, email = ?, password = ?, activo = ?, " +
                "ultimo_acceso = ?, licencia_criador = ?, fecha_vigencia_licencia = ?, direccion = ?, " +
                "telefono = ?, nombre_haras = ?, caballos_registrados = ?, caballos_activos = ? " +
                "WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = obtenerConexion();
            stmt = conn.prepareStatement(sql);

            llenarParametrosUpdate(stmt, criador);

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

    private Criador mapearCriador(ResultSet rs) throws SQLException {
        Criador criador = new Criador();
        criador.setIdUsuario(rs.getString("id"));
        criador.setNombreUsuario(rs.getString("nombre_usuario"));
        criador.setEmail(rs.getString("email"));
        criador.setPassword(rs.getString("password"));
        criador.setActivo(rs.getBoolean("activo"));
        criador.setFechaRegistro(rs.getTimestamp("fecha_registro").toLocalDateTime());

        Timestamp ultimoAcceso = rs.getTimestamp("ultimo_acceso");
        if (ultimoAcceso != null) {
            criador.setUltimoAcceso(ultimoAcceso.toLocalDateTime());
        }

        criador.setTipoUsuario(rs.getString("tipo_usuario"));
        criador.setLicenciaCriador(rs.getString("licencia_criador"));
        criador.setFechaVigenciaLicencia(rs.getDate("fecha_vigencia_licencia").toLocalDate());
        criador.setDireccion(rs.getString("direccion"));
        criador.setTelefono(rs.getString("telefono"));
        criador.setNombreHaras(rs.getString("nombre_haras"));
        criador.setCaballosRegistrados(rs.getInt("caballos_registrados"));
        criador.setCaballosActivos(rs.getInt("caballos_activos"));

        return criador;
    }

    private void llenarParametrosInsert(PreparedStatement stmt, Criador criador) throws SQLException {
        stmt.setString(1, criador.getIdUsuario());
        stmt.setString(2, criador.getNombreUsuario());
        stmt.setString(3, criador.getEmail());
        stmt.setString(4, criador.getPassword());
        stmt.setBoolean(5, criador.isActivo());
        stmt.setTimestamp(6, Timestamp.valueOf(criador.getFechaRegistro()));
        stmt.setTimestamp(7, criador.getUltimoAcceso() != null ?
                Timestamp.valueOf(criador.getUltimoAcceso()) : null);
        stmt.setString(8, criador.getTipoUsuario());
        stmt.setString(9, criador.getLicenciaCriador());
        stmt.setDate(10, Date.valueOf(criador.getFechaVigenciaLicencia()));
        stmt.setString(11, criador.getDireccion());
        stmt.setString(12, criador.getTelefono());
        stmt.setString(13, criador.getNombreHaras());
        stmt.setInt(14, criador.getCaballosRegistrados());
        stmt.setInt(15, criador.getCaballosActivos());
    }

    private void llenarParametrosUpdate(PreparedStatement stmt, Criador criador) throws SQLException {
        stmt.setString(1, criador.getNombreUsuario());
        stmt.setString(2, criador.getEmail());
        stmt.setString(3, criador.getPassword());
        stmt.setBoolean(4, criador.isActivo());
        stmt.setTimestamp(5, criador.getUltimoAcceso() != null ?
                Timestamp.valueOf(criador.getUltimoAcceso()) : null);
        stmt.setString(6, criador.getLicenciaCriador());
        stmt.setDate(7, Date.valueOf(criador.getFechaVigenciaLicencia()));
        stmt.setString(8, criador.getDireccion());
        stmt.setString(9, criador.getTelefono());
        stmt.setString(10, criador.getNombreHaras());
        stmt.setInt(11, criador.getCaballosRegistrados());
        stmt.setInt(12, criador.getCaballosActivos());
        stmt.setString(13, criador.getIdUsuario());
    }

    // Métodos adicionales
    public Criador obtenerPorNombreUsuario(String nombreUsuario) throws SQLException {
        String sql = "SELECT * FROM " + TABLA + " WHERE nombre_usuario = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = obtenerConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, nombreUsuario);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Criador criador = mapearCriador(rs);
                criador.setCaballos(caballoDAO.obtenerPorCriador(criador.getIdUsuario()));
                return criador;
            }
            return null;
        } finally {
            cerrarRecursos(conn, stmt, rs);
        }
    }

    public boolean renovarLicencia(String criadorId, LocalDate nuevaFecha) throws SQLException {
        String sql = "UPDATE " + TABLA + " SET fecha_vigencia_licencia = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = obtenerConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setDate(1, Date.valueOf(nuevaFecha));
            stmt.setString(2, criadorId);

            return stmt.executeUpdate() > 0;
        } finally {
            cerrarRecursos(conn, stmt, null);
        }
    }

    public boolean actualizarContadorCaballos(String criadorId) throws SQLException {
        String sql = "UPDATE " + TABLA + " SET caballos_registrados = (SELECT COUNT(*) FROM caballos WHERE criador_id = ?), " +
                "caballos_activos = (SELECT COUNT(*) FROM caballos WHERE criador_id = ? AND " +
                "(ultima_carrera IS NULL OR DATEDIFF(CURRENT_DATE, ultima_carrera) >= 7)) " +
                "WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = obtenerConexion();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, criadorId);
            stmt.setString(2, criadorId);
            stmt.setString(3, criadorId);

            return stmt.executeUpdate() > 0;
        } finally {
            cerrarRecursos(conn, stmt, null);
        }
    }
}