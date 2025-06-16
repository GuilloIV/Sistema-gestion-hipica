package mx.uv.feaa.model.dao;

import mx.uv.feaa.model.dto.CriadorDTO;
import mx.uv.feaa.util.DataAccessException;
import mx.uv.feaa.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CriadorDAO {

    public Optional<CriadorDTO> findById(String id) throws SQLException {
        String sql = """
            SELECT u.id, u.nombre_usuario, u.email, u.password, u.activo,
                   u.fecha_registro, u.ultimo_acceso, u.tipo_usuario,
                   c.numero_ganado, c.numero_perdido
            FROM Usuario u
            INNER JOIN Criador c ON u.id = c.id
            WHERE u.id = ? AND u.tipo_usuario = 'CRIADOR'
        """;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToCriadorDTO(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<CriadorDTO> findAllActive() throws SQLException {
        String sql = """
            SELECT u.id, u.nombre_usuario, u.email, u.password, u.activo,
                   u.fecha_registro, u.ultimo_acceso, u.tipo_usuario,
                   c.numero_ganado, c.numero_perdido
            FROM Usuario u
            INNER JOIN Criador c ON u.id = c.id
            WHERE u.activo = TRUE AND u.tipo_usuario = 'CRIADOR'
            ORDER BY u.fecha_registro DESC
        """;

        List<CriadorDTO> criadores = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                criadores.add(mapToCriadorDTO(rs));
            }
        }
        return criadores;
    }

    public void save(CriadorDTO criador) throws SQLException {
        DatabaseUtil.executeTransaction(() -> {
            try (Connection conn = DatabaseUtil.getConnection()) {
                // Guardar en Usuario
                String sqlUsuario = """
                    INSERT INTO Usuario (id, nombre_usuario, email, password, activo, 
                                         fecha_registro, ultimo_acceso, tipo_usuario)
                    VALUES (?, ?, ?, ?, ?, ?, ?, 'CRIADOR')
                    ON DUPLICATE KEY UPDATE
                        nombre_usuario = VALUES(nombre_usuario),
                        email = VALUES(email),
                        password = VALUES(password),
                        activo = VALUES(activo),
                        ultimo_acceso = VALUES(ultimo_acceso)
                """;

                try (PreparedStatement stmtUsuario = conn.prepareStatement(sqlUsuario)) {
                    stmtUsuario.setString(1, criador.getId());
                    stmtUsuario.setString(2, criador.getNombreUsuario());
                    stmtUsuario.setString(3, criador.getEmail());
                    stmtUsuario.setString(4, criador.getPassword());
                    stmtUsuario.setBoolean(5, criador.isActivo());

                    if (criador.getFechaRegistro() != null) {
                        stmtUsuario.setTimestamp(6, Timestamp.valueOf(criador.getFechaRegistro()));
                    } else {
                        stmtUsuario.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
                    }

                    if (criador.getUltimoAcceso() != null) {
                        stmtUsuario.setTimestamp(7, Timestamp.valueOf(criador.getUltimoAcceso()));
                    } else {
                        stmtUsuario.setNull(7, Types.TIMESTAMP);
                    }

                    stmtUsuario.executeUpdate();
                }

                // Guardar en Criador
                String sqlCriador = """
                    INSERT INTO Criador (id, numero_ganado, numero_perdido)
                    VALUES (?, ?, ?)
                    ON DUPLICATE KEY UPDATE
                        numero_ganado = VALUES(numero_ganado),
                        numero_perdido = VALUES(numero_perdido)
                """;

                try (PreparedStatement stmtCriador = conn.prepareStatement(sqlCriador)) {
                    stmtCriador.setString(1, criador.getId());
                    stmtCriador.setInt(2, criador.getNombreHaras());
                    stmtCriador.setInt(3, criador.getNumeroPerdido());
                    stmtCriador.executeUpdate();
                }

            } catch (SQLException e) {
                throw new DataAccessException("Error guardando criador: " + e.getMessage(), e);
            }
        });
    }

    public void deleteById(String id) throws SQLException {
        String sqlCriador = "DELETE FROM Criador WHERE id = ?";
        String sqlUsuario = "DELETE FROM Usuario WHERE id = ? AND tipo_usuario = 'CRIADOR'";
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (
                    PreparedStatement stmt1 = conn.prepareStatement(sqlCriador);
                    PreparedStatement stmt2 = conn.prepareStatement(sqlUsuario)
            ) {
                stmt1.setString(1, id);
                stmt1.executeUpdate();
                stmt2.setString(1, id);
                stmt2.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private CriadorDTO mapToCriadorDTO(ResultSet rs) throws SQLException {
        CriadorDTO dto = new CriadorDTO();
        dto.setId(rs.getString("id"));
        dto.setNombreUsuario(rs.getString("nombre_usuario"));
        dto.setEmail(rs.getString("email"));
        dto.setPassword(rs.getString("password"));
        dto.setActivo(rs.getBoolean("activo"));
        dto.setFechaRegistro(rs.getTimestamp("fecha_registro").toLocalDateTime());
        Timestamp ultimoAcceso = rs.getTimestamp("ultimo_acceso");
        dto.setUltimoAcceso(ultimoAcceso != null ? ultimoAcceso.toLocalDateTime() : null);
        dto.setNumeroGanado(rs.getInt("numero_ganado"));
        dto.setNumeroPerdido(rs.getInt("numero_perdido"));
        return dto;
    }
}

