package mx.uv.feaa.util;


import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {
    public static Connection getConnection() throws SQLException {
        return ConexionBD.getConnection();
    }
}
