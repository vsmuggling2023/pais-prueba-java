/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import conn.Conexion;
import modelo.Ciudad;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CuidadesDao {

    // 1. BUSCAR CIUDADES CON FILTROS
    public List<Ciudad> listarCiudades(String idStr, String nombre, String distrito, String codigoPais, String poblacionStr) {
        List<Ciudad> lista = new ArrayList<>();
        String sql = "SELECT ID, Name, District, CountryCode, Population FROM city";
        
        ArrayList<String> conditions = new ArrayList<>();
        ArrayList<Object> params = new ArrayList<>();

        // Filtro ID
        if (idStr != null && !idStr.isEmpty() && !idStr.equals("Ingresa el ID")) {
            try {
                conditions.add("ID = ?");
                params.add(Integer.parseInt(idStr));
            } catch (NumberFormatException e) {}
        }
        // Filtro Nombre
        if (nombre != null && !nombre.isEmpty() && !nombre.equals("Ingresa el nombre")) {
            conditions.add("Name LIKE ?");
            params.add("%" + nombre + "%");
        }
        // Filtro Distrito
        if (distrito != null && !distrito.isEmpty() && !distrito.equals("Ingresa el Distrito")) {
            conditions.add("District LIKE ?");
            params.add("%" + distrito + "%");
        }
        // Filtro Código País
        if (codigoPais != null && !codigoPais.isEmpty() && !codigoPais.equals("Ingresa el código de pais")) {
            conditions.add("CountryCode LIKE ?");
            params.add(codigoPais + "%");
        }
        // Filtro Población
        if (poblacionStr != null && !poblacionStr.isEmpty() && !poblacionStr.equals("Ingresa la población")) {
            try {
                conditions.add("Population >= ?");
                params.add(Integer.parseInt(poblacionStr));
            } catch (NumberFormatException e) {}
        }

        if (!conditions.isEmpty()) {
            sql += " WHERE " + String.join(" AND ", conditions);
        }
        sql += " LIMIT 100";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Ciudad(
                        rs.getInt("ID"),
                        rs.getString("Name"),
                        rs.getString("District"),
                        rs.getString("CountryCode"),
                        rs.getInt("Population")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar ciudades: " + e.getMessage());
        }
        return lista;
    }

    // 2. AGREGAR CIUDAD
    // Nota: El ID es autoincremental, no se pasa en el INSERT
    public boolean agregarCiudad(Ciudad ciudad) {
        String sql = "INSERT INTO city (Name, CountryCode, Population, District) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ciudad.getName());
            ps.setString(2, ciudad.getCountryCode());
            ps.setInt(3, ciudad.getPopulation());
            // Asignamos un valor por defecto al distrito si viene vacío para evitar errores
            String dist = (ciudad.getDistrict() == null || ciudad.getDistrict().isEmpty()) ? "-" : ciudad.getDistrict();
            ps.setString(4, dist);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al agregar ciudad: " + e.getMessage());
            return false;
        }
    }

    // 3. MODIFICAR CIUDAD
    public boolean modificarCiudad(Ciudad ciudadNueva, int idOriginal) {
        // Permitimos cambiar el ID también, como estaba en tu lógica original
        String sql = "UPDATE city SET ID = ?, Name = ?, CountryCode = ?, District = ?, Population = ? WHERE ID = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ciudadNueva.getId());
            ps.setString(2, ciudadNueva.getName());
            ps.setString(3, ciudadNueva.getCountryCode());
            ps.setString(4, ciudadNueva.getDistrict());
            ps.setInt(5, ciudadNueva.getPopulation());
            ps.setInt(6, idOriginal);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al modificar ciudad: " + e.getMessage());
            // Podrías lanzar excepciones específicas aquí si lo deseas
            return false;
        }
    }

    // 4. REPORTE: CIUDADES MÁS POBLADAS (JOIN con País)
    public List<Ciudad> listarCiudadesMasPobladas() {
        List<Ciudad> lista = new ArrayList<>();
        String sql = "SELECT T1.ID, T1.Name AS CityName, T1.Population, "
                   + "T2.Name AS CountryName, T2.Continent "
                   + "FROM city T1 JOIN country T2 ON T1.CountryCode = T2.Code "
                   + "ORDER BY T1.Population DESC LIMIT 50";

        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Ciudad c = new Ciudad();
                c.setId(rs.getInt("ID"));
                c.setName(rs.getString("CityName"));
                c.setPopulation(rs.getInt("Population"));
                // Guardamos nombre país y continente en los campos auxiliares
                c.setCountryName(rs.getString("CountryName"));
                c.setContinent(rs.getString("Continent"));
                
                lista.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}