/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import conn.Conexion;
import modelo.Pais;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;     // <-- Faltaba este import
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;  // <-- Faltaba este import
import java.util.List;

public class PaisesDao {

    // 1. Método para BUSCAR países
    public List<Pais> listarPaises(String codigo, String nombre, String continente, String poblacion) {
        List<Pais> lista = new ArrayList<>();
        String sql = "SELECT Code, Name, Continent, Population FROM country";

        ArrayList<String> conditions = new ArrayList<>();
        ArrayList<Object> params = new ArrayList<>();

        if (codigo != null && !codigo.isEmpty() && !codigo.equals("Ingresa el codigo")) {
            conditions.add("Code LIKE ?");
            params.add(codigo + "%");
        }
        if (nombre != null && !nombre.isEmpty() && !nombre.equals("Ingresa el nombre")) {
            conditions.add("Name LIKE ?");
            params.add("%" + nombre + "%");
        }
        if (continente != null && !continente.isEmpty() && !continente.equals("Ingresa el continente")) {
            conditions.add("Continent LIKE ?");
            params.add("%" + continente + "%");
        }
        if (poblacion != null && !poblacion.isEmpty() && !poblacion.equals("Ingresa la población")) {
            conditions.add("Population >= ?");
            try {
                params.add(Integer.parseInt(poblacion));
            } catch (NumberFormatException e) {
            }
        }

        if (!conditions.isEmpty()) {
            sql += " WHERE " + String.join(" AND ", conditions);
        }
        sql += " LIMIT 100";

        try (Connection conn = Conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pais p = new Pais(
                            rs.getString("Code"),
                            rs.getString("Name"),
                            rs.getString("Continent"),
                            String.valueOf(rs.getInt("Population"))
                    );
                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar países: " + e.getMessage());
        }
        return lista;
    }

