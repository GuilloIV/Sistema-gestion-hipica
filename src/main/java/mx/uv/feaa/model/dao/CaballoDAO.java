package mx.uv.feaa.model.dao;

import mx.uv.feaa.enumeracion.SexoCaballo;
import mx.uv.feaa.model.dto.CaballoDTO;
import mx.uv.feaa.util.DataAccessException;
import mx.uv.feaa.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CaballoDAO {
    /**
     * Busca un caballo por su ID
     */
    public Optional<CaballoDTO> findById(String id) throws SQLException {
        String sql = """
            SELECT id, nombre, fecha_nacimiento, sexo, peso, 
                   pedigri, ultima_carrera, criador_id 
            FROM Caballo 
            WHERE id = ?
        """;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapToCaballoDTO(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Busca todos los caballos de un criador específico
     */
    public List<CaballoDTO> findByCriador(String criadorId) throws SQLException {
        String sql = """
            SELECT id, nombre, fecha_nacimiento, sexo, peso, 
                   pedigri, ultima_carrera, criador_id 
            FROM Caballo 
            WHERE criador_id = ?
            ORDER BY nombre
        """;
        List<CaballoDTO> caballos = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, criadorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    caballos.add(mapToCaballoDTO(rs));
                }
            }
        }
        return caballos;
    }

    /**
     * Busca todos los caballos activos (que han corrido recientemente)
     */
    public List<CaballoDTO> findActiveCaballos() throws SQLException {
        String sql = """
            SELECT id, nombre, fecha_nacimiento, sexo, peso, 
                   pedigri, ultima_carrera, criador_id 
            FROM Caballo 
            WHERE ultima_carrera >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR)
            ORDER BY ultima_carrera DESC
        """;
        List<CaballoDTO> caballos = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    caballos.add(mapToCaballoDTO(rs));
                }
            }
        }
        return caballos;
    }

    /**
     * Busca caballos por nombre (búsqueda parcial)
     */
    public List<CaballoDTO> findByName(String nombre) throws SQLException {
        String sql = """
            SELECT id, nombre, fecha_nacimiento, sexo, peso, 
                   pedigri, ultima_carrera, criador_id 
            FROM Caballo 
            WHERE nombre LIKE ?
            ORDER BY nombre
        """;
        List<CaballoDTO> caballos = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nombre + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    caballos.add(mapToCaballoDTO(rs));
                }
            }
        }
        return caballos;
    }

    /**
     * Busca caballos por sexo
     */
    public List<CaballoDTO> findBySexo(SexoCaballo sexo) throws SQLException {
        String sql = """
            SELECT id, nombre, fecha_nacimiento, sexo, peso, 
                   pedigri, ultima_carrera, criador_id 
            FROM Caballo 
            WHERE sexo = ?
            ORDER BY nombre
        """;
        List<CaballoDTO> caballos = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sexo.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    caballos.add(mapToCaballoDTO(rs));
                }
            }
        }
        return caballos;
    }

    /**
     * Obtiene todos los caballos
     */
    public List<CaballoDTO> findAll() throws SQLException {
        String sql = """
            SELECT id, nombre, fecha_nacimiento, sexo, peso, 
                   pedigri, ultima_carrera, criador_id 
            FROM Caballo 
            ORDER BY nombre
        """;
        List<CaballoDTO> caballos = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    caballos.add(mapToCaballoDTO(rs));
                }
            }
        }
        return caballos;
    }

    /**
     * Guarda o actualiza un caballo
     */
    public void save(CaballoDTO caballo) throws SQLException {
        String sql = """
            INSERT INTO Caballo (id, nombre, fecha_nacimiento, sexo, peso, 
                               pedigri, ultima_carrera, criador_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                nombre = VALUES(nombre),
                fecha_nacimiento = VALUES(fecha_nacimiento),
                sexo = VALUES(sexo),
                peso = VALUES(peso),
                pedigri = VALUES(pedigri),
                ultima_carrera = VALUES(ultima_carrera),
                criador_id = VALUES(criador_id)
        """;

        DatabaseUtil.executeTransaction(() -> {
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, caballo.getId());
                stmt.setString(2, caballo.getNombre());
                stmt.setDate(3, Date.valueOf(caballo.getFechaNacimiento()));
                stmt.setString(4, caballo.getSexo().toString());

                // Manejo de peso nullable
                if (caballo.getPeso() > 0) {
                    stmt.setDouble(5, caballo.getPeso());
                } else {
                    stmt.setNull(5, Types.DECIMAL);
                }

                // Manejo de pedigri nullable
                if (caballo.getPedigri() != null && !caballo.getPedigri().trim().isEmpty()) {
                    stmt.setString(6, caballo.getPedigri());
                } else {
                    stmt.setNull(6, Types.LONGVARCHAR);
                }

                // Manejo de ultima_carrera nullable
                if (caballo.getUltimaCarrera() != null) {
                    stmt.setDate(7, Date.valueOf(caballo.getUltimaCarrera()));
                } else {
                    stmt.setNull(7, Types.DATE);
                }

                // Manejo de criador_id nullable
                if (caballo.getCriadorId() != null && !caballo.getCriadorId().trim().isEmpty()) {
                    stmt.setString(8, caballo.getCriadorId());
                } else {
                    stmt.setNull(8, Types.VARCHAR);
                }

                stmt.executeUpdate();

                // Actualizar contador de caballos del criador si es necesario
                if (caballo.getCriadorId() != null) {
                    updateCriadorCaballosCount(conn, caballo.getCriadorId());
                }

            } catch (SQLException e) {
                throw new DataAccessException("Error guardando caballo: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Actualiza solo la fecha de última carrera
     */
    public void updateUltimaCarrera(String caballoId, Date fechaCarrera) throws SQLException {
        String sql = "UPDATE Caballo SET ultima_carrera = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, fechaCarrera);
            stmt.setString(2, caballoId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DataAccessException("No se encontró el caballo con ID: " + caballoId);
            }
        }
    }

    /**
     * Elimina un caballo por su ID
     */
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM Caballo WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Verifica si existe un caballo con el ID dado
     */
    public boolean exists(String id) throws SQLException {
        String sql = "SELECT 1 FROM Caballo WHERE id = ? LIMIT 1";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Mapea un ResultSet a CaballoDTO
     */
    private CaballoDTO mapToCaballoDTO(ResultSet rs) throws SQLException {
        CaballoDTO dto = new CaballoDTO();
        dto.setId(rs.getString("id"));
        dto.setNombre(rs.getString("nombre"));

        // Manejo seguro de fechas
        Date fechaNacimiento = rs.getDate("fecha_nacimiento");
        if (fechaNacimiento != null) {
            dto.setFechaNacimiento(fechaNacimiento.toLocalDate());
        }

        // Conversión de ENUM desde la base de datos
        String sexoStr = rs.getString("sexo");
        if (sexoStr != null) {
            // Mapeo de valores de BD a enum
            switch (sexoStr.toUpperCase()) {
                case "MACHO":
                    dto.setSexo(SexoCaballo.MACHO);
                    break;
                case "HEMBRA":
                    dto.setSexo(SexoCaballo.HEMBRA);
                    break;
                case "CASTRADO":
                    dto.setSexo(SexoCaballo.CASTRADO);
                    break;
                default:
                    throw new SQLException("Valor de sexo no válido: " + sexoStr);
            }
        }

        // Manejo de peso (puede ser null)
        double peso = rs.getDouble("peso");
        if (!rs.wasNull()) {
            dto.setPeso(peso);
        }

        // Manejo de pedigri (puede ser null)
        dto.setPedigri(rs.getString("pedigri"));

        // Manejo de ultima_carrera (puede ser null)
        Date ultimaCarrera = rs.getDate("ultima_carrera");
        if (ultimaCarrera != null) {
            dto.setUltimaCarrera(ultimaCarrera.toLocalDate());
        }

        // Manejo de criador_id (puede ser null)
        dto.setCriadorId(rs.getString("criador_id"));

        return dto;
    }

    /**
     * Actualiza el contador de caballos de un criador
     */
    private void updateCriadorCaballosCount(Connection conn, String criadorId) throws SQLException {
        String sql = """
            UPDATE Criador 
            SET caballos_registrados = (
                SELECT COUNT(*) FROM Caballo WHERE criador_id = ?
            ),
            caballos_activos = (
                SELECT COUNT(*) FROM Caballo 
                WHERE criador_id = ? 
                AND ultima_carrera >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR)
            )
            WHERE id = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, criadorId);
            stmt.setString(2, criadorId);
            stmt.setString(3, criadorId);
            stmt.executeUpdate();
        }
    }
}