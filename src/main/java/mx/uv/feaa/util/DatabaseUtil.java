package mx.uv.feaa.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseUtil {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/gestionhipica";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Gur0v_D@t@b@s3";
    private static Connection connection = null;

    static {
        try {
            // Cargar driver dinámicamente
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Error cargando driver JDBC", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            Properties connectionProps = new Properties();
            connectionProps.put("user", DB_USER);
            connectionProps.put("password", DB_PASSWORD);
            connectionProps.put("useSSL", "false");
            connectionProps.put("autoReconnect", "true");
            connectionProps.put("characterEncoding", "UTF-8");
            connectionProps.put("useUnicode", "true");

            connection = DriverManager.getConnection(DB_URL, connectionProps);
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error cerrando conexión: " + e.getMessage());
        }
    }

    // Método para ejecutar transacciones
    public static void executeTransaction(Runnable dbOperation) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);  // Iniciar transacción

            dbOperation.run();

            conn.commit();
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error en rollback: " + ex.getMessage());
            }
            throw new DataAccessException("Error en transacción", e);
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error restableciendo auto-commit: " + e.getMessage());
            }
        }
    }
}