    // 2. Método para AGREGAR un país (CORREGIDO: usa getCode, getName...)
    public boolean agregarPais(Pais pais) {
        String sql = "INSERT INTO country (Code, Name, Continent, Population) VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            // AQUÍ ESTABA EL ERROR: Usamos los getters en Inglés ahora
            ps.setString(1, pais.getCode());       // Antes: getCodigo()
            ps.setString(2, pais.getName());       // Antes: getNombre()
            ps.setString(3, pais.getContinent());  // Antes: getContinente()

            // Validación de población
            String pobTexto = pais.getPopulation().replace(".", "").replace(",", "").trim();
            if (pobTexto.isEmpty()) {
                ps.setInt(4, 0);
            } else {
                ps.setInt(4, Integer.parseInt(pobTexto));
            }

            return ps.executeUpdate() > 0;

        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error al agregar país: " + e.getMessage());
            return false;
        }
    }

    // 3. Método para MODIFICAR un país (CORREGIDO: usa getCode, getName...)
    public boolean modificarPais(Pais pais, String codigoOriginal) {
        String sql = "UPDATE country SET Code = ?, Name = ?, Continent = ?, Population = ? WHERE Code = ?";

        try (Connection conn = Conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            // AQUÍ TAMBIÉN CORREGIMOS
            ps.setString(1, pais.getCode());
            ps.setString(2, pais.getName());
            ps.setString(3, pais.getContinent());

            String pobTexto = pais.getPopulation().replace(".", "").replace(",", "").trim();
            if (pobTexto.isEmpty()) {
                ps.setInt(4, 0);
            } else {
                ps.setInt(4, Integer.parseInt(pobTexto));
            }

            ps.setString(5, codigoOriginal);

            return ps.executeUpdate() > 0;

        } catch (SQLException | NumberFormatException e) {
            System.err.println("Error al modificar país: " + e.getMessage());
            return false;
        }
    }

    // 4. Método para obtener detalles completos
    public Pais obtenerDetallesPais(String codigoPais) {
        Pais p = null;
        String sql = "SELECT T1.*, T2.Name AS CapitalName "
                + "FROM country T1 "
                + "LEFT JOIN city T2 ON T1.Capital = T2.ID "
                + "WHERE T1.Code = ?";

        try (Connection conn = Conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, codigoPais);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    p = new Pais();
                    p.setCode(rs.getString("Code"));
                    p.setName(rs.getString("Name"));
                    p.setContinent(rs.getString("Continent"));
                    p.setRegion(rs.getString("Region"));
                    p.setSurfaceArea(String.format("%,.2f", rs.getDouble("SurfaceArea")));

                    Object indep = rs.getObject("IndepYear");
                    p.setIndepYear(indep != null ? indep.toString() : "N/A");

                    p.setPopulation(String.format("%,d", rs.getInt("Population")));

                    double lifeExp = rs.getDouble("LifeExpectancy");
                    p.setLifeExpectancy(rs.wasNull() ? "N/A" : String.format("%,.1f", lifeExp));

                    p.setGnp(String.format("%,.2f", rs.getDouble("GNP")));
                    p.setGovernmentForm(rs.getString("GovernmentForm"));
                    p.setHeadOfState(rs.getString("HeadOfState"));
                    p.setCapitalName(rs.getString("CapitalName"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener detalles: " + e.getMessage());
        }
        return p;
    }

    // 5. Listar por independencia
    public List<Pais> listarPorIndependencia() {
        List<Pais> lista = new ArrayList<>();
        String sql = "SELECT Code, Name, Continent, Population, IndepYear FROM country ORDER BY IndepYear DESC, Name ASC";

        try (Connection conn = Conexion.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Pais p = new Pais(
                        rs.getString("Code"),
                        rs.getString("Name"),
                        rs.getString("Continent"),
                        String.valueOf(rs.getInt("Population"))
                );
                Object indep = rs.getObject("IndepYear");
                p.setIndepYear(indep != null ? indep.toString() : "N/A");
                lista.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // 6. Listar capitales
    public List<String[]> listarCapitalesPorContinente() {
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT T1.Continent, T1.Name AS CountryName, T2.Name AS CapitalName "
                + "FROM country T1 JOIN city T2 ON T1.Capital = T2.ID "
                + "ORDER BY T1.Continent ASC, T1.Name ASC";

        try (Connection conn = Conexion.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new String[]{
                    rs.getString("Continent"),
                    rs.getString("CountryName"),
                    rs.getString("CapitalName")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // 7. Comparar países
    public List<Pais> obtenerPaisesPorCodigos(List<String> codigos) {
        List<Pais> lista = new ArrayList<>();
        if (codigos == null || codigos.isEmpty()) {
            return lista;
        }

        String placeholders = String.join(",", Collections.nCopies(codigos.size(), "?"));
        String sql = "SELECT T1.*, T2.Name AS CapitalName "
                + "FROM country T1 LEFT JOIN city T2 ON T1.Capital = T2.ID "
                + "WHERE T1.Code IN (" + placeholders + ") ORDER BY T1.Name";

        try (Connection conn = Conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < codigos.size(); i++) {
                ps.setString(i + 1, codigos.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pais p = new Pais();
                    p.setName(rs.getString("Name"));
                    p.setContinent(rs.getString("Continent"));
                    p.setRegion(rs.getString("Region"));
                    p.setSurfaceArea(String.format("%,.2f", rs.getDouble("SurfaceArea")));
                    Object indep = rs.getObject("IndepYear");
                    p.setIndepYear(indep != null ? indep.toString() : "N/A");
                    p.setPopulation(String.format("%,d", rs.getInt("Population")));
                    double lifeExp = rs.getDouble("LifeExpectancy");
                    p.setLifeExpectancy(rs.wasNull() ? "N/A" : String.format("%,.1f", lifeExp));
                    p.setGnp(String.format("%,.2f", rs.getDouble("GNP")));
                    p.setGovernmentForm(rs.getString("GovernmentForm"));
                    p.setHeadOfState(rs.getString("HeadOfState"));
                    p.setCapitalName(rs.getString("CapitalName"));
                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
