/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package conn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Santo Tomas
 */
public class Conexion {
    //Se debe configurar la BD
    private static final String NAME_DB  = "smuggling_redsocial";
    private static final String USER     = "smuggling";
    private static final String PASSWORD = "181730366u";
    private static final String HOST     = "mysql-smuggling.alwaysdata.net";
    private static final String PORT     = "3306";
    private static final String CONN     = "jdbc:mysql://" + HOST + ":" + PORT + "/" + NAME_DB + "?useSSL=false&serverTimezone=UTC";
    
    public Conexion(){
        try {
            // Cargar el driver (opcional desde JDBC 4.0, pero se puede incluir)
            Class.forName("com.mysql.cj.jdbc.Driver");

        // Crear la conexión
        Connection conexion = DriverManager.getConnection(CONN, USER, PASSWORD);
        System.out.println("✅ Conexión exitosa a MySQL 8.0.33");

        // Cerrar la conexión
        conexion.close();
        } catch (ClassNotFoundException e) {
            System.out.println("❌ No se encontró el driver JDBC");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("❌ Error al conectar con la base de datos");
            e.printStackTrace();
        }
    }
}
