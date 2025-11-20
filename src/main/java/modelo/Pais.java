/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

public class Pais {

    // Todos tus atributos actuales (String para simplificar, como lo tenías)
    private String code;
    private String name;
    private String continent;
    private String region;
    private String surfaceArea;
    private String indepYear;
    private String population;
    private String lifeExpectancy;
    private String gnp;
    private String governmentForm;
    private String headOfState;
    private String capitalName; // Guardaremos el nombre, no el ID

    public Pais() {
    }

    // Constructor simple para la tabla principal
    public Pais(String code, String name, String continent, String population) {
        this.code = code;
        this.name = name;
        this.continent = continent;
        this.population = population;
    }

    // Getters y Setters para TODO (necesarios para el DAO)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSurfaceArea() {
        return surfaceArea;
    }

    public void setSurfaceArea(String surfaceArea) {
        this.surfaceArea = surfaceArea;
    }

    public String getIndepYear() {
        return indepYear;
    }

    public void setIndepYear(String indepYear) {
        this.indepYear = indepYear;
    }

    public String getPopulation() {
        return population;
    }

    public void setPopulation(String population) {
        this.population = population;
    }

    public String getLifeExpectancy() {
        return lifeExpectancy;
    }

    public void setLifeExpectancy(String lifeExpectancy) {
        this.lifeExpectancy = lifeExpectancy;
    }

    public String getGnp() {
        return gnp;
    }

    public void setGnp(String gnp) {
        this.gnp = gnp;
    }

    public String getGovernmentForm() {
        return governmentForm;
    }

    public void setGovernmentForm(String governmentForm) {
        this.governmentForm = governmentForm;
    }

    public String getHeadOfState() {
        return headOfState;
    }

    public void setHeadOfState(String headOfState) {
        this.headOfState = headOfState;
    }

    public String getCapitalName() {
        return capitalName;
    }

    public void setCapitalName(String capitalName) {
        this.capitalName = capitalName;
    }
}
