/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

public class Ciudad {

    private int id;
    private String name;
    private String countryCode;
    private String district;
    private int population;

    // Campos auxiliares para reportes (JOINs)
    private String countryName;
    private String continent;

    public Ciudad() {
    }

    // Constructor principal para la tabla normal
    public Ciudad(int id, String name, String district, String countryCode, int population) {
        this.id = id;
        this.name = name;
        this.district = district;
        this.countryCode = countryCode;
        this.population = population;
    }

    // Constructor para insertar (sin ID, ya que es auto-incremental)
    public Ciudad(String name, String district, String countryCode, int population) {
        this.name = name;
        this.district = district;
        this.countryCode = countryCode;
        this.population = population;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }
}
