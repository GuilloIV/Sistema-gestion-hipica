package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.entidades.Apuesta;
import mx.uv.feaa.model.entidades.ApuestaGanador;
import mx.uv.feaa.enumeracion.EstadoApuesta;
import mx.uv.feaa.enumeracion.TipoApuesta;
import mx.uv.feaa.model.entidades.ApuestaSeleccion;
import mx.uv.feaa.util.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ApuestaDAO implements IGenericDAO<Apuesta, String> {
    private static final String TABLE_NAME = "Apuesta";
    private static final String SELECT_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE idApuesta = ?";
    private static final String SELECT_ALL = "SELECT * FROM " + TABLE_NAME;
    private static final String INSERT = "INSERT INTO " + TABLE_NAME +
            "(idApuesta, apostador_id, carrera_id, tipoApuesta, montoApostado, fechaApuesta, estado, cuotaAplicada, montoGanado) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE " + TABLE_NAME + " SET " +
            "apostador_id = ?, carrera_id = ?, tipoApuesta = ?, montoApostado = ?, fechaApuesta = ?, " +
            "estado = ?, cuotaAplicada = ?, montoGanado = ? WHERE idApuesta = ?";
    private static final String DELETE = "DELETE FROM " + TABLE_NAME + " WHERE idApuesta = ?";

    @Override
    public Optional<Apuesta> getById(String id) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Apuesta apuesta = null;

        try {
            conn = ConexionBD.getConnection();
            stmt = conn.prepareStatement(SELECT_BY_ID);
            stmt.setString(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                apuesta = mapearApuesta(rs);
            }
        } finally {
            ConexionBD.cerrar(conn, stmt, rs);
        }

        return Optional.ofNullable(apuesta);
    }

    @Override
    public List<Apuesta> getAll() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Apuesta> apuestas = new ArrayList<>();

        try {
            conn = ConexionBD.getConnection();
            stmt = conn.prepareStatement(SELECT_ALL);
            rs = stmt.executeQuery();

            while (rs.next()) {
                apuestas.add(mapearApuesta(rs));
            }
        } finally {
            ConexionBD.cerrar(conn, stmt, rs);
        }

        return apuestas;
    }

    @Override
    public boolean save(Apuesta entity) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean saved = false;

        try {
            conn = ConexionBD.getConnection();
            stmt = conn.prepareStatement(INSERT);

            stmt.setString(1, entity.getId());
            stmt.setString(2, entity.getIdUsuario());
            stmt.setString(3, entity.getIdCarrera());
            stmt.setString(4, entity.getTipoApuesta().name());
            stmt.setDouble(5, entity.getMontoApostado());
            stmt.setTimestamp(6, Timestamp.valueOf(entity.getFechaApuesta()));
            stmt.setString(7, entity.getEstado().name());
            stmt.setDouble(8, entity.getCuotaAplicada());
            stmt.setDouble(9, entity.getMontoGanado());

            saved = stmt.executeUpdate() > 0;
        } finally {
            ConexionBD.cerrar(conn, stmt);
        }

        return saved;
    }

    @Override
    public boolean update(Apuesta entity) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean updated = false;

        try {
            conn = ConexionBD.getConnection();
            stmt = conn.prepareStatement(UPDATE);

            stmt.setString(1, entity.getIdUsuario());
            stmt.setString(2, entity.getIdCarrera());
            stmt.setString(3, entity.getTipoApuesta().name());
            stmt.setDouble(4, entity.getMontoApostado());
            stmt.setTimestamp(5, Timestamp.valueOf(entity.getFechaApuesta()));
            stmt.setString(6, entity.getEstado().name());
            stmt.setDouble(7, entity.getCuotaAplicada());
            stmt.setDouble(8, entity.getMontoGanado());
            stmt.setString(9, entity.getId());

            updated = stmt.executeUpdate() > 0;
        } finally {
            ConexionBD.cerrar(conn, stmt);
        }

        return updated;
    }

    @Override
    public boolean delete(String id) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean deleted = false;

        try {
            conn = ConexionBD.getConnection();
            stmt = conn.prepareStatement(DELETE);
            stmt.setString(1, id);

            deleted = stmt.executeUpdate() > 0;
        } finally {
            ConexionBD.cerrar(conn, stmt);
        }

        return deleted;
    }

    private Apuesta mapearApuesta(ResultSet rs) throws SQLException {
        Apuesta apuesta = new ApuestaGanador(
                rs.getString("idApuesta"),
                rs.getString("apostador_id"),
                rs.getString("carrera_id"),
                TipoApuesta.valueOf(rs.getString("tipoApuesta")),
                rs.getDouble("montoApostado")
        );

        apuesta.setFechaApuesta(rs.getTimestamp("fechaApuesta").toLocalDateTime());
        apuesta.setEstado(EstadoApuesta.valueOf(rs.getString("estado")));
        apuesta.setCuotaAplicada(rs.getDouble("cuotaAplicada"));
        apuesta.setMontoGanado(rs.getDouble("montoGanado"));

        return apuesta;
    }

    public List<Apuesta> getByApostadorId(String apostadorId) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Apuesta> apuestas = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE apostador_id = ?";

        try {
            conn = ConexionBD.getConnection();
            stmt = conn.prepareStatement(query);
            stmt.setString(1, apostadorId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                apuestas.add(mapearApuesta(rs));
            }
        } finally {
            ConexionBD.cerrar(conn, stmt, rs);
        }

        return apuestas;
    }

    public List<Apuesta> getByEstado(EstadoApuesta estado) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Apuesta> apuestas = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE estado = ?";

        try {
            conn = ConexionBD.getConnection();
            stmt = conn.prepareStatement(query);
            stmt.setString(1, estado.name());
            rs = stmt.executeQuery();

            while (rs.next()) {
                apuestas.add(mapearApuesta(rs));
            }
        } finally {
            ConexionBD.cerrar(conn, stmt, rs);
        }

        return apuestas;
    }
    public boolean saveWithSelections(Apuesta apuesta, List<ApuestaSeleccion> selecciones) throws SQLException {
        Connection conn = null;
        try {
            conn = ConexionBD.getConnection();
            conn.setAutoCommit(false);  // Iniciar transacción

            // 1. Guardar la apuesta principal
            if (!save(apuesta)) {
                conn.rollback();
                return false;
            }

            // 2. Guardar las selecciones
            ApuestaSeleccionDAO seleccionDAO = new ApuestaSeleccionDAO();
            for (ApuestaSeleccion seleccion : selecciones) {
                if (!seleccionDAO.save(seleccion)) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);  // Restaurar auto-commit
                conn.close();
            }
        }
    }

    public boolean updateWithSelections(Apuesta apuesta, List<ApuestaSeleccion> selecciones) throws SQLException {
        Connection conn = null;
        try {
            conn = ConexionBD.getConnection();
            conn.setAutoCommit(false);

            // 1. Actualizar apuesta principal
            if (!update(apuesta)) {
                conn.rollback();
                return false;
            }

            // 2. Eliminar selecciones antiguas
            ApuestaSeleccionDAO seleccionDAO = new ApuestaSeleccionDAO();
            if (!seleccionDAO.deleteByApuestaId(apuesta.getId())) {
                conn.rollback();
                return false;
            }

            // 3. Guardar nuevas selecciones
            for (ApuestaSeleccion seleccion : selecciones) {
                if (!seleccionDAO.save(seleccion)) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
}