package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.dto.ApostadorDTO;
import mx.uv.feaa.util.DataAccessException;
import mx.uv.feaa.util.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ApostadorDAO {

    /**
     * Busca un apostador por su ID
     */
    public Optional<ApostadorDTO> findById(String id) throws SQLException {
        String sql = """
            SELECT u.id, u.nombre_usuario, u.email, u.password, u.activo, 
                   u.fecha_registro, u.ultimo_acceso, u.tipo_usuario,
                   a.saldo, a.limite_apuesta, a.total_apostado, a.total_ganado, 
                   a.apuestas_realizadas, a.ultima_actividad
            FROM Usuario u
            INNER JOIN Apostador a ON u.id = a.id
            WHERE u.id = ? AND u.tipo_usuario = 'APOSTADOR'
        """;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToApostadorDTO(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Busca un apostador por nombre de usuario
     */
    public Optional<ApostadorDTO> findByUsername(String nombreUsuario) throws SQLException {
        String sql = """
            SELECT u.id, u.nombre_usuario, u.email, u.password, u.activo, 
                   u.fecha_registro, u.ultimo_acceso, u.tipo_usuario,
                   a.saldo, a.limite_apuesta, a.total_apostado, a.total_ganado, 
                   a.apuestas_realizadas, a.ultima_actividad
            FROM Usuario u
            INNER JOIN Apostador a ON u.id = a.id
            WHERE u.nombre_usuario = ? AND u.tipo_usuario = 'APOSTADOR'
        """;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombreUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToApostadorDTO(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Busca un apostador por email
     */
    public Optional<ApostadorDTO> findByEmail(String email) throws SQLException {
        String sql = """
            SELECT u.id, u.nombre_usuario, u.email, u.password, u.activo, 
                   u.fecha_registro, u.ultimo_acceso, u.tipo_usuario,
                   a.saldo, a.limite_apuesta, a.total_apostado, a.total_ganado, 
                   a.apuestas_realizadas, a.ultima_actividad
            FROM Usuario u
            INNER JOIN Apostador a ON u.id = a.id
            WHERE u.email = ? AND u.tipo_usuario = 'APOSTADOR'
        """;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToApostadorDTO(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Obtiene todos los apostadores activos
     */
    public List<ApostadorDTO> findAllActive() throws SQLException {
        String sql = """
            SELECT u.id, u.nombre_usuario, u.email, u.password, u.activo, 
                   u.fecha_registro, u.ultimo_acceso, u.tipo_usuario,
                   a.saldo, a.limite_apuesta, a.total_apostado, a.total_ganado, 
                   a.apuestas_realizadas, a.ultima_actividad
            FROM Usuario u
            INNER JOIN Apostador a ON u.id = a.id
            WHERE u.activo = TRUE AND u.tipo_usuario = 'APOSTADOR'
            ORDER BY u.fecha_registro DESC
        """;

        List<ApostadorDTO> apostadores = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                apostadores.add(mapToApostadorDTO(rs));
            }
        }
        return apostadores;
    }

    /**
     * Obtiene apostadores con mayor actividad reciente
     */
    public List<ApostadorDTO> findMostActive(int limit) throws SQLException {
        String sql = """
            SELECT u.id, u.nombre_usuario, u.email, u.password, u.activo, 
                   u.fecha_registro, u.ultimo_acceso, u.tipo_usuario,
                   a.saldo, a.limite_apuesta, a.total_apostado, a.total_ganado, 
                   a.apuestas_realizadas, a.ultima_actividad
            FROM Usuario u
            INNER JOIN Apostador a ON u.id = a.id
            WHERE u.activo = TRUE AND u.tipo_usuario = 'APOSTADOR'
            ORDER BY a.ultima_actividad DESC, a.apuestas_realizadas DESC
            LIMIT ?
        """;

        List<ApostadorDTO> apostadores = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    apostadores.add(mapToApostadorDTO(rs));
                }
            }
        }
        return apostadores;
    }

    /**
     * Busca apostadores con saldo mayor a un monto específico
     */
    public List<ApostadorDTO> findBySaldoMinimo(BigDecimal saldoMinimo) throws SQLException {
        String sql = """
            SELECT u.id, u.nombre_usuario, u.email, u.password, u.activo, 
                   u.fecha_registro, u.ultimo_acceso, u.tipo_usuario,
                   a.saldo, a.limite_apuesta, a.total_apostado, a.total_ganado, 
                   a.apuestas_realizadas, a.ultima_actividad
            FROM Usuario u
            INNER JOIN Apostador a ON u.id = a.id
            WHERE a.saldo >= ? AND u.activo = TRUE AND u.tipo_usuario = 'APOSTADOR'
            ORDER BY a.saldo DESC
        """;

        List<ApostadorDTO> apostadores = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, saldoMinimo);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    apostadores.add(mapToApostadorDTO(rs));
                }
            }
        }
        return apostadores;
    }

    /**
     * Guarda o actualiza un apostador (junto con sus datos de usuario)
     */
    public void save(ApostadorDTO apostador) throws SQLException {
        DatabaseUtil.executeTransaction(() -> {
            try (Connection conn = DatabaseUtil.getConnection()) {
                // Primero insertar/actualizar en tabla Usuario
                String sqlUsuario = """
                    INSERT INTO Usuario (id, nombre_usuario, email, password, activo, 
                                       fecha_registro, ultimo_acceso, tipo_usuario)
                    VALUES (?, ?, ?, ?, ?, ?, ?, 'APOSTADOR')
                    ON DUPLICATE KEY UPDATE
                        nombre_usuario = VALUES(nombre_usuario),
                        email = VALUES(email),
                        password = VALUES(password),
                        activo = VALUES(activo),
                        ultimo_acceso = VALUES(ultimo_acceso)
                """;

                try (PreparedStatement stmtUsuario = conn.prepareStatement(sqlUsuario)) {
                    stmtUsuario.setString(1, apostador.getId());
                    stmtUsuario.setString(2, apostador.getNombreUsuario());
                    stmtUsuario.setString(3, apostador.getEmail());
                    stmtUsuario.setString(4, apostador.getPassword());
                    stmtUsuario.setBoolean(5, apostador.isActivo());

                    if (apostador.getFechaRegistro() != null) {
                        stmtUsuario.setTimestamp(6, Timestamp.valueOf(apostador.getFechaRegistro()));
                    } else {
                        stmtUsuario.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
                    }

                    if (apostador.getUltimoAcceso() != null) {
                        stmtUsuario.setTimestamp(7, Timestamp.valueOf(apostador.getUltimoAcceso()));
                    } else {
                        stmtUsuario.setNull(7, Types.TIMESTAMP);
                    }

                    stmtUsuario.executeUpdate();
                }

                // Luego insertar/actualizar en tabla Apostador
                String sqlApostador = """
                    INSERT INTO Apostador (id, saldo, limite_apuesta, total_apostado, 
                                         total_ganado, apuestas_realizadas, ultima_actividad)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    ON DUPLICATE KEY UPDATE
                        saldo = VALUES(saldo),
                        limite_apuesta = VALUES(limite_apuesta),
                        total_apostado = VALUES(total_apostado),
                        total_ganado = VALUES(total_ganado),
                        apuestas_realizadas = VALUES(apuestas_realizadas),
                        ultima_actividad = VALUES(ultima_actividad)
                """;

                try (PreparedStatement stmtApostador = conn.prepareStatement(sqlApostador)) {
                    stmtApostador.setString(1, apostador.getId());
                    stmtApostador.setBigDecimal(2, apostador.getSaldo());
                    stmtApostador.setBigDecimal(3, apostador.getLimiteApuesta());
                    stmtApostador.setBigDecimal(4, apostador.getTotalApostado());
                    stmtApostador.setBigDecimal(5, apostador.getTotalGanado());
                    stmtApostador.setInt(6, apostador.getApuestasRealizadas());

                    if (apostador.getUltimaActividad() != null) {
                        stmtApostador.setDate(7, Date.valueOf(apostador.getUltimaActividad()));
                    } else {
                        stmtApostador.setNull(7, Types.DATE);
                    }

                    stmtApostador.executeUpdate();
                }

            } catch (SQLException e) {
                throw new DataAccessException("Error guardando apostador: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Actualiza el saldo de un apostador
     */
    public void updateSaldo(String apostadorId, BigDecimal nuevoSaldo) throws SQLException {
        String sql = "UPDATE Apostador SET saldo = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, nuevoSaldo);
            stmt.setString(2, apostadorId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DataAccessException("No se encontró el apostador con ID: " + apostadorId);
            }
        }
    }

    /**
     * Actualiza el último acceso de un apostador
     */
    public void updateUltimoAcceso(String apostadorId) throws SQLException {
        String sql = "UPDATE Usuario SET ultimo_acceso = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stmt.setString(2, apostadorId);

            stmt.executeUpdate();
        }
    }

    /**
     * Actualiza la última actividad de apuesta
     */
    public void updateUltimaActividad(String apostadorId) throws SQLException {
        String sql = "UPDATE Apostador SET ultima_actividad = CURDATE() WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, apostadorId);
            stmt.executeUpdate();
        }
    }

    /**
     * Desactiva un apostador
     */
    public void deactivate(String id) throws SQLException {
        String sql = "UPDATE Usuario SET activo = FALSE WHERE id = ? AND tipo_usuario = 'APOSTADOR'";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DataAccessException("No se encontró el apostador con ID: " + id);
            }
        }
    }

    /**
     * Reactiva un apostador
     */
    public void activate(String id) throws SQLException {
        String sql = "UPDATE Usuario SET activo = TRUE WHERE id = ? AND tipo_usuario = 'APOSTADOR'";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DataAccessException("No se encontró el apostador con ID: " + id);
            }
        }
    }

    /**
     * Verifica si existe un apostador con el ID dado
     */
    public boolean exists(String id) throws SQLException {
        String sql = """
            SELECT 1 FROM Usuario u 
            INNER JOIN Apostador a ON u.id = a.id 
            WHERE u.id = ? AND u.tipo_usuario = 'APOSTADOR' 
            LIMIT 1
        """;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Verifica si existe un nombre de usuario
     */
    public boolean existsUsername(String nombreUsuario) throws SQLException {
        String sql = "SELECT 1 FROM Usuario WHERE nombre_usuario = ? LIMIT 1";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombreUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Verifica si existe un email
     */
    public boolean existsEmail(String email) throws SQLException {
        String sql = "SELECT 1 FROM Usuario WHERE email = ? LIMIT 1";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Elimina un apostador (elimina de ambas tablas por CASCADE)
     */
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM Usuario WHERE id = ? AND tipo_usuario = 'APOSTADOR'";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Mapea un ResultSet a ApostadorDTO
     */
    private ApostadorDTO mapToApostadorDTO(ResultSet rs) throws SQLException {
        ApostadorDTO dto = new ApostadorDTO();

        // Campos de Usuario
        dto.setId(rs.getString("id"));
        dto.setNombreUsuario(rs.getString("nombre_usuario"));
        dto.setEmail(rs.getString("email"));
        dto.setPassword(rs.getString("password"));
        dto.setActivo(rs.getBoolean("activo"));
        dto.setTipoUsuario(rs.getString("tipo_usuario"));

        // Manejo de timestamps
        Timestamp fechaRegistro = rs.getTimestamp("fecha_registro");
        if (fechaRegistro != null) {
            dto.setFechaRegistro(fechaRegistro.toLocalDateTime());
        }

        Timestamp ultimoAcceso = rs.getTimestamp("ultimo_acceso");
        if (ultimoAcceso != null) {
            dto.setUltimoAcceso(ultimoAcceso.toLocalDateTime());
        }

        // Campos específicos de Apostador
        dto.setSaldo(rs.getBigDecimal("saldo"));
        dto.setLimiteApuesta(rs.getBigDecimal("limite_apuesta"));
        dto.setTotalApostado(rs.getBigDecimal("total_apostado"));
        dto.setTotalGanado(rs.getBigDecimal("total_ganado"));
        dto.setApuestasRealizadas(rs.getInt("apuestas_realizadas"));

        Date ultimaActividad = rs.getDate("ultima_actividad");
        if (ultimaActividad != null) {
            dto.setUltimaActividad(ultimaActividad.toLocalDate());
        }

        return dto;
    }
}