package mx.uv.feaa.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ConexionBD {
    private static final Logger LOGGER = Logger.getLogger(ConexionBD.class.getName());

    // Constantes para nombres de propiedades
    private static final String PROP_URL = "jdbc.url";
    private static final String PROP_USER = "jdbc.user";
    private static final String PROP_PASSWORD = "jdbc.password";
    private static final String CONFIG_FILE = "database.properties";

    // Variables de conexión (final ya que no deberían cambiar después de la inicialización)
    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    // Connection es volatile para acceso seguro entre hilos
    private static volatile Connection connection;

    // Bloque estático para inicialización
    static {
        Properties props = cargarConfiguracion();
        URL = props.getProperty(PROP_URL);
        USER = props.getProperty(PROP_USER);
        PASSWORD = props.getProperty(PROP_PASSWORD);

        // Validación básica de configuración
        if (URL == null || USER == null || PASSWORD == null) {
            throw new RuntimeException("Configuración de BD incompleta en " + CONFIG_FILE);
        }
    }

    private ConexionBD() {
        // Constructor privado para evitar instanciación
    }

    private static Properties cargarConfiguracion() {
        Properties properties = new Properties();

        try (InputStream input = ConexionBD.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new RuntimeException("No se encontró el archivo " + CONFIG_FILE + " en el classpath");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar configuración de base de datos", e);
        }

        return properties;
    }

    public static Connection getConnection() throws SQLException {
        // Doble verificación para singleton thread-safe
        Connection conn = connection;
        if (conn == null || conn.isClosed()) {
            synchronized (ConexionBD.class) {
                conn = connection;
                if (conn == null || conn.isClosed()) {
                    connection = DriverManager.getConnection(URL, USER, PASSWORD);
                    conn = connection;
                }
            }
        }
        return conn;
    }

    public static void closeConnection() {
        synchronized (ConexionBD.class) {
            cerrar(connection);
            connection = null;
        }
    }

    public static void cerrar(AutoCloseable... recursos) {
        for (AutoCloseable recurso : recursos) {
            if (recurso != null) {
                try {
                    recurso.close();
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Error al cerrar recurso: " + recurso.getClass().getName(), e);
                }
            }
        }
    }

    public static boolean verificarConexion() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Consulta simple para verificar que la conexión funciona
            stmt.execute("SELECT 1");
            return true;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error verificando conexión a BD", e);
            return false;
        }
    }

    // Método para ejecutar transacciones
    public static void ejecutarTransaccion(Transaccion transaccion) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            transaccion.ejecutar(conn);
            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                cerrar(conn);
            }
        }
    }

    @FunctionalInterface
    public interface Transaccion {
        void ejecutar(Connection conn) throws SQLException;
    }
}