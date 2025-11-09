
package conn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Conexion {
    
    // --- Tus datos de Alwaysdata (Están correctos) ---
    private static final String HOST     = "mysql-softimelody.alwaysdata.net";
    private static final String PORT     = "3306";
    private static final String NAME_DB  = "softimelody_world";
    private static final String USER     = "439733";
    private static final String PASSWORD = "MaxRage1";
    // --- Fin de la configuración ---

    
    private static final String CONN_URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + NAME_DB + "?useSSL=false&serverTimezone=UTC";
    
    private static Connection conexion = null;

   
    private Conexion() {
    }

    public static Connection getConnection() {
        try {
            if (conexion == null || conexion.isClosed()) {
                
                // Cargar el driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Crear la conexión
                conexion = DriverManager.getConnection(CONN_URL, USER, PASSWORD);
                System.out.println("✅ Conexión exitosa a: " + HOST);
            }
            return conexion;
        } catch (ClassNotFoundException e) {
            System.out.println("❌ ERROR: No se encontró el driver JDBC de MySQL.");
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            System.out.println("❌ ERROR al conectar con la base de datos.");
            System.out.println("Verifica host, BD, usuario y contraseña.");
            e.printStackTrace();
            return null;
        }
    }
    
  
    public static void closeConnection() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("🔌 Conexión cerrada.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al cerrar la conexión");
            e.printStackTrace();
        }
    }
}