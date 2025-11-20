/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import conn.Conexion;
import modelo.Idioma;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IdiomasDao {

    // 1. BUSCAR IDIOMAS CON FILTROS
    public List<Idioma> listarIdiomas(String codigo, String nombre, String oficial, String porcentaje) {
        List<Idioma> lista = new ArrayList<>();
        String sql = "SELECT CountryCode, Language, IsOfficial, Percentage FROM countrylanguage";

        ArrayList<String> conditions = new ArrayList<>();
        ArrayList<Object> params = new ArrayList<>();

        // Filtro: Código País
        if (codigo != null && !codigo.isEmpty() && !codigo.equals("Ingresa el código de país")) {
            conditions.add("CountryCode LIKE ?");
            params.add(codigo + "%");
        }
        // Filtro: Idioma
        if (nombre != null && !nombre.isEmpty() && !nombre.equals("Ingresa el idioma")) {
            conditions.add("Language LIKE ?");
            params.add("%" + nombre + "%");
        }
        // Filtro: Es Oficial (T/F)
        if (oficial != null && !oficial.isEmpty() && !oficial.equals("Ingresa T o F (Oficial)")) {
            String val = oficial.substring(0, 1).toUpperCase();
            if (val.equals("T") || val.equals("F")) {
                conditions.add("IsOfficial = ?");
                params.add(val);
            }
        }
        // Filtro: Porcentaje
        if (porcentaje != null && !porcentaje.isEmpty() && !porcentaje.equals("Ingresa el porcentaje")) {
            try {
                conditions.add("Percentage >= ?");
                params.add(Double.parseDouble(porcentaje));
            } catch (NumberFormatException e) {
                /* Ignorar si no es número */ }
        }

        if (!conditions.isEmpty()) {
            sql += " WHERE " + String.join(" AND ", conditions);
        }
        sql += " ORDER BY CountryCode, Language LIMIT 100";

        try (Connection conn = Conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Idioma(
                            rs.getString("CountryCode"),
                            rs.getString("Language"),
                            rs.getString("IsOfficial"),
                            rs.getDouble("Percentage")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar idiomas: " + e.getMessage());
        }
        return lista;
    }

    // 2. AGREGAR IDIOMA
    public boolean agregarIdioma(Idioma idioma) {
        String sql = "INSERT INTO countrylanguage (CountryCode, Language, IsOfficial, Percentage) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, idioma.getCountryCode().toUpperCase());
            ps.setString(2, idioma.getLanguage());
            ps.setString(3, idioma.getIsOfficial().toUpperCase());
            ps.setDouble(4, idioma.getPercentage());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // Aquí podrías lanzar excepciones personalizadas si quieres manejar duplicados en la vista
            System.err.println("Error al agregar idioma: " + e.getMessage());
            return false;
        }
    }

    // 3. MODIFICAR IDIOMA
    public boolean modificarIdioma(Idioma idiomaNuevo, String oldCode, String oldLang) {
        String sql = "UPDATE countrylanguage SET CountryCode = ?, Language = ?, IsOfficial = ?, Percentage = ? "
                + "WHERE CountryCode = ? AND Language = ?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, idiomaNuevo.getCountryCode().toUpperCase());
            ps.setString(2, idiomaNuevo.getLanguage());
            ps.setString(3, idiomaNuevo.getIsOfficial().toUpperCase());
            ps.setDouble(4, idiomaNuevo.getPercentage());

            // Clave original para el WHERE
            ps.setString(5, oldCode);
            ps.setString(6, oldLang);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al modificar idioma: " + e.getMessage());
            return false;
        }
    }

    // 4. REPORTE: IDIOMAS MÁS HABLADOS
    public List<Idioma> listarIdiomasMasHablados() {
        List<Idioma> lista = new ArrayList<>();
        String sql = "SELECT T1.Language, SUM(T2.Population * T1.Percentage / 100) AS TotalSpeakers "
                + "FROM countrylanguage T1 JOIN country T2 ON T1.CountryCode = T2.Code "
                + "GROUP BY T1.Language ORDER BY TotalSpeakers DESC LIMIT 50";

        try (Connection conn = Conexion.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Idioma id = new Idioma();
                id.setLanguage(rs.getString("Language"));
                // Formateamos el número aquí o guardamos el double. 
                // Para el reporte guardaremos el String formateado en el campo auxiliar.
                double total = rs.getDouble("TotalSpeakers");
                id.setTotalSpeakers(String.format("%,.0f", total));
                lista.add(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
