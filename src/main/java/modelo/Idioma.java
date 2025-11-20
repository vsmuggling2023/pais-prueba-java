/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

public class Idioma {
    private String countryCode;
    private String language;
    private String isOfficial;
    private double percentage;
    private String totalSpeakers; // Para el reporte de hablantes

    public Idioma() {
    }

    // Constructor principal
    public Idioma(String countryCode, String language, String isOfficial, double percentage) {
        this.countryCode = countryCode;
        this.language = language;
        this.isOfficial = isOfficial;
        this.percentage = percentage;
    }

    // Getters y Setters
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getIsOfficial() { return isOfficial; }
    public void setIsOfficial(String isOfficial) { this.isOfficial = isOfficial; }

    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }

    public String getTotalSpeakers() { return totalSpeakers; }
    public void setTotalSpeakers(String totalSpeakers) { this.totalSpeakers = totalSpeakers; }
}