/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import conn.Conexion;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Santo Tomas
 */
public class LoginDao {

    /**
     * Método privado para encriptar la contraseña con SHA-256. Este método
     * estaba antes en la Vista como clase estática 'Seguridad'.
     */
    private String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Error al encriptar contraseña", ex);
        }
    }

    /**
     * Valida si el usuario y contraseña son correctos.
     *
     * @param usuario El nombre de usuario ingresado.
     * @param password La contraseña en texto plano (se encriptará aquí dentro).
     * @return true si las credenciales son válidas, false si no.
     */
    public boolean autenticar(String usuario, String password) {
        String passHash = sha256(password);
        String sql = "SELECT * FROM usuarios WHERE usuario = ? AND contrasena_hash = ?";

        // Usamos try-with-resources para asegurar que se cierren los recursos
        try (Connection conn = Conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setString(2, passHash);

            try (ResultSet rs = ps.executeQuery()) {
                // Si rs.next() es true, significa que encontró un registro
                return rs.next();
            }

        } catch (SQLException e) {
            System.out.println("Error SQL en LoginDao: " + e.getMessage());
            return false;
        }
    }
}
